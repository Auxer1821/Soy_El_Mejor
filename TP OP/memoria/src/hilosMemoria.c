#include "../include/hilosMemoria.h"


void* funcion_De_Kernel (void *element){
    t_data_hilo* data_hilo = (t_data_hilo*) element;
    t_memoria* memoria = data_hilo->memoria;
    int socket = data_hilo->socket;
    free(element);

	t_log* logger = iniciar_logger("./logs-memoria-kernel.log", "Log-memoria-kernel");
	log_debug(logger, "Hilo Memoria-Kernel");


	//Realizamos el handshake con Kernel
    /*
	log_debug (logger, "Iniciando el handshake Kernel");
	handshake_servidor (logger, conexion_kernel, &handshake_Kernel, &resultOk, &resultError, 1);
	log_debug (logger, "Handshake terminado Kernel");
    */

    t_paquete* paquete = malloc(sizeof(t_paquete));
    paquete->buffer = malloc(sizeof(t_buffer));

    while (1)
    {

        // Primero recibimos el codigo de operacion
        recv(socket, &(paquete->codigo_de_op), sizeof(int), 0);

        // Después ya podemos recibir el buffer. Primero su tamaño seguido del contenido
        recv(socket, &(paquete->buffer->size), sizeof(int), 0);
        paquete->buffer->stream = malloc(paquete->buffer->size);
        recv(socket, paquete->buffer->stream, paquete->buffer->size, 0);

        switch (paquete->codigo_de_op)
        {
        case NEW_PROCESS://int PID
            log_debug(logger, "NEW_PROCESS");
            int pid = *deserializar_int(paquete->buffer);
            log_debug(logger, "Creación de Proceso PID: %d",pid);
            
            lock_mutex(memoria->mutex,logger);

            inicializar_tabla_de_segmentos(socket, memoria->cant_max_segmentos, pid, memoria );
            
            unlock_mutex(memoria->mutex,logger);
            break;

        case CREATE_SEGMENT:
            log_debug(logger, "CREATE_SEGMENT");
            t_Pet_Segmento* peticion = deserializar_Pet_de_Segmento(paquete -> buffer);
            
            lock_mutex(memoria->mutex,logger);

            crear_segmento(logger , socket,  peticion->ID, peticion->tamanio, peticion->PID, memoria, memoria->algoritmo);

            unlock_mutex(memoria->mutex,logger);
            break;
            

        case DELETE_SEGMENT: // 1er Int: PID, 2do Int: ID del segmento a eliminar
            log_debug(logger, "DELETE_SEGMENT");
            t_Par_de_Ints* data = deserializar_Par_de_Ints(paquete -> buffer);
            
            lock_mutex(memoria->mutex,logger);
            log_debug(logger, "Eliminando Segmento");
            t_Par_de_Ints base_tamanio = eliminar_segmento(socket, data->data1, data->data2, memoria);

            log_info(logger,"PID: %d - Eliminar Segmento: %d - Base: %d - TAMAÑO: %d", data->data1,  data->data2, base_tamanio.data1, base_tamanio.data2);
            
            
            unlock_mutex(memoria->mutex,logger);
            break;

        case TERMINATE_PROCESS:	//tanto el envio de la creacion del segmento como las respuesta las encapsule en la misma poroque hacen lo mismo
            log_debug(logger, "TERMINATE_PROCESS");
            int PID = *deserializar_int(paquete->buffer);
            log_info(logger, "Eliminación de Proceso PID: %d", PID);
            lock_mutex(memoria->mutex,logger);
            eliminar_tabla_segmento(PID, memoria);
            enviar_int_tradicional(socket, 0);
            unlock_mutex(memoria->mutex, logger);
            break;

        default:
            break;
        }

        free(paquete->buffer->stream);
    }
    
}


void* funcion_De_CPU(void * element){

	t_data_hilo* data_hilo = (t_data_hilo*) element;
	t_memoria* memoria = data_hilo->memoria;
    int socket = data_hilo->socket;
    free(element);

	t_log* logger = iniciar_logger("./logs-memoria-cpu.log", "Logs-memoria-cpu");
	log_debug(logger, "Hilo Memoria-CPU");


    
	//Realizamos el handshake con CPU
    /*
	log_debug (logger, "Iniciando el handshake CPU");
	handshake_servidor (logger, conexion_cpu, &handshake_CPU, &resultOk, &resultError, 4);
	log_debug (logger, "Handshake terminado CPU");
    */

    t_paquete* paquete = malloc(sizeof(t_paquete));
    paquete->buffer = malloc(sizeof(t_buffer));

    while (1)
    {

        // Primero recibimos el codigo de operacion
        recv(socket, &(paquete->codigo_de_op), sizeof(int), 0);

        // Después ya podemos recibir el buffer. Primero su tamaño seguido del contenido
        recv(socket, &(paquete->buffer->size), sizeof(int), 0);
        paquete->buffer->stream = malloc(paquete->buffer->size);
        recv(socket, paquete->buffer->stream, paquete->buffer->size, 0);
        
        enviar_bool(socket,true);
        int PID = recibir_int(socket);

        switch (paquete->codigo_de_op)
        {
        case MOV_IN:
            log_debug(logger, "MOV_IN");
            //Recibe la direccion fisica del segmento y el tamaño a leer
            t_Par_de_Ints* conjunto = deserializar_Par_de_Ints(paquete->buffer);
            int direccion_fisica = conjunto->data1;
            int tamanio_a_leer = conjunto->data2;

            lock_mutex(memoria->mutex,logger);

            //Extraigo contenido de memoria
            char* contenido = malloc(tamanio_a_leer+1);
            memset(contenido,'\0',tamanio_a_leer+1);
            memcpy(contenido, memoria->espacio_usuario + direccion_fisica, tamanio_a_leer);
            log_debug(logger,"Data a enviar en memoria :%s", (char*)(memoria->espacio_usuario + direccion_fisica));
            unlock_mutex(memoria->mutex,logger);
            log_info(logger, "PID: %d - Acción: LEER - Dirección física: %d - Tamaño: %d - Origen: CPU", PID, direccion_fisica, tamanio_a_leer);
            log_debug(logger,"Data copidada de memoria :%s",contenido);
            //Envio contenido a CPU
            sleep_ms(memoria->retardo_memoria);
            enviar_string_tradicional(socket, contenido);
            free(contenido);
            free(conjunto);
            break;
           
        case MOV_OUT:
            log_debug(logger, "MOV_OUT");
            t_dato_DF* conjunto_DF = deserializar_dato_DF(paquete->buffer);
            char* registro_valor = conjunto_DF->valor;
            int registro_tamanio = conjunto_DF->longitud;
            int dir_fisica = conjunto_DF->df;

            lock_mutex(memoria->mutex,logger);

            //Lo copio desde la dirección logica y no desde donde comienza el segmento
            log_debug(logger,"Data recivida:%s",registro_valor);
            memset(memoria->espacio_usuario + dir_fisica, registro_tamanio, 0);
            memcpy(memoria->espacio_usuario + dir_fisica, registro_valor, registro_tamanio);
            log_debug(logger,"Data copidada:%s",(char*)(memoria->espacio_usuario + dir_fisica));

            unlock_mutex(memoria->mutex,logger);
            
            log_info(logger, "PID: %d - Acción: ESCRIBIR - Dirección física: %d - Tamaño: %d - Origen: CPU", PID, dir_fisica, registro_tamanio);
            free(registro_valor);
            free(conjunto_DF);
            //Devuelve algo?
            break;

        default:
            break;
        }

        free(paquete->buffer->stream);
    }
}


void* funcion_De_FS(void * element){
	t_data_hilo* data_hilo = (t_data_hilo*) element;
	t_memoria* memoria = data_hilo->memoria;
	int socket = data_hilo->socket;
    free(element);

	t_log* logger = iniciar_logger("./logs-memoria-fs.log", "Log-memoria-fs");
	log_debug(logger, "Hilo Memoria-FS");


	//Realizamos el handshake con FileSystem
	/*
    log_debug (logger, "Iniciando el handshake FileSystem");
	handshake_servidor (logger,conexion_file_system, &handshake_FileSystem, &resultOk, &resultError, 2);
	log_debug (logger, "Handshake terminado FileSystem");
    */

    t_paquete* paquete = malloc(sizeof(t_paquete));
    paquete->buffer = malloc(sizeof(t_buffer));

    while (1)
    {

        // Primero recibimos el codigo de operacion
        recv(socket, &(paquete->codigo_de_op), sizeof(int), 0);

        // Después ya podemos recibir el buffer. Primero su tamaño seguido del contenido
        recv(socket, &(paquete->buffer->size), sizeof(int), 0);
        paquete->buffer->stream = malloc(paquete->buffer->size);
        recv(socket, paquete->buffer->stream, paquete->buffer->size, 0);

        switch (paquete->codigo_de_op)
        {
        case F_READ:
            
            t_dato_DF* conjunto = deserializar_dato_DF(paquete->buffer);
            char* archivo_valor = conjunto->valor;
            int archivo_tamanio = conjunto->longitud;
            int direccion_fisica = conjunto->df; 

            lock_mutex(memoria->mutex,logger);

            //Lo copio desde la dirección logica y no desde donde comienza el segmento
            memcpy(memoria->espacio_usuario + direccion_fisica, archivo_valor, archivo_tamanio);

            unlock_mutex(memoria->mutex,logger);

            log_info(logger, "Acción: ESCRIBIR - Dirección física: %d - Tamaño: %d - Origen: FS", direccion_fisica, archivo_tamanio);

            break;

        case F_WRITE:
            
            //Recibe la direccion fisica del segmento y el tamaño a leer
            t_Par_de_Ints* data = deserializar_Par_de_Ints(paquete->buffer);
            int df = data->data1;
            int tamanio_a_leer = data->data2;

            lock_mutex(memoria->mutex,logger);

            //Extraigo contenido de memoria
            char* contenido = malloc(tamanio_a_leer+1);
            memset(contenido,'\0',tamanio_a_leer+1);
            memcpy(contenido, memoria->espacio_usuario + df, tamanio_a_leer);
            
            unlock_mutex(memoria->mutex,logger);

            log_info(logger, "Acción: LEER - Dirección física: %d - Tamaño: %d - Origen: FS", df, tamanio_a_leer);
            log_debug(logger, "Data enviada:%s",contenido);

            //Envio contenido a File System
            sleep_ms(memoria->retardo_memoria);
            enviar_string_tradicional(socket, contenido);
            free(contenido);
            break;

        default:
            break;
        }
        free(paquete->buffer->stream);
    }


}

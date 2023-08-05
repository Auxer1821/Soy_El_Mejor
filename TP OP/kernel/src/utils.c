#include "../include/utils.h"

//----------------leer_config-----------------------------------------------------------------------------------------------//

void leer_config(t_log* logger, t_config* config, char** puerto_escucha, char** puerto_cpu, char** puerto_file_system, char** puerto_memoria, char** ip_cpu, char** ip_file_system, char** ip_memoria){
	*puerto_escucha = config_get_string_value(config, "PUERTO_ESCUCHA");
	*puerto_cpu = config_get_string_value(config, "PUERTO_CPU");
	*puerto_file_system = config_get_string_value(config, "PUERTO_FILESYSTEM");
	*puerto_memoria = config_get_string_value(config, "PUERTO_MEMORIA");

	*ip_cpu = config_get_string_value(config, "IP_CPU");
	*ip_file_system = config_get_string_value(config, "IP_FILESYSTEM");
	*ip_memoria = config_get_string_value(config, "IP_MEMORIA");

	log_info(logger,"Lei los siguientes datos: \n Puerto Consola: %s \n"
												"Puerto CPU: %s \t IP CPU: %s \n "
												"Puerto File System: %s \t IP File System: %s \n "
												"Puerto Memoria: %s \t IP Memoria: %s \n",
			*puerto_escucha, *puerto_cpu, *ip_cpu, *puerto_file_system, *ip_file_system, *puerto_memoria, *ip_memoria);
}
//
//void crearConexion(char* IP, char* puerto, int* conexion){
//	conexion = crear_conexion(IP, puerto);
//	log_info(logger,"Se creo la conexion con el CPU.");
//}

//----------------RECIBIR_PAQUETE + REALIZAR INSTRUCCION-----------------------------------------------------------------------------------------------//

void actualizar_registro_pcb(t_PCB* proceso_enviado, t_registros_pcb registros_cpu){

	memcpy(proceso_enviado->registros_cpu.ax,registros_cpu.ax,4);
	memcpy(proceso_enviado->registros_cpu.bx,registros_cpu.bx,4);
	memcpy(proceso_enviado->registros_cpu.cx,registros_cpu.cx,4);
	memcpy(proceso_enviado->registros_cpu.dx,registros_cpu.dx,4);

	memcpy(proceso_enviado->registros_cpu.eax,registros_cpu.eax,8);
	memcpy(proceso_enviado->registros_cpu.ebx,registros_cpu.ebx,8);
	memcpy(proceso_enviado->registros_cpu.ecx,registros_cpu.ecx,8);
	memcpy(proceso_enviado->registros_cpu.edx,registros_cpu.edx,8);

	memcpy(proceso_enviado->registros_cpu.rax,registros_cpu.rax,16);
	memcpy(proceso_enviado->registros_cpu.rbx,registros_cpu.rbx,16);
	memcpy(proceso_enviado->registros_cpu.rcx,registros_cpu.rcx,16);
	memcpy(proceso_enviado->registros_cpu.rdx,registros_cpu.rdx,16);

}

void recibir_paquete_cpu(int socket_cpu, int socket_filesystem, int socket_memoria, t_dictionary* recursos, sem_t* sem_recursos, t_log* logger, t_PCB* proceso_enviado, t_archivos_kernel archivos_global, t_list* lista_ready, pthread_mutex_t* mutex_ready, sem_t* sem_habilitar_hilo3, t_temporal* temporizador_global, t_peticiones peticiones, sem_t* binario_open, pthread_mutex_t* mutex_pet_FS, t_compactacion lista_pcbs_creados, int alfa, sem_t* semaforo_pet_fs){
	t_paquete* paquete = malloc(sizeof(t_paquete));
	paquete->buffer = malloc(sizeof(t_buffer));

	// Primero recibimos el codigo de operacion
	recv(socket_cpu, &(paquete->codigo_de_op), sizeof(int), 0);

	// Después ya podemos recibir el buffer. Primero su tamaño seguido del contenido
	recv(socket_cpu, &(paquete->buffer->size), sizeof(int), 0);
	paquete->buffer->stream = malloc(paquete->buffer->size);
	recv(socket_cpu, paquete->buffer->stream, paquete->buffer->size, 0);


	void* data;

	switch (paquete->codigo_de_op)
	{
	case CONTEXTO:
		log_debug(logger,"CONTEXTO");
		data = deserializar_contexto(paquete -> buffer);
		memcpy(&proceso_enviado->program_counter,&((t_contexto*)data)->program_counter,sizeof(int));
		//
		actualizar_registro_pcb(proceso_enviado,((t_contexto*)data)->registros_cpu);

		memcpy(&proceso_enviado->tiempo_de_bloqueo,&((t_contexto*)data)->tiempo_de_bloqueo,sizeof(int));
		memcpy(&proceso_enviado->estado_actual,&((t_contexto*)data)->estado_actual,sizeof(int));
		

		memcpy(&proceso_enviado->razon_finalizado,&((t_contexto*)data)->razon_finalizado,sizeof(int));


		liberar_contexto(data);

		free(paquete->buffer->stream);
		free(paquete->buffer);
		free(paquete);
		return;


		break;
	case PREG_REC:
		log_debug(logger,"PREG_REC");
		char * key_recurso_ped = deserializar_string(paquete -> buffer);
		t_recurso* recurso_pedido;
		int respuesta_preg_rec = -1; //si termina en -1, significa que el recurso pedido no existe, entonces va a finalizar el proceso

		if(dictionary_has_key(recursos,key_recurso_ped)){
			recurso_pedido = dictionary_get(recursos,key_recurso_ped);
			respuesta_preg_rec = asignar_recurso(recurso_pedido, logger);
		

		
			if(respuesta_preg_rec == 0){ // NO HAY recursos
				char* recurso_bloqueante = string_duplicate(key_recurso_ped);
				proceso_enviado->recurso=recurso_bloqueante;
				proceso_enviado->razon_bloque = RECURSO;
				log_info(logger, "PID: %d - Wait: %s - Instancias: %d", proceso_enviado->pid, key_recurso_ped , (recurso_pedido->instancias_totales - recurso_pedido->instancias_usadas));

			
			}
			
			else if(respuesta_preg_rec == 1) // HAY recursos
			{
				log_info(logger, "PID: %d - Wait: %s - Instancias: %d", proceso_enviado->pid, key_recurso_ped , (recurso_pedido->instancias_totales - recurso_pedido->instancias_usadas));
			}
		}

		enviar_int_tradicional(socket_cpu, respuesta_preg_rec);
		free(key_recurso_ped);


		break;
	case SIGNAL_REC:
		log_debug(logger,"SIGNAL_REC");
		bool respuesta_signal = false;
		char* key_recurso_lib = deserializar_string(paquete->buffer);
		if(dictionary_has_key(recursos, key_recurso_lib))
		{
			t_recurso* recurso_a_liberar = dictionary_get(recursos, key_recurso_lib);
			liberar_instancia_recurso(recurso_a_liberar, sem_recursos, logger);
			log_info(logger, "PID: %d - Signal: %s - Instancias: %d", proceso_enviado->pid, key_recurso_lib , (recurso_a_liberar->instancias_totales - recurso_a_liberar->instancias_usadas));
			respuesta_signal=true;
		}
		enviar_bool(socket_cpu, respuesta_signal);
		free(key_recurso_lib);
		break;

	case F_OPEN:
		log_debug(logger,"F OPEN");
		data = deserializar_string(paquete->buffer); // [NombreArchivo]

		log_info(logger, "PID: %d - Abrir Archivo: %s", proceso_enviado->pid, (char*)data);

		int respuesta_f_open = abrir_archivo(logger, archivos_global, proceso_enviado, data, socket_filesystem, binario_open, peticiones );


		if(respuesta_f_open==0) sem_post(semaforo_pet_fs);
	

		enviar_int_tradicional(socket_cpu, respuesta_f_open);
		free(data);
		break;

	case F_CLOSE:
		log_debug(logger,"F_CLOSE");
		data = deserializar_string(paquete->buffer); // [NombreArchivo]
		
		bool respuesta_f_close = true;

		log_info(logger, "PID: %d - Cerrar Archivo: %s", proceso_enviado->pid, (char*)data);

		cerrar_archivo (logger, archivos_global, proceso_enviado, data, lista_ready, mutex_ready, sem_habilitar_hilo3, temporizador_global, alfa);
		enviar_bool(socket_cpu, respuesta_f_close);
		
		free(data);
		break;

	case F_SEEK:
		log_debug(logger,"F_SEEK");
		data = deserializar_string(paquete->buffer);
		
		char** aux = string_split(data, "|"); //aux[0] es nombre archivo, aux[1] es posicion

		char* nombre_archivo = aux[0];
		char* posicion = aux[1];

		bool respuesta_f_seek = true;


		if(!dictionary_has_key(proceso_enviado->tabla_de_archivos, nombre_archivo)){
			respuesta_f_seek = false;
		}

		else{
			int* puntero_actual = dictionary_get (proceso_enviado->tabla_de_archivos, nombre_archivo); 
			*puntero_actual = atoi(posicion);
			log_info(logger, "PID: %d - Actualizar puntero Archivo: %s - Puntero %d", proceso_enviado->pid, nombre_archivo, *puntero_actual);
		}
		
		enviar_bool(socket_cpu, respuesta_f_seek);
		free(data);
		break;

	case F_READ:
		log_debug(logger,"F_READ");
		data = deserializar_string(paquete->buffer);

		controlFS(logger, peticiones, socket_filesystem, proceso_enviado, data, F_READ);
		enviar_bool(socket_cpu,true);
		free(data);

		break;

	case F_WRITE:
		log_debug(logger,"F_WRITE");

		data = deserializar_string(paquete->buffer);

		controlFS(logger, peticiones, socket_filesystem, proceso_enviado, data, F_WRITE);
		enviar_bool(socket_cpu,true);

		free(data);
		break;
		
	case F_TRUNCATE:
		log_debug(logger,"F_TRUNCATE");
		data = deserializar_string(paquete->buffer);
		
		controlFS(logger, peticiones, socket_filesystem, proceso_enviado, data, F_TRUNCATE);
		enviar_bool(socket_cpu,true);
		free(data);

		break;

	case CREATE_SEGMENT:
		log_debug(logger,"CREATE_SEGMENT");
		int respuesta_create_seg;

		char* segmento_a_crear = deserializar_string(paquete->buffer);
		char** segmento_a_crear_split=string_split(segmento_a_crear,"|");

		int id = atoi(segmento_a_crear_split[0]);
		int tamanio = atoi(segmento_a_crear_split[1]);

		lock_mutex(lista_pcbs_creados.mutex_memoria,logger);
		enviar_Pet_Segmento(socket_memoria, proceso_enviado->pid, id, tamanio);

		recibir_paquete_memoria(logger, socket_memoria, socket_cpu,proceso_enviado, mutex_pet_FS,lista_pcbs_creados.pcbs_creados);
		unlock_mutex(lista_pcbs_creados.mutex_memoria,logger);
		log_info(logger, "PID: %d - Crear Segmento - Id: %d - Tamaño: %d", proceso_enviado->pid, id, tamanio);
		free(segmento_a_crear);
		break;

	case DELETE_SEGMENT:
		log_debug(logger,"DELETE_SEGMENT");
		int* id_segmento = deserializar_int(paquete -> buffer);
		
		lock_mutex(lista_pcbs_creados.mutex_memoria,logger);
		enviar_Par_de_Ints(socket_memoria, proceso_enviado->pid, *id_segmento, DELETE_SEGMENT);

		recibir_paquete_memoria(logger, socket_memoria, socket_cpu,proceso_enviado, mutex_pet_FS,lista_pcbs_creados.pcbs_creados);
		unlock_mutex(lista_pcbs_creados.mutex_memoria,logger);
		log_info(logger, "PID: %d - Eliminar Segmento - Id: %d", proceso_enviado->pid, *id_segmento);

		free(id_segmento);
		break;

	default:
		log_debug(logger,"CASO NO ENCONTRADO");
		break;
	}


	recibir_paquete_cpu(socket_cpu, socket_filesystem, socket_memoria, recursos, sem_recursos, logger, proceso_enviado, archivos_global, lista_ready, mutex_ready, sem_habilitar_hilo3, temporizador_global, peticiones, binario_open, mutex_pet_FS,lista_pcbs_creados, alfa,semaforo_pet_fs);
	
	//free(instrucciones_como_string);
	
	free(paquete->buffer->stream);
	free(paquete->buffer);
	free(paquete);
}

void recibir_paquete_memoria(t_log* logger, int conexion_memoria, int socket_cpu, t_PCB* proceso_enviado, pthread_mutex_t* mutex_pet_FS,t_list* pcbs_creados){
	t_paquete* paquete = malloc(sizeof(t_paquete));
	paquete->buffer = malloc(sizeof(t_buffer));

	// Primero recibimos el codigo de operacion
	recv(conexion_memoria, &(paquete->codigo_de_op), sizeof(int), 0);

	// Después ya podemos recibir el buffer. Primero su tamaño seguido del contenido
	recv(conexion_memoria, &(paquete->buffer->size), sizeof(int), 0);
	paquete->buffer->stream = malloc(paquete->buffer->size);
	recv(conexion_memoria, paquete->buffer->stream, paquete->buffer->size, 0);


	switch (paquete->codigo_de_op)
	{
	case OUT_OF_MEMORY:
		int a = 0;
		enviar_int(socket_cpu, &a, OUT_OF_MEMORY);
		break;
	case PET_COMPACTAR:	//tanto el envio de la creacion del segmento como las respuesta las encapsule en la misma poroque hacen lo mismo
		log_info(logger, "Compactación: Se solicitó compactación / Esperando Fin de Operaciones de FS");
		lock_mutex(mutex_pet_FS, logger);
		enviar_int_tradicional(conexion_memoria, 0);

		// Recibimos y actualizamos todas las tablas de segmentos
		log_debug(logger, "Esperando tablas ....");
		actualizar_tablas_segmentos(logger,conexion_memoria,pcbs_creados);
		log_info(logger, "Se finalizó el proceso de compactación");
		enviar_int_tradicional(conexion_memoria, 0);		
		log_debug(logger, "Confirmacion de compactacion de kernel enviada");
		unlock_mutex(mutex_pet_FS, logger);

		recibir_paquete_memoria(logger, conexion_memoria, socket_cpu, proceso_enviado, mutex_pet_FS,pcbs_creados); //esperando la RTA_CRE_SEG
		break;
	case TS:
		t_list* data = deserializar_tabla_de_segmentos(paquete->buffer);

		list_destroy_and_destroy_elements(proceso_enviado->tabla_segmentos,free);
		proceso_enviado->tabla_segmentos=data;
		enviar_tabla_de_segmentos(socket_cpu,data);
		break;
	default:
		log_error(logger,"ERROR DE TIPO DE PAQUETE");
		break;

	}



//----------------CERRAR_PROGRAMA-----------------------------------------------------------------------------------------------//

	//free(instrucciones_como_string);
	free(paquete->buffer->stream);
	free(paquete->buffer);
	free(paquete);
}

void actualizar_tablas_segmentos(t_log* logger, int socket_memoria, t_list* lista_pcbs_creados){

		t_buffer*  buffer = malloc(sizeof(t_buffer));
		// Después ya podemos recibir el buffer. Primero su tamaño seguido del contenido
		recv(socket_memoria, &(buffer->size), sizeof(int), 0);
		buffer->stream = malloc(buffer->size);
		recv(socket_memoria, buffer->stream, buffer->size, 0);
		void* stream = buffer->stream;

		int cant_pcbs;
		memcpy(&cant_pcbs, stream, sizeof(int));
		stream += sizeof(int);


		for(int j=0 ; j < cant_pcbs; j++)
		{
			int pid;
			memcpy(&pid, stream, sizeof(int));
			stream += sizeof(int);

			bool buscar_pid(void* arg){
				t_PCB* proceso_aux=(t_PCB*)arg;
				return proceso_aux->pid==pid;
			};

			t_PCB* proceso = list_remove_by_condition(lista_pcbs_creados,buscar_pid);
			list_clean_and_destroy_elements(proceso->tabla_segmentos,free);

			int cant_segmentos;
			memcpy(&cant_segmentos, stream, sizeof(int));
			stream += sizeof(int);

			for(int i =0; i < cant_segmentos; i++){
				t_segmento* segmento = malloc(sizeof(t_segmento));
				memcpy(&segmento->ID, stream, sizeof(int));
				stream += sizeof(int);
				memcpy(&segmento->base, stream, sizeof(int));
				stream += sizeof(int);
				memcpy(&segmento->tamanio, stream, sizeof(int));
				stream += sizeof(int);
				list_add(proceso->tabla_segmentos, segmento);
			}

		}

}

void terminar_programa(t_log* logger, t_config* config, int conexion_cpu, int conexion_file_system, int conexion_memoria){
	/* Y por ultimo, hay que liberar lo que utilizamos (conexion, log y config)
	  con las funciones de las commons y del TP mencionadas en el enunciado */

	  log_destroy (logger);
	  config_destroy (config);
	  liberar_conexion(conexion_cpu);
	  liberar_conexion(conexion_file_system);
	  liberar_conexion(conexion_memoria);
}
/*
//----------------FUNCIONES_MUTEX-----------------------------------------------------------------------------------------------//

void lock_mutex(pthread_mutex_t* mutex, t_log* logger){

    if ( pthread_mutex_lock(mutex) != 0){
            log_error(logger,"ERROR BLOQUEANDO EL MUTEX");
            exit(111);
        } // BLOQUEA EL SEMAFORO
}

void unlock_mutex(pthread_mutex_t* mutex, t_log* logger){
    
    if ( pthread_mutex_unlock(mutex) != 0){
            log_error(logger,"ERROR DESBLOQUEANDO EL MUTEX");
            exit(112);
        } // DESBLOQUEA EL SEMAFORO
}
*/
//----------------agregar_proceso_READY-----------------------------------------------------------------------------------------------//

void agregar_proceso_READY(t_log* logger, t_PCB* proceso, t_list* lista_ready, pthread_mutex_t* mutex, sem_t* semaforo_hay_algo_listo, t_temporal* temporizador_global, int alfa){
	    proceso->estado_actual=READY;
        lock_mutex(mutex, logger);

    	int estimador_nuevo = alfa * proceso->estimacion_proxima_rafaga + (1-alfa) * proceso->tiempo_de_ejecucion;
		proceso->estimacion_proxima_rafaga=estimador_nuevo;

        //contador del hrrn no hecho bien x mi xq las commons son una mierda :) att:@BRUNO
		proceso->tiempo_de_llegada_ready=temporal_gettime(temporizador_global);
        list_add(lista_ready, proceso);
        sem_post(semaforo_hay_algo_listo);

        unlock_mutex(mutex, logger);
}

//----------------controlFS-----------------------------------------------------------------------------------------------//

void controlFS(t_log* logger, t_peticiones peticiones, int socket_filesystem, t_PCB* proceso_enviado, char* data, int codifo_op){
	//data = "nombre|direccion_memoria|cantbyte"
	//data(F_TRUNCATE) = "nombre|tamaño"
	char** data_separada = string_split(data,"|");
	char* peticion = string_new();
	string_append_with_format(&peticion, "%s", data);

	if(codifo_op == F_TRUNCATE){
		int* posicion_archivo = dictionary_get(proceso_enviado->tabla_de_archivos, data_separada[0]);
	    *posicion_archivo = 0;

		log_info(logger, "PID: %d - Archivo: %s - Tamaño: %s", proceso_enviado->pid, data_separada[0], data_separada[1]);
	}
	else
	{
		int* posicion_archivo = dictionary_get(proceso_enviado->tabla_de_archivos, data_separada[0]);
		string_append_with_format(&peticion,"|%s", string_itoa( *posicion_archivo));

		if( codifo_op == F_READ )
			log_info(logger, "PID: %d - Leer Archivo: %s - Puntero %d - Dirección Memoria %s - Tamaño %s", proceso_enviado->pid, data_separada[0], *posicion_archivo, data_separada[1], data_separada[2]);
		else
			log_info(logger, "PID: %d - Escribir Archivo: %s - Puntero %d - Dirección Memoria %s - Tamaño %s", proceso_enviado->pid, data_separada[0], *posicion_archivo, data_separada[1], data_separada[2]);
		
		*posicion_archivo += atoi(data_separada[2]);
	}



	t_peticion* elemento = malloc(sizeof(t_peticion));
	elemento->codigo_op = codifo_op;
	elemento->peticion = peticion;
	elemento->proceso = proceso_enviado;
	
	lock_mutex(peticiones.mutex, logger);
	list_add(peticiones.peticiones, elemento);
	unlock_mutex(peticiones.mutex, logger);
	log_info(logger,"PID: %d - Bloqueado por: %s", proceso_enviado->pid, data_separada[0]);


	proceso_enviado->razon_bloque = PET_FS;
}


char* pasar_lista_string(t_list* lista){ 

	char* r = string_new();
	
	void concatenar_en_string(void* prs){
		t_PCB* proceso = (t_PCB*)prs;
		string_append_with_format(&r ,"%s - ", string_itoa(proceso->pid));
	};


	list_iterate(lista, concatenar_en_string);
	
	return r;
}



t_compactacion* inicializar_t_compactacion(t_log* logger	){
	t_compactacion* r = malloc(sizeof(t_compactacion));
	r->pcbs_creados =list_create();

	//pthread_mutex_t Aux_mutex_memoria;		// hilos {3,5}
	pthread_mutex_t* mutex_memoria = malloc(sizeof(pthread_mutex_t));
	if ( pthread_mutex_init(mutex_memoria, NULL) != 0){
		log_error(logger,"ERROR INICIAR EL MUTEX");
		exit(3);
	}
	r->mutex_memoria=mutex_memoria;

	return r;
}


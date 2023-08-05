#include "../include/instrucciones.h"

//-----------------------inicializarRegistros------------------------------------------------------------------------

t_dictionary* inicializarRegistros(void){
    t_dictionary* registro = dictionary_create();

    t_registro* ax = malloc(sizeof(t_registro));
    ax->valor = malloc(4);
    ax->longitud = 4;
    dictionary_put(registro,"AX", ax);

    t_registro* bx = malloc(sizeof(t_registro));
    bx->valor = malloc(4);
    bx->longitud = 4;
    dictionary_put(registro,"BX", bx);

    t_registro* cx = malloc(sizeof(t_registro));
    cx->valor = malloc(4);
    cx->longitud = 4;
    dictionary_put(registro,"CX", cx);

    t_registro* dx = malloc(sizeof(t_registro));
    dx->valor = malloc(4);
    dx->longitud = 4;
    dictionary_put(registro,"DX", dx);

    t_registro* eax = malloc(sizeof(t_registro));
    eax->valor = malloc(8);
    eax->longitud = 8;
    dictionary_put(registro,"EAX", eax);

    t_registro* ebx = malloc(sizeof(t_registro));
    ebx->valor = malloc(8);
    ebx->longitud = 8;
    dictionary_put(registro,"EBX", ebx);

    t_registro* ecx = malloc(sizeof(t_registro));
    ecx->valor = malloc(8);
    ecx->longitud = 8;
    dictionary_put(registro,"ECX", ecx);

    t_registro* edx = malloc(sizeof(t_registro));
    edx->valor = malloc(8);
    edx->longitud = 8;
    dictionary_put(registro,"EDX", edx);

    t_registro* rax = malloc(sizeof(t_registro));
    rax->valor = malloc(16);
    rax->longitud = 16;
    dictionary_put(registro,"RAX", rax);

    t_registro* rbx = malloc(sizeof(t_registro));
    rbx->valor = malloc(16);
    rbx->longitud = 16;
    dictionary_put(registro,"RBX", rbx);

    t_registro* rcx = malloc(sizeof(t_registro));
    rcx->valor = malloc(16);
    rcx->longitud = 16;
    dictionary_put(registro,"RCX", rcx);

    t_registro* rdx = malloc(sizeof(t_registro));
    rdx->valor = malloc(16);
    rdx->longitud = 16;
    dictionary_put(registro,"RDX", rdx);

    return registro;
}

//-----------------------liberarRegistros CON S------------------------------------------------------------------------
void liberarRegistros (t_dictionary* registros){

    dictionary_iterator(registros, liberarRegistro);
    dictionary_destroy(registros);

}

//-----------------------liberarRegistro SIN S------------------------------------------------------------------------
void liberarRegistro(char* key, void* element){

    t_registro* registro = (t_registro*)element;
    free(registro->valor);
    free(registro);


}

//-----------------------actualizar_registro------------------------------------------------------------------------

void actualizar_registro (t_dictionary* registros_propios, t_registros_pcb registros_kernel){

    t_registro* registro;
    registro=dictionary_get(registros_propios,"AX");
    memcpy(registro->valor,registros_kernel.ax,4);
    registro=dictionary_get(registros_propios,"BX");
    memcpy(registro->valor,registros_kernel.bx,4);
    registro=dictionary_get(registros_propios,"CX");
    memcpy(registro->valor,registros_kernel.cx,4);
    registro=dictionary_get(registros_propios,"DX");
    memcpy(registro->valor,registros_kernel.dx,4);
 
    registro=dictionary_get(registros_propios,"EAX");
    memcpy(registro->valor,registros_kernel.eax,8);
    registro=dictionary_get(registros_propios,"EBX");
    memcpy(registro->valor,registros_kernel.ebx,8);
    registro=dictionary_get(registros_propios,"ECX");
    memcpy(registro->valor,registros_kernel.ecx,8);
    registro=dictionary_get(registros_propios,"EDX");
    memcpy(registro->valor,registros_kernel.edx,8);


    registro=dictionary_get(registros_propios,"RAX");
    memcpy(registro->valor,registros_kernel.rax,16);
    registro=dictionary_get(registros_propios,"RBX");
    memcpy(registro->valor,registros_kernel.rbx,16);
    registro=dictionary_get(registros_propios,"RCX");
    memcpy(registro->valor,registros_kernel.rcx,16);
    registro=dictionary_get(registros_propios,"RDX");
    memcpy(registro->valor,registros_kernel.rdx,16);

}

//-----------------------actualizar_pcb------------------------------------------------------------------------

void actualizar_contexto (t_contexto* contexto, t_dictionary* registros_propios){

    t_registro* registro;
    registro=dictionary_get(registros_propios,"AX");
    memcpy(contexto->registros_cpu.ax,registro->valor,4);
    registro=dictionary_get(registros_propios,"BX");
    memcpy(contexto->registros_cpu.bx,registro->valor,4);
    registro=dictionary_get(registros_propios,"CX");
    memcpy(contexto->registros_cpu.cx,registro->valor,4);
    registro=dictionary_get(registros_propios,"DX");
    memcpy(contexto->registros_cpu.dx,registro->valor,4);
 
    registro=dictionary_get(registros_propios,"EAX");
    memcpy(contexto->registros_cpu.eax,registro->valor,8);
    registro=dictionary_get(registros_propios,"EBX");
    memcpy(contexto->registros_cpu.ebx,registro->valor,8);
    registro=dictionary_get(registros_propios,"ECX");
    memcpy(contexto->registros_cpu.ecx,registro->valor,8);
    registro=dictionary_get(registros_propios,"EDX");
    memcpy(contexto->registros_cpu.edx,registro->valor,8);


    registro=dictionary_get(registros_propios,"RAX");
    memcpy(contexto->registros_cpu.rax,registro->valor,16);
    registro=dictionary_get(registros_propios,"RBX");
    memcpy(contexto->registros_cpu.rbx,registro->valor,16);
    registro=dictionary_get(registros_propios,"RCX");
    memcpy(contexto->registros_cpu.rcx,registro->valor,16);
    registro=dictionary_get(registros_propios,"RDX");
    memcpy(contexto->registros_cpu.rdx,registro->valor,16);

}

//-----------------------realizar_instruccion--PROGRAMA PRINCIPAL----------------------------------------------------------------------

void realizar_instruccion (t_contexto* pcb, t_dictionary* registros, char** instruccion, int conexion_kernel, int conexion_memoria, t_log* logger, int retardo_de_instruccion, int tam_max_seg){

    //---------------------------------------------------------------------------
		if(string_equals_ignore_case("SET", instruccion[0]))
		{
            log_info(logger,"PID: %d - Ejecutando: %s - %s %s", pcb->pid, instruccion[0], instruccion[1], instruccion[2]);
            string_to_upper(instruccion[1]);
            t_registro* registo = dictionary_get (registros, instruccion[1]);
			set ( registo, instruccion[2], retardo_de_instruccion);
		}
    //---------------------------------------------------------------------------
		else if (string_equals_ignore_case("YIELD", instruccion[0]))
		{
            log_info(logger,"PID: %d - Ejecutando: %s", pcb->pid, instruccion[0]);
            yield(pcb);
		}
    //---------------------------------------------------------------------------
        else if (string_equals_ignore_case("EXIT", instruccion[0]))
        {
            log_info(logger,"PID: %d - Ejecutando: %s", pcb->pid, instruccion[0]);
            exit_instruccion(pcb, FIN_SUCCESS);
        }
    //---------------------------------------------------------------------------
        else if (string_equals_ignore_case("I/O", instruccion[0]))
        {
            log_info(logger,"PID: %d - Ejecutando: %s - %s", pcb->pid, instruccion[0], instruccion[1]);
        	io(pcb, atoi (instruccion[1]) );
        }
    //---------------------------------------------------------------------------
        else if (string_equals_ignore_case("WAIT", instruccion[0]))
        {
            log_info(logger,"PID: %d - Ejecutando: %s - %s", pcb->pid, instruccion[0], instruccion[1]);
        	wait_instruccion (pcb, instruccion[1], conexion_kernel,logger);

        }
    //---------------------------------------------------------------------------
        else if (string_equals_ignore_case("SIGNAL", instruccion[0]))
        {
            log_info(logger,"PID: %d - Ejecutando: %s - %s", pcb->pid, instruccion[0], instruccion[1]);
            signal_instruccion (pcb, instruccion[1], conexion_kernel,logger);
        }
    //---------------------------------------------------------------------------
        else if (string_equals_ignore_case("MOV_IN", instruccion[0]))
        {
            log_info(logger,"PID: %d - Ejecutando: %s - %s %s", pcb->pid, instruccion[0], instruccion[1], instruccion[2]);
            string_to_upper(instruccion[1]);
            t_registro* registro = dictionary_get(registros, instruccion[1]);
            mov_in (registro, atoi(instruccion[2]), tam_max_seg, pcb, conexion_memoria, logger); // verificar el  [2] cuando entendamos memoria y sepamos de donde sale direccion logica
        }
    //---------------------------------------------------------------------------
        else if (string_equals_ignore_case("MOV_OUT", instruccion[0]))
        {
            log_info(logger,"PID: %d - Ejecutando: %s - %s %s",pcb->pid, instruccion[0], instruccion[2], instruccion[1]);
            string_to_upper(instruccion[2]);
            t_registro* registro = dictionary_get(registros, instruccion[2]);
            mov_out ( registro, atoi(instruccion[1]), tam_max_seg, pcb, conexion_memoria, logger); // verificar el  [2] cuando entendamos memoria y sepamos de donde sale direccion logica
        }
    //---------------------------------------------------------------------------
        else if (string_equals_ignore_case("F_OPEN", instruccion[0]))
        {   
            log_info(logger,"PID: %d - Ejecutando: %s - %s",pcb->pid, instruccion[0], instruccion[1]);
            f_open ( instruccion[1], conexion_kernel, pcb, registros, logger);
        }
    //---------------------------------------------------------------------------
        else if (string_equals_ignore_case("F_CLOSE", instruccion[0]))
        {
            log_info(logger,"PID: %d - Ejecutando: %s - %s", pcb->pid, instruccion[0], instruccion[1]);
            f_close ( instruccion[1], conexion_kernel, pcb, registros, logger);
        }
    //---------------------------------------------------------------------------
        else if (string_equals_ignore_case("F_SEEK", instruccion[0]))
        {
            log_info(logger,"PID:%d - Ejecutando: %s %s %s",pcb->pid, instruccion[0],instruccion[1],instruccion[2]);
            f_seek ( instruccion[1], instruccion[2], conexion_kernel, pcb, registros, logger);
        }
    //---------------------------------------------------------------------------
        else if (string_equals_ignore_case("F_READ", instruccion[0]))
        {
            log_info(logger,"PID: %d - Ejecutando: %s - %s %s %s",pcb->pid, instruccion[0],instruccion[1],instruccion[2],instruccion[3]);
            f_read ( instruccion[1], instruccion[2], instruccion[3], conexion_kernel, pcb, logger, tam_max_seg);
        }
    //---------------------------------------------------------------------------
        else if (string_equals_ignore_case("F_WRITE", instruccion[0]))
        {
            log_info(logger,"PID: %d - Ejecutando: %s - %s %s %s",pcb->pid, instruccion[0],instruccion[1],instruccion[2],instruccion[3]);
            f_write ( instruccion[1], instruccion[2], instruccion[3], conexion_kernel, pcb, logger, tam_max_seg);
        }
    //---------------------------------------------------------------------------
        else if (string_equals_ignore_case("F_TRUNCATE", instruccion[0]))
        {
            log_info(logger,"PID: %d - Ejecutando: %s - %s %s",pcb->pid, instruccion[0],instruccion[1],instruccion[2]);
            f_truncate ( instruccion[1], instruccion[2], conexion_kernel, pcb, registros);
        }
    //---------------------------------------------------------------------------
        else if (string_equals_ignore_case("CREATE_SEGMENT", instruccion[0]))
        {
            log_info(logger,"PID: %d - Ejecutando: %s - %s %s",pcb->pid, instruccion[0],instruccion[1],instruccion[2]);
            create_segment ( instruccion[1], instruccion[2], conexion_kernel, pcb);
        }
    //---------------------------------------------------------------------------
        else if (string_equals_ignore_case("DELETE_SEGMENT", instruccion[0]))
        {
            log_info(logger,"PID: %d - Ejecutando: %s - %s",pcb->pid, instruccion[0],instruccion[1]);
            delete_segment ( instruccion[1], conexion_kernel, pcb);
        }
        else
        {
            log_error(logger,"NO SE ENCONTRO LA INTRUCCION");
        }
}

//-----------------------FUNCIONES AUXILIARES------------------------------------------------------------------------

int string_to_int(char* texto){ //creditos a bruno x aprobar SZ y recordar como se hace
    int numero;
    int esponente = 1;

    for(int i = string_length(texto); i >= 0; i--){
        numero += esponente * (texto [i] - '0');
        esponente *= 10;
    }
    return numero;
}


void resetCero(t_registro* registro){
    for (int i=0; i < (registro->longitud); i++)
    {
        registro->valor[i]='0';
    }
}

//-----------------------INSTRUCCIONES------------------------------------------------------------------------


void set(t_registro* registro, char* unValor, int retardo_de_instruccion){
    resetCero (registro); //setea el registo a todos ceros '0'

    int i=0; 
    
    while ( unValor[i] != '\0' && i < registro->longitud){
        registro->valor[i] = unValor[i];
        i++;
    }

    sleep_ms(retardo_de_instruccion);
}

void yield (t_contexto* pcb){
    pcb->estado_actual = READY;
}


void exit_instruccion (t_contexto* pcb, t_razon_finalizado razon_finalizado){
    pcb->estado_actual = ENDED;
    pcb->razon_finalizado = razon_finalizado;
}


void io (t_contexto* pcb, int unTiempo){
	pcb->tiempo_de_bloqueo = unTiempo;
    pcb->estado_actual = BLOCK;
}

void wait_instruccion (t_contexto* pcb, char* nombre_recurso, int conexion, t_log* logger){

    enviar_string(conexion, nombre_recurso, PREG_REC);
    int respuesta = recibir_int(conexion);
   

    switch (respuesta)
    {
    case 0:
        pcb->estado_actual = BLOCK;
        break;

    case 1:
        /* code */
        break;

    case -1:
        log_error(logger,"ERROR EN PID: %d - WAIT DE RECURSO NO EXISTENTE", pcb->pid);
        exit_instruccion(pcb, FIN_INVALID_RESOURCE);
        break;
    
    default:
        log_error(logger,"ERROR EN PID: %d - RESPUSTAS INVALIDA", pcb->pid);
        exit_instruccion(pcb, ERROR);
        break;
    }
}


void signal_instruccion (t_contexto* pcb, char* nombre_recurso, int conexion, t_log* logger){ //Preguntar el sabado porque esta función tiene menos utilidad que Quincy 
    
    
    enviar_string(conexion, nombre_recurso, SIGNAL_REC);
    
    if ( recibir_bool(conexion) )
    {
        //todo ok
    }
    else
    {
        log_error(logger,"ERROR EN PID: %d - SIGNAL DE RECURSO NO EXISTENTE", pcb->pid);
        exit_instruccion(pcb, FIN_INVALID_RESOURCE);
    }
}


void mov_in (t_registro* registro, int direccion_logica, int tam_max_segmento, t_contexto* pcb, int conexion_memoria, t_log* logger) {
    int id_segmento;
    int offset;
    //Dirección lógica a física
    int direccion_fisica = MMU(&id_segmento,&offset, direccion_logica, tam_max_segmento, pcb, registro->longitud, logger);
    if(direccion_fisica == -1){
        log_error(logger,"PID: %d - Error SEG_FAULT- Segmento: %d - Offset: %d - Tamaño: %d",pcb->pid, id_segmento, offset, registro->longitud);
        exit_instruccion(pcb, FIN_SEG_FAULT);
    }else{
        enviar_Par_de_Ints(conexion_memoria, direccion_fisica, registro->longitud, MOV_IN);
        recibir_bool(conexion_memoria);
        enviar_int_tradicional(conexion_memoria,pcb->pid);
        log_info(logger,"PID:%d - MOV_IN - Enviando direccion fisica: %d",pcb->pid, direccion_fisica);
        //Actualizo el valor en el registro
        char * aux = recibir_string_tradicional(conexion_memoria, registro->longitud);
        char* charlog = malloc(registro->longitud+1);
        memset(charlog,'\0',registro->longitud+1);
        memcpy(charlog,aux,registro->longitud);
        log_info(logger,"PID: %d - Acción: LEER - Segmento: %d - Dirección Física: %d - Valor: %s", pcb->pid, id_segmento, direccion_fisica, charlog);
        memcpy(registro->valor,aux,registro->longitud);
        free(aux);
        free(charlog);
    }    
}

void mov_out (t_registro* registro, int direccion_logica, int tam_max_segmento, t_contexto* pcb, int conexion_memoria, t_log* logger){
    int id_segmento;
    int offset;
    //Dirección lógica a física
    int direccion_fisica = MMU(&id_segmento,&offset, direccion_logica, tam_max_segmento, pcb , registro->longitud, logger);
    if(direccion_fisica == -1){// Caso se pasa de la memoria max
        log_error(logger, "PID: %d - Error SEG_FAULT- Segmento: %d - Offset: %d - Tamaño: %d" , pcb->pid, id_segmento,offset, registro->longitud);
        exit_instruccion(pcb, FIN_SEG_FAULT);
    }else{
        //Le envio a memoria el registro y la dirección física
        log_info(logger,"PID:%d - MOV_OUT  %s", pcb->pid, registro->valor);
        enviar_dato_con_DF(conexion_memoria, registro->longitud, registro->valor, &direccion_fisica, MOV_OUT);
        log_info(logger,"PID: %d - Acción: ESCRIBIR - Segmento: %d - Dirección Física: %d - Valor: %s", pcb->pid, id_segmento, direccion_fisica, registro->valor);
        recibir_bool(conexion_memoria);
        enviar_int_tradicional(conexion_memoria,pcb->pid);
    }
    
}

void f_open ( char* nombre_archivo, int conexion, t_contexto* pcb, t_dictionary* registros, t_log* logger){
    enviar_string (conexion, nombre_archivo, F_OPEN);
    
    switch ( recibir_int(conexion) )
    {
    case 1:
        pcb->estado_actual=BLOCK;
        break;
    case 2: //SE ROMPIO EL FILESYSTEM (O ALGO)
        log_error(logger,"ERROR EN PID: %d - F_OPEN - NO SE RECIBIO RESPUESTA DEL KERNEL", pcb->pid);
        exit_instruccion(pcb, ERROR);
        break;
    
    default:
        break;
    }
}

void f_close ( char* nombre_archivo, int conexion, t_contexto* pcb, t_dictionary* registros, t_log* logger){
    enviar_string (conexion, nombre_archivo, F_CLOSE);

    if (!recibir_bool(conexion)){
        log_error(logger,"ERROR EN PID: %d - F_CLOSE - NO SE RECIBIO RESPUESTA DEL KERNEL", pcb->pid);
        exit_instruccion(pcb, ERROR);
    }
}

void f_seek ( char* nombre_archivo, char* posicion, int conexion, t_contexto* pcb, t_dictionary* registros, t_log* logger){ // Entender el memoria para mejorar el char*
    
    string_append_with_format(&nombre_archivo,"|%s", posicion);

    enviar_string (conexion, nombre_archivo, F_SEEK);

    if (!recibir_bool(conexion)){
        log_error(logger,"ERROR EN PID: %d - F_SEEK - NO SE RECIBIO RESPUESTA DEL KERNEL",pcb->pid);
        exit_instruccion(pcb, ERROR);
    }

}

void f_read (char* nombre_archivo, char* direccion_logica, char* bytes, int conexion, t_contexto* pcb, t_log* logger, int seg_max){   
    
    int id;
    int offset;
    int direccion_fisica=MMU(&id, &offset, atoi(direccion_logica), seg_max, pcb, atoi(bytes), logger); // = mmu_traducir_memoria(direccion_logica)
    if(direccion_fisica == -1){// Caso se pasa de la memoria max
        log_error(logger, "PID: %d - Error SEG_FAULT- Segmento: %d - Offset: %d - Tamaño: %d" , pcb->pid, id, offset, atoi(bytes));
        exit_instruccion(pcb, FIN_SEG_FAULT);
    }
    else{
        char* direccion_memoria = string_itoa(direccion_fisica);
        char* dato_enviar = string_new();
        string_append_with_format(&dato_enviar,"%s",nombre_archivo);

        string_append_with_format(&dato_enviar,"|%s", direccion_memoria);
        string_append_with_format(&dato_enviar,"|%s", bytes);

        enviar_string (conexion, dato_enviar, F_READ);
        recibir_bool (conexion);

        pcb->estado_actual = BLOCK;

    }

}

void f_write (char* nombre_archivo, char* direccion_logica, char* bytes, int conexion, t_contexto* pcb, t_log* logger, int seg_max){
    

    int id;
    int offset;
    int direccion_fisica=MMU(&id, &offset, atoi(direccion_logica), seg_max, pcb, atoi(bytes), logger); // = mmu_traducir_memoria(direccion_logica)
    if(direccion_fisica == -1){// Caso se pasa de la memoria max
        log_error(logger, "PID: %d - Error SEG_FAULT- Segmento: %d - Offset: %d - Tamaño: %d" , pcb->pid, id, offset, atoi(bytes));
        exit_instruccion(pcb, FIN_SEG_FAULT);
    }
    else{
        char* direccion_memoria = string_itoa(direccion_fisica);
        char* dato_enviar = string_new();
        string_append_with_format(&dato_enviar,"%s",nombre_archivo);
        
        string_append_with_format(&dato_enviar,"|%s", direccion_memoria);
        string_append_with_format(&dato_enviar,"|%s", bytes);

        enviar_string (conexion, dato_enviar, F_WRITE);
        recibir_bool (conexion);

        pcb->estado_actual = BLOCK;

    }
}

void f_truncate (char* nombre_archivo, char* tamanio, int conexion, t_contexto* pcb, t_dictionary* registros){
    char* dato_enviar = string_new();
    string_append_with_format(&dato_enviar,"%s",nombre_archivo);
    string_append_with_format(&dato_enviar,"|%s", tamanio);

    enviar_string (conexion, dato_enviar, F_TRUNCATE);
    recibir_bool (conexion);

    pcb->estado_actual = BLOCK;

    
}

void create_segment (char* ID_segmento, char* tamanio, int conexion, t_contexto* pcb){

    string_append_with_format(&ID_segmento,"|%s", tamanio);
    
    enviar_string (conexion, ID_segmento, CREATE_SEGMENT); //Envia los datos


    t_paquete* paquete = malloc(sizeof(t_paquete));
	paquete->buffer = malloc(sizeof(t_buffer));

	// Primero recibimos el codigo de operacion
	recv(conexion, &(paquete->codigo_de_op), sizeof(int), 0);

	// Después ya podemos recibir el buffer. Primero su tamaño seguido del contenido
	recv(conexion, &(paquete->buffer->size), sizeof(int), 0);
	paquete->buffer->stream = malloc(paquete->buffer->size);
	recv(conexion, paquete->buffer->stream, paquete->buffer->size, 0);

    switch (paquete->codigo_de_op)
    {
    case OUT_OF_MEMORY:
        exit_instruccion(pcb,FIN_OUT_OF_MEMORY);
        break;
    
    case TS:
        
        list_destroy_and_destroy_elements(pcb->tabla_segmentos,free);
		pcb->tabla_segmentos=deserializar_tabla_de_segmentos(paquete->buffer);
        break;

    default:
        exit_instruccion(pcb,ERROR);
        break;
    }

}

void delete_segment (char* ID_segmento, int conexion, t_contexto* pcb){

    int ID = atoi(ID_segmento);
    enviar_int (conexion, &ID, DELETE_SEGMENT);

    t_paquete* paquete = malloc(sizeof(t_paquete));
	paquete->buffer = malloc(sizeof(t_buffer)); 

    recv(conexion, &(paquete->codigo_de_op), sizeof(int), 0);
	// Después ya podemos recibir el buffer. Primero su tamaño seguido del contenido
	recv(conexion, &(paquete->buffer->size), sizeof(int), 0);
	paquete->buffer->stream = malloc(paquete->buffer->size);
	recv(conexion, paquete->buffer->stream, paquete->buffer->size, 0);

    list_destroy_and_destroy_elements(pcb->tabla_segmentos,free);
	pcb->tabla_segmentos=deserializar_tabla_de_segmentos(paquete->buffer);

}

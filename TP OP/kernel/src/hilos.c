    #include "../include/hilos.h"
 
//-----------------------HILOS01------------------------------------------------------------------------

hilo_1* inicializar_Hilo1 (char* puerto_escucha, t_list* lista_de_socket, sem_t* sem_new_process, pthread_mutex_t* mutex_soket){

    hilo_1* hilo1 = malloc(sizeof(hilo_1));

    hilo1->puerto_escucha=puerto_escucha;
    hilo1->lista_de_socket=lista_de_socket;
    hilo1->sem_new_process=sem_new_process;
    hilo1->mutex_soket=mutex_soket;
    return hilo1;
}

void* funcion_Hilo1 (void* argumentos_hilo1){ //Espera conexiones y crea la lista de sockets


    hilo_1* argumento_hilo1 = (hilo_1*)argumentos_hilo1;



    t_log* logger_hilo1 = iniciar_logger("./logskernelhilo1.log", "Log-kernel-hilo1");
	log_debug(logger_hilo1, "Se inició el logger hilo1");


    //Iniciamos el servidor
    int server_consolas = iniciar_servidor(logger_hilo1, argumento_hilo1->puerto_escucha);
    log_info(logger_hilo1, "Servidor listo para recibir a la consola");

    while (true){

        int socket = esperar_cliente(server_consolas, logger_hilo1);//Esperar al cliente

        lock_mutex(argumento_hilo1->mutex_soket,logger_hilo1);// BLOQUEA EL SEMAFORO
        list_add(argumento_hilo1->lista_de_socket, &socket); //modifica la lista
        unlock_mutex(argumento_hilo1->mutex_soket,logger_hilo1);// DESBLOQUEA EL SEMAFORO
         log_debug(logger_hilo1, "Se conectó una consola");

        if ( sem_post (argumento_hilo1->sem_new_process) != 0){ // +1 al semaforo del hilo 2 creador de PCB
            log_error (logger_hilo1, "ERROR CON SEMAFORO PARA INICIAR LA CREACION DEL PCB. Hilo 1 -> 2");
            exit (10);
        } 
    }
    
    return NULL;

}

//-----------------------HILOS02------------------------------------------------------------------------

hilo_2* inicializar_Hilo2 (t_list* lista_de_socket, t_list* lista_de_conexiones, t_list* lista_de_PCBs_NEW, sem_t* sem_new_process, sem_t* semaforo_FinReady_De_procesos, t_config* config, pthread_mutex_t* mutex_Lista_NEW, pthread_mutex_t* mutex_Lista_conexiones, pthread_mutex_t* mutex_soket){

    hilo_2* hilo2 = malloc(sizeof(hilo_2));
       
    hilo2->lista_de_socket=lista_de_socket;
    hilo2->lista_de_conexiones=lista_de_conexiones;
    hilo2->lista_de_PCBs_NEW=lista_de_PCBs_NEW;
    hilo2->sem_new_process=sem_new_process;
    hilo2->semaforo_FinReady_De_procesos=semaforo_FinReady_De_procesos;
    hilo2->config=config;
    hilo2->mutex_Lista_NEW=mutex_Lista_NEW;
    hilo2->mutex_Lista_conexiones=mutex_Lista_conexiones;
    hilo2->mutex_soket=mutex_soket;
    return hilo2;
}


void* funcion_Hilo2 (void* argumentos){ // Crea los PCB y los agrega a new

    hilo_2* argumento = (hilo_2 *)argumentos;


    t_log* logger_hilo2 = iniciar_logger("./logskernelhilo2.log", "Log-kernel-hilo2");
	log_debug(logger_hilo2, "Se inició el logger hilo2");


    int PID=0;
    t_PCB* PCB_aux;
    t_conexion* aux;
    
    while (true){
        sem_wait (argumento->sem_new_process); // Espera a que haya un nuevo proceso
        lock_mutex(argumento->mutex_soket,logger_hilo2);
        int* socket = list_remove(argumento->lista_de_socket,0);
        unlock_mutex(argumento->mutex_soket,logger_hilo2);

        //cargamos el nodo aux
        aux = malloc( sizeof (t_conexion) );
        aux->socket = *socket;
        aux->PID = PID;

        lock_mutex(argumento->mutex_Lista_conexiones,logger_hilo2);
        list_add (argumento->lista_de_conexiones, aux);
        unlock_mutex(argumento->mutex_Lista_conexiones,logger_hilo2);
        log_debug(logger_hilo2, "Se añadio la conexion a la lista de conexiones");

        //Recibimos las instrucciones del kernel
	    char* lista_de_instrucciones = recibir_paquete(*socket);
        log_debug(logger_hilo2, "Se recibio las instrucciones");

	    //Creamos el PCB y lo cargamos en un PCB auxiliar,
	    PCB_aux = crear_PCB (lista_de_instrucciones, argumento->config, PID);

        log_info(logger_hilo2, "Creación de Proceso: Se crea el proceso %d en NEW", PID);
	    //Cargamos el PCB auxiliar en la lista

        lock_mutex(argumento->mutex_Lista_NEW,logger_hilo2);// BLOQUEA EL SEMAFORO
	    list_add (argumento->lista_de_PCBs_NEW, PCB_aux);
        unlock_mutex(argumento->mutex_Lista_NEW, logger_hilo2);// DESBLOQUEA EL SEMAFORO
        log_debug(logger_hilo2, "Se añadio la pcb a la cola de pcb-new");

        if ( sem_post (argumento->semaforo_FinReady_De_procesos) != 0 ){
            log_error (logger_hilo2, "ERROR CON SEMAFORO PARA INICIAR EL PROGRAMA. Hilo 2 -> 5");
            exit(22);
        }
        log_debug(logger_hilo2, "habilitando el semaforo del hilo 5");
        PID++;

    }
    return NULL;

}

//-----------------------HILOS03------------------------------------------------------------------------

hilo_3* inicializar_Hilo3 (t_list* lista_de_PCBs_READY, int conexion_cpu, int conexion_filesystem, int conexion_memoria, t_list* lista_de_PCBs_ENDED, t_dictionary* diccionario_recurso, sem_t* sem_recursos, sem_t* semaforo_FinReady_De_procesos, pthread_mutex_t* mutex_Lista_READY, pthread_mutex_t* mutex_Lista_PCBs_ENDED, sem_t* semaforo_hay_algo_listo, t_config* config, t_temporal* temporizador_global, t_archivos_kernel archivos, sem_t* semaforo_pet_fs, t_peticiones peticiones, t_hilo_4_LOGS hilo_4_logs, sem_t* binario_open, pthread_mutex_t* mutex_pet_FS, t_compactacion* lista_pcbs_creados, int alfa){
    
    hilo_3* hilo3 = malloc(sizeof(hilo_3));
    hilo3->lista_de_PCBs_READY=lista_de_PCBs_READY;
    hilo3->conexion_cpu=conexion_cpu;
    hilo3->conexion_filesystem=conexion_filesystem;
    hilo3->conexion_memoria=conexion_memoria;
    hilo3->lista_de_PCBs_ENDED=lista_de_PCBs_ENDED;
    hilo3->semaforo_FinReady_De_procesos=semaforo_FinReady_De_procesos;
    hilo3->mutex_Lista_READY=mutex_Lista_READY;
    hilo3->mutex_Lista_PCBs_ENDED=mutex_Lista_PCBs_ENDED;
    hilo3->semaforo_hay_algo_listo=semaforo_hay_algo_listo;
    hilo3->diccionario_recurso=diccionario_recurso;
    hilo3->sem_recursos=sem_recursos;
    hilo3->config=config;
    hilo3->temporizador_global=temporizador_global;
    hilo3->archivos=archivos;
    hilo3->semaforo_pet_fs=semaforo_pet_fs;
    hilo3->peticiones=peticiones;
    hilo3->hilo_4_logs=hilo_4_logs;
    hilo3->binario_open=binario_open;
    hilo3->mutex_pet_FS=mutex_pet_FS;
    hilo3->lista_pcbs_creados=lista_pcbs_creados;
    hilo3->alfa=alfa;
    return hilo3;
}


void* funcion_Hilo3 (void* argumentos){ //Planificador de corto plazo

    hilo_3* argumento = (hilo_3 *)argumentos;

    char* algortimo_planificacion = config_get_string_value(argumento->config, "ALGORITMO_PLANIFICACION");

    t_log* logger_hilo3 = iniciar_logger("./logskernelhilo3.log", "Log-kernel-hilo3");
	log_debug(logger_hilo3, "Se inició el logger hilo3");
    
    while(true){
        sem_wait(argumento->semaforo_hay_algo_listo);


        
        if ( string_equals_ignore_case(algortimo_planificacion, "HRRN") ){
            lock_mutex(argumento->mutex_Lista_READY, logger_hilo3);
            ordenar_lista_HRRN(argumento->lista_de_PCBs_READY, argumento->temporizador_global);
            unlock_mutex(argumento->mutex_Lista_READY, logger_hilo3);
        }

        char* lista_pids_ready = pasar_lista_string(argumento->lista_de_PCBs_READY);
        log_info(logger_hilo3 , "Cola Ready %s: %s", algortimo_planificacion , lista_pids_ready);
        
        //Funcion que chequee FIFO a HRRN ooooo Funcion de ordenar la lista




        lock_mutex(argumento->mutex_Lista_READY, logger_hilo3);// BLOQUEA EL SEMAFORO

        t_PCB* pcb_enviado = list_remove (argumento->lista_de_PCBs_READY, 0);

        unlock_mutex(argumento->mutex_Lista_READY, logger_hilo3);// DESBLOQUEA EL SEMAFORO

             
        //ENVIA AL CPU
        //...
        pcb_enviado->razon_bloque = IO;


        t_contexto* contexto_enviado=malloc(sizeof(t_contexto));
        contexto_enviado->pid = pcb_enviado->pid;
        contexto_enviado->program_counter=pcb_enviado->program_counter;
        contexto_enviado->razon_finalizado=pcb_enviado->razon_finalizado;
        contexto_enviado->tiempo_de_bloqueo=pcb_enviado->tiempo_de_bloqueo;
        contexto_enviado->registros_cpu=pcb_enviado->registros_cpu;
        contexto_enviado->tabla_segmentos=pcb_enviado->tabla_segmentos;
        contexto_enviado->instrucciones=pcb_enviado->instrucciones;    
        
        if ( enviar_contexto (argumento->conexion_cpu, contexto_enviado) == -1){
            log_error (logger_hilo3, "ERROR DE ENVIO DE PCB AL CPU, en hilo 3");
            exit(31);
        }
        free(contexto_enviado);
        log_info(logger_hilo3, "PID: %d - Estado Anterior: READY - Estado Actual: RUNNING", pcb_enviado->pid);

         
        int64_t tiempo_process_start = temporal_gettime(argumento->temporizador_global);

        //RECIBE DEL CPU
        recibir_paquete_cpu (argumento->conexion_cpu, argumento->conexion_filesystem, argumento->conexion_memoria, argumento->diccionario_recurso, argumento->sem_recursos, logger_hilo3, pcb_enviado, argumento->archivos, argumento->lista_de_PCBs_READY, argumento->mutex_Lista_READY, argumento->semaforo_hay_algo_listo, argumento->temporizador_global, argumento->peticiones, argumento->binario_open, argumento->mutex_pet_FS, *argumento->lista_pcbs_creados, argumento->alfa, argumento->semaforo_pet_fs);
        log_debug(logger_hilo3, "Se recibio el pcb");

        log_debug(logger_hilo3, "Estado actual %d", pcb_enviado->estado_actual);

        log_debug(logger_hilo3, "Actualizando el tiempo del hilo 3");

        pcb_enviado->tiempo_de_ejecucion = temporal_gettime(argumento->temporizador_global)- tiempo_process_start +  pcb_enviado->tiempo_de_ejecucion;

        switch (pcb_enviado->estado_actual) {
        case READY:
            agregar_proceso_READY(logger_hilo3, pcb_enviado, argumento->lista_de_PCBs_READY, argumento->mutex_Lista_READY, argumento->semaforo_hay_algo_listo, argumento->temporizador_global, argumento->alfa);
            log_info(logger_hilo3, "PID: %d - Estado Anterior: RUNNING - Estado Actual: READY", pcb_enviado->pid);

            break;
        

        case BLOCK:
        //SUPONIENDO QUE LO SIGUIENTE FUNCIONA Y CREA SU PROPIO HILO
            log_info(logger_hilo3, "PID: %d - Estado Anterior: RUNNING - Estado Actual: BLOCK", pcb_enviado->pid);
            swich_Bloqueados(logger_hilo3, pcb_enviado, argumento->lista_de_PCBs_READY, argumento->mutex_Lista_READY, argumento->semaforo_hay_algo_listo, argumento->config, argumento->temporizador_global, argumento->diccionario_recurso, argumento->archivos, argumento->semaforo_pet_fs, argumento->hilo_4_logs);
        break;

        case ENDED:
            lock_mutex(argumento->mutex_Lista_PCBs_ENDED,logger_hilo3);
            list_add (argumento->lista_de_PCBs_ENDED, pcb_enviado);
            unlock_mutex(argumento->mutex_Lista_PCBs_ENDED,logger_hilo3);

            if ( sem_post (argumento->semaforo_FinReady_De_procesos) != 0 ){
                log_error (logger_hilo3, "ERROR CON SEMAFORO PARA FINALIZAR EL PROGRAMA. Hilo 3 -> 5");
                exit(35);
            }
            log_info(logger_hilo3, "PID: %d - Estado Anterior: RUNNING - Estado Actual: EXIT", pcb_enviado->pid);
        break;


        default:
            log_error(logger_hilo3,"ERROR NO COMPATIBILIDAD DE ESTADO");
        break;

        }
        
    }
    return NULL;

}

//-----------------------HILOS04------------------------------------------------------------------------

hilo_4* inicializar_Hilo4 (t_list* lista_PCBs_READY, t_PCB* PCB, pthread_mutex_t* mutex_Lista_READY, sem_t* semaforo_hay_algo_listo, t_config* config, t_temporal* temporizador_global, t_hilo_4_LOGS hilo_4_logs){
    hilo_4* hilo4 = malloc(sizeof(hilo_4));
    hilo4->lista_PCBs_READY=lista_PCBs_READY;
    hilo4->PCB=PCB;
    hilo4->mutex_Lista_READY=mutex_Lista_READY;
    hilo4->semaforo_hay_algo_listo = semaforo_hay_algo_listo;
    hilo4->config=config;
    hilo4->temporizador_global=temporizador_global;
    hilo4->hilo_4_logs=hilo_4_logs;

    return hilo4;
}


void* funcion_Hilo4 (void* argumentos){ //Planificacion ==> Bloqueados
    hilo_4* argumento = (hilo_4 *)argumentos;

    pthread_mutex_lock(argumento->hilo_4_logs.mutex_log);
	log_debug(argumento->hilo_4_logs.logger_hilo4, "Se inició un hilo4");

    log_info(argumento->hilo_4_logs.logger_hilo4, "PID: %d - Ejecuta IO: %d", argumento->PCB->pid, argumento->PCB->tiempo_de_bloqueo);
    pthread_mutex_unlock(argumento->hilo_4_logs.mutex_log);

    int alfa = config_get_int_value(argumento->config,"HRRN_ALFA");

    bloquear_proceso_temporalmente (argumento->lista_PCBs_READY, argumento->PCB, argumento->mutex_Lista_READY, alfa, argumento->temporizador_global);

    
    pthread_mutex_lock(argumento->hilo_4_logs.mutex_log);
    log_info(argumento->hilo_4_logs.logger_hilo4, "PID: %d - Estado Anterior: BLOCK - Estado Actual: READY", argumento->PCB->pid);
    pthread_mutex_unlock(argumento->hilo_4_logs.mutex_log);



    argumento->PCB->estado_actual=READY;
	pthread_mutex_lock(argumento->mutex_Lista_READY);

	argumento->PCB->tiempo_de_llegada_ready=temporal_gettime(argumento->temporizador_global);
	list_add(argumento->lista_PCBs_READY, argumento->PCB);

	pthread_mutex_unlock(argumento->mutex_Lista_READY);

    
    sem_post(argumento->semaforo_hay_algo_listo);

    free(argumento);

    
    return NULL;

}


//-----------------------HILOS05------------------------------------------------------------------------

hilo_5* inicializar_Hilo5 (t_list* lista_de_PCBs_ENDED, t_list* lista_de_conexiones, sem_t* semaforo_FinReady_De_procesos, t_list* lista_PCBs_READY, t_list* lista_de_PCBs_NEW, pthread_mutex_t* mutex_Lista_READY, pthread_mutex_t* mutex_Lista_NEW, pthread_mutex_t* mutex_Lista_conexiones, pthread_mutex_t* mutex_Lista_PCBs_ENDED, sem_t* semaforo_hay_algo_listo, t_dictionary* diccionario_recurso, sem_t* sem_recursos,  t_config* config, t_temporal* temporizador_global, t_compactacion* lista_pcbs_creados, int socket_memoria){
    hilo_5* hilo5 = malloc(sizeof(hilo_5));
    hilo5->lista_de_PCBs_ENDED=lista_de_PCBs_ENDED;
    hilo5->lista_de_conexiones=lista_de_conexiones;
    hilo5->diccionario_recurso=diccionario_recurso;
    hilo5->sem_recursos=sem_recursos;
    hilo5->semaforo_FinReady_De_procesos=semaforo_FinReady_De_procesos;
    hilo5->lista_PCBs_READY=lista_PCBs_READY;
    hilo5->lista_de_PCBs_NEW=lista_de_PCBs_NEW;
    hilo5->mutex_Lista_READY=mutex_Lista_READY;
    hilo5->mutex_Lista_NEW=mutex_Lista_NEW;
    hilo5->mutex_Lista_conexiones=mutex_Lista_conexiones;
    hilo5->mutex_Lista_PCBs_ENDED=mutex_Lista_PCBs_ENDED;
    hilo5->semaforo_hay_algo_listo=semaforo_hay_algo_listo;
    hilo5->config=config;
    hilo5->temporizador_global=temporizador_global;
    hilo5->lista_pcbs_creados=lista_pcbs_creados;
    hilo5->socket_memoria=socket_memoria;
    return hilo5;
}

void* funcion_Hilo5 (void* argumentos){ //Termina conexiones + Exit
    hilo_5* argumento =(hilo_5 *)argumentos;
    int grado_MultiProgram = config_get_int_value(argumento->config,"GRADO_MAX_MULTIPROGRAMACION");

    t_log* logger_hilo5 = iniciar_logger("./logskernelhilo5.log", "Log-kernel-hilo5");
	log_debug(logger_hilo5, "Se inició el logger hilo5");



    while(true){
        log_debug(logger_hilo5,"Esperando nuevo pcb o finalizacion del pcb");
        sem_wait (argumento->semaforo_FinReady_De_procesos); //Cambiar nombre pq es el semaforo compartido 
        log_debug(logger_hilo5, "SE HABILITA EL HILO 5");
        if (!list_is_empty (argumento->lista_de_PCBs_ENDED)){ //Condición si hay algo que liberar (ENDED -> consola)

            log_debug(logger_hilo5, "SE VA A CERRAR LA CONSOLA");

            log_debug(logger_hilo5, "LOCK MUTEX  PCB END 5");
            lock_mutex(argumento->mutex_Lista_PCBs_ENDED,logger_hilo5);
            t_PCB* pcb_finalizado = list_remove (argumento->lista_de_PCBs_ENDED, 0);
            unlock_mutex(argumento->mutex_Lista_PCBs_ENDED,logger_hilo5);
            log_debug(logger_hilo5, "UNLOCK MUTEX  PCB END 5");

            char* razon;
            switch (pcb_finalizado->razon_finalizado)
            {
            case FIN_SUCCESS:
                razon="FIN_SUCCESS";
                break;
            case FIN_SEG_FAULT:
                razon="FIN_SEG_FAULT";
                break;
            case FIN_INVALID_RESOURCE:
                razon="FIN_INVALID_RESOURCE";
                break;
            case FIN_OUT_OF_MEMORY:
                razon="OUT_OF_MEMORY";
                break;
            case ERROR:
                razon="ERROR";
                break;
            
            default:
                razon="RAZON DESCONOCIDA";
                break;
            }
            log_info(logger_hilo5,"Fin de Proceso: Finaliza el proceso %d - Motivo: %s", pcb_finalizado->pid, razon);



            //PARA PONER EN LA COMMONS
            
            bool buscarConexion (void* conexionBuscada){
                t_conexion* laConexion = (t_conexion*) conexionBuscada;
                return buscarConexionSegunPid (pcb_finalizado->pid, laConexion);
            };
            
            lock_mutex(argumento->mutex_Lista_conexiones,logger_hilo5);
            t_conexion* conexion = list_remove_by_condition(argumento->lista_de_conexiones, buscarConexion); //confirmar si funciona este mostro feo         
            unlock_mutex(argumento->mutex_Lista_conexiones,logger_hilo5);
            log_debug(logger_hilo5, "CONEXION ENCONTRADA");


            //liberar_recursos_asignados(pcb_finalizado->recursos_asignados, argumento->diccionario_recurso, argumento->sem_recursos, logger_hilo5);
            log_debug(logger_hilo5, "LIBERAR RECURSO   5");
            lock_mutex(argumento->lista_pcbs_creados->mutex_memoria,logger_hilo5);
            terminar_proceso (pcb_finalizado, conexion,argumento->socket_memoria, argumento->lista_pcbs_creados->pcbs_creados); //Completar esta funcion para que funcione y liberar el pcb 
            unlock_mutex(argumento->lista_pcbs_creados->mutex_memoria,logger_hilo5);
            log_debug(logger_hilo5, "TERMINAR PCB  5");
            
            log_debug(logger_hilo5, "Se cerro una consola");
            grado_MultiProgram++;
        //terminar todo lo que tenga q ver con este pcb
        }

        if (!list_is_empty(argumento->lista_de_PCBs_NEW) &&  0 < grado_MultiProgram){//Condición si hay algo que agregar 
            //RECORDAR agregar mutex en hilo 3 para esto
            log_debug(logger_hilo5, "Lista_NEW");
            lock_mutex(argumento->mutex_Lista_NEW,logger_hilo5);// BLOQUEA EL SEMAFORO
            log_debug(logger_hilo5, "Lista_READY");
            lock_mutex(argumento->mutex_Lista_READY,logger_hilo5);// BLOQUEA EL SEMAFORO
            log_debug(logger_hilo5, "memoria");
            lock_mutex(argumento->lista_pcbs_creados->mutex_memoria,logger_hilo5);
            mover_proceso_NEW_a_READY (logger_hilo5, argumento->lista_de_PCBs_NEW, argumento->lista_PCBs_READY, argumento->mutex_Lista_READY, argumento->semaforo_hay_algo_listo, argumento->temporizador_global, argumento->socket_memoria,argumento->lista_pcbs_creados->pcbs_creados);
            unlock_mutex(argumento->lista_pcbs_creados->mutex_memoria,logger_hilo5);
            unlock_mutex(argumento->mutex_Lista_NEW,logger_hilo5);// DESBLOQUEA EL SEMAFORO
            unlock_mutex(argumento->mutex_Lista_READY,logger_hilo5);// DESBLOQUEA EL SEMAFORO
            grado_MultiProgram--;
            log_debug(logger_hilo5, "Se agrego un proceso nuevo a ready");
        }

    }
    return NULL;
}

//-----------------------OTROS------------------------------------------------------------------------

bool buscarConexionSegunPid (int pid_a_encontrar, t_conexion* conexion){
    return conexion->PID == pid_a_encontrar;
}

void swich_Bloqueados (t_log* logger, t_PCB* pcb, t_list* lista_de_PCBs_READY, pthread_mutex_t* mutex_Lista_READY, sem_t* semaforo_hay_algo_listo, t_config* config, t_temporal* temporizador_global, t_dictionary* diccionario_recurso, t_archivos_kernel archivos, sem_t* semaforo_pet_fs, t_hilo_4_LOGS hilo_4_logs){
    

    switch (pcb->razon_bloque)
    {
    case IO:

        log_info(logger,"PID: %d - Bloqueado por: IO", pcb->pid);
        pthread_t hilo4;
        
        hilo_4* argumento_De_Hilo4 = inicializar_Hilo4(lista_de_PCBs_READY, pcb, mutex_Lista_READY, semaforo_hay_algo_listo, config, temporizador_global,hilo_4_logs);
        if ( pthread_create (&hilo4, NULL, funcion_Hilo4, (void*)argumento_De_Hilo4) != 0) {
            log_error (logger, "ERROR CREANDO UN HILO 4. Hilo 3");
            exit (32);
        }

        if ( pthread_detach (hilo4) != 0) {
            log_error(logger, "ERROR FINALIZANDO UN HILO 4. Hilo 3");
            exit (33);
        }
        
        
        break;

    
    case RECURSO:
        t_recurso* recurso = dictionary_get(diccionario_recurso, pcb->recurso);
        pthread_mutex_lock(recurso->mutex_cola_espera);
        queue_push(recurso->espera, pcb);
        pthread_mutex_unlock(recurso->mutex_cola_espera);

        log_info(logger,"PID: %d - Bloqueado por: %s", pcb->pid, recurso->nombre);
        
        break;

    case ARCHIVO:

        // Lo hace: abrir_archivo ==> archivos.c
        //log_info(logger,"PID: %d - Bloqueado por: %s", pcb->pid, archivo->nombre);
        break;

    case PET_FS:

        sem_post(semaforo_pet_fs);

        break;

    default:
        log_error(logger, "RAZON DE BLOQUEO NO DESIGNADA");
        break;
    }
}

//-----------------------crear_t_hilo_4_LOGS------------------------------------------------------------------------

t_hilo_4_LOGS crear_t_hilo_4_LOGS(void){
    t_hilo_4_LOGS r;
    r.logger_hilo4=iniciar_logger("./logskernel-hilo4.log", "Log-kernel-hilo4");//cambiar a tlist @Bruno
    
	pthread_mutex_t* mutex_pet_FS = malloc(sizeof(pthread_mutex_t));
	if ( pthread_mutex_init(mutex_pet_FS, NULL) != 0){
		log_error(r.logger_hilo4,"ERROR INICIAR EL MUTEX");
		exit(3);
	}

    r.mutex_log = mutex_pet_FS;

    
    return r;
}
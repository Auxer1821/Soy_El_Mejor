#include "../include/recursos.h"

//-----------------------inicializarRecursos------------------------------------------------------------------------

t_dictionary* inicializarRecursos(t_config* config, t_log* logger){

    t_dictionary* recursos = dictionary_create();

    log_debug(logger, "Se crea el diccionario");
    char** recursos_config = config_get_array_value(config, "RECURSOS");
	char** instancias_recursos_config = config_get_array_value(config, "INSTANCIAS_RECURSOS");
    log_debug(logger, "Se utiliza la config de los recursos");

    t_recurso* recurso;
    

    for (int i=0; recursos_config[i] != NULL;  i++)
    {
        recurso = malloc(sizeof(t_recurso));
        
        recurso->nombre= recursos_config[i];
        recurso->instancias_usadas = 0;
        recurso->instancias_totales = atoi(instancias_recursos_config[i]);
        recurso->espera=queue_create();

        
        pthread_mutex_t* mutex_cola_espera= malloc(sizeof(pthread_mutex_t));		// hilos {4,R}
	    if ( pthread_mutex_init(mutex_cola_espera, NULL) != 0){
		log_error(logger,"ERROR INICIAR EL MUTEX");
		exit(3);
        }
        recurso->mutex_cola_espera=mutex_cola_espera;

        pthread_mutex_t* mutex_instancia_usada=malloc(sizeof(pthread_mutex_t));		// hilos {REs}
	    if ( pthread_mutex_init(mutex_instancia_usada, NULL) != 0){
		log_error(logger,"ERROR INICIAR EL MUTEX");
		exit(3);
        }
        recurso->mutex_instancia_usada=mutex_instancia_usada;

        dictionary_put(recursos, recurso->nombre, recurso);
        log_debug(logger, "Se agrego un recurso al diccionaro");

    }

    return recursos;
}

//-----------------------liberarRecursos------------------------------------------------------------------------

void liberarRecursos(t_dictionary* recursos){

    dictionary_iterator(recursos,liberar_recurso);
}

//-----------------------liberar_recurso------------------------------------------------------------------------

void liberar_recurso(char* key, void* element){

    t_recurso* recurso = (t_recurso*)element;

    pthread_mutex_destroy(recurso->mutex_cola_espera);
    pthread_mutex_destroy(recurso->mutex_instancia_usada);
    queue_destroy(recurso->espera);
    free(recurso->mutex_cola_espera);
    free(recurso->mutex_instancia_usada);

    free(recurso);
    
}

//-----------------------inicializar_Hilo_Recursos------------------------------------------------------------------------

hilo_Recursos* inicializar_Hilo_Recursos(t_dictionary* recursos, sem_t* sem_recursos, t_list* lista_PCBs_READY, pthread_mutex_t* mutex_Lista_READY, sem_t* semaforo_hay_algo_listo, t_temporal* temporizador_global,int alfa){
    
    hilo_Recursos* hilo_recursos = malloc(sizeof(hilo_Recursos));

    hilo_recursos->recursos=recursos;
    hilo_recursos->sem_recursos=sem_recursos;
    hilo_recursos->lista_PCBs_READY=lista_PCBs_READY;
    hilo_recursos->mutex_Lista_READY=mutex_Lista_READY;
    hilo_recursos->semaforo_hay_algo_listo=semaforo_hay_algo_listo;
    hilo_recursos->temporizador_global=temporizador_global;
    hilo_recursos->alfa=alfa;

    return hilo_recursos;

}

//-----------------------funcion_Hilo_Recursos------------------------------------------------------------------------

void* funcion_Hilo_Recursos(void* argumento){

    hilo_Recursos* hilo_recursos = (hilo_Recursos*)argumento;

    t_log* logger_hiloR = iniciar_logger("./logskernelRecurso.log", "Log-kernel-hilo-recurso");
	log_debug(logger_hiloR, "Se inició el logger del hilo recurso");



    while (1)
    {
        sem_wait(hilo_recursos->sem_recursos);

        void funcion_recurso_dictionary(char* key, void* element){
            funcion_recurso(key, element, hilo_recursos->sem_recursos, hilo_recursos->lista_PCBs_READY, hilo_recursos->mutex_Lista_READY, hilo_recursos->semaforo_hay_algo_listo, logger_hiloR, hilo_recursos->temporizador_global, hilo_recursos->alfa);
        }



        dictionary_iterator(hilo_recursos->recursos,funcion_recurso_dictionary);

    }
    

}

//-----------------------funcion_recurso------------------------------------------------------------------------

void funcion_recurso(char* key, void* element, sem_t* sem_recursos, t_list* lista_PCBs_READY, pthread_mutex_t* mutex_Lista_READY, sem_t* semaforo_hay_algo_listo, t_log* logger, t_temporal* temporizador_global, int alfa){
    
    t_recurso* recurso = (t_recurso*)element;


        lock_mutex(recurso->mutex_instancia_usada, logger);
    if(recurso->instancias_totales > recurso->instancias_usadas && !queue_is_empty(recurso->espera)){


        lock_mutex(recurso->mutex_cola_espera, logger);
        t_PCB* pcb = queue_pop(recurso->espera);
        recurso->instancias_usadas++;
        unlock_mutex(recurso->mutex_cola_espera, logger);


        /*
        int leng = strlen(key)+1;
        char* aux = malloc (leng);
        memcpy(aux, key, leng);
        list_add(pcb->recursos_asignados,aux);
        */
        agregar_proceso_READY(logger, pcb, lista_PCBs_READY, mutex_Lista_READY, semaforo_hay_algo_listo, temporizador_global, alfa);
        log_info( logger , "PID: %d - Estado Anterior: BLOCK - Estado Actual: READY", pcb->pid);


        /* BORRAR DESPUES, cuando funciones
        pcb->estado_actual=READY;
        
        lock_mutex(mutex_Lista_READY, logger);
        list_add(lista_PCBs_READY, pcb);
        pcb->tiempo_de_llegada_ready=temporal_create();
        unlock_mutex(mutex_Lista_READY, logger);
        
        sem_post(semaforo_hay_algo_listo);
        */   
    }
        unlock_mutex(recurso->mutex_instancia_usada, logger);


}

//-----------------------asignar_recurso------------------------------------------------------------------------

int asignar_recurso(t_recurso* recurso, t_log* logger){ //0 [false];    1 [true]
    int ret = 0;
    if(recurso->instancias_totales > recurso->instancias_usadas){
        lock_mutex(recurso->mutex_instancia_usada, logger);
        recurso->instancias_usadas++;
        unlock_mutex(recurso->mutex_instancia_usada, logger);
        ret = 1;
    }

    return ret;
}

//-----------------------liberar_instancia_recurso------------------------------------------------------------------------

void liberar_instancia_recurso(t_recurso* recurso, sem_t* sem_recursos, t_log* logger){

    lock_mutex(recurso->mutex_instancia_usada, logger);
        recurso->instancias_usadas--;
    unlock_mutex(recurso->mutex_instancia_usada, logger);
    
    sem_post(sem_recursos);

}

//-----------------------liberar_recursos_asignados------------------------------------------------------------------------

void liberar_recursos_asignados(t_list* list, t_dictionary* recursos, sem_t* sem_recursos, t_log* logger){

    int len = list_size(list);
    for (int i = 0; i < len; i++)
    {
        char* key = list_get(list,i);
        t_recurso* recurso= dictionary_get(recursos,key);
        liberar_instancia_recurso(recurso, sem_recursos ,logger);
    }

}

//-----------------------string_to_int------------------------------------------------------------------------

int string_to_int( char* numero ){

    int r=0;
    int e=1;
    for(int i= string_length(numero)-1 ; i>=0 ; i-- )
    {
        r+=(numero[i]-'0')*e;
        e*=10;
    }

    return e;
}

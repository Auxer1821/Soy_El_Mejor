#include "../include/controlFS.h"

//-----------------------IniciarArgumentos------------------------------------------------------------------------

t_hilo_pet_FS* inicializar_peticiones_FS(int socket_filesystem, t_peticiones peticiones, t_list* lista_ready, pthread_mutex_t* mutex_pet_fs, pthread_mutex_t* mutex_ready, sem_t* semaforo_pet_fs, sem_t* sem_habilitar_hilo3, t_temporal* temporizador_global, sem_t* binario_open,int alfa){
    
    t_hilo_pet_FS* hilo_pet_fs = malloc(sizeof(t_hilo_pet_FS));
    
    hilo_pet_fs->socket_filesystem=socket_filesystem;
	hilo_pet_fs->peticiones=peticiones;
    hilo_pet_fs->lista_ready=lista_ready;
    hilo_pet_fs->mutex_pet_fs=mutex_pet_fs;
    hilo_pet_fs->mutex_ready=mutex_ready;
    hilo_pet_fs->semaforo_pet_fs=semaforo_pet_fs;
    hilo_pet_fs->sem_habilitar_hilo3=sem_habilitar_hilo3;
    hilo_pet_fs->temporizador_global=temporizador_global;
    hilo_pet_fs->binario_open=binario_open;
    hilo_pet_fs->alfa=alfa;


    return hilo_pet_fs;

}

//-----------------------MAIN------------------------------------------------------------------------

void* hiloPeticionFS(void* args){

    t_hilo_pet_FS* argumento = (t_hilo_pet_FS*) args;
    
    t_log* logger = iniciar_logger("./logFS.log", "LogFS");
    log_debug(logger,"iniciar hilo pet");


    while (1)
    {
        sem_wait(argumento->semaforo_pet_fs); //cantidad de petisiones
        lock_mutex(argumento->mutex_pet_fs, logger); //wait a compactación

        t_PCB* proceso = enviar_pet_fs(argumento->peticiones, argumento->socket_filesystem, logger);

        recibir_bool(argumento->socket_filesystem);
        log_debug(logger,"TERMINO ALGO DE FS");

        if(proceso!=NULL){//read write truncate
            log_info( logger , "PID: %d - Estado Anterior: BLOCK - Estado Actual: READY", proceso->pid);
            agregar_proceso_READY(logger, proceso, argumento->lista_ready, argumento->mutex_ready, argumento-> sem_habilitar_hilo3, argumento->temporizador_global, argumento->alfa);
        }
        unlock_mutex(argumento->mutex_pet_fs, logger);
    }
    
    return NULL;
}

//-----------------------enviar_pet_fs------------------------------------------------------------------------

t_PCB* enviar_pet_fs(t_peticiones peticiones, int soketFS, t_log* logger)
{
    lock_mutex(peticiones.mutex, logger);

    t_peticion* peticion = list_remove(peticiones.peticiones,0);
    

    t_PCB* proceso = peticion->proceso;

    log_debug(logger,"PID: %d, PET: %s, COOP:%d",proceso->pid,peticion->peticion,peticion->codigo_op);

    enviar_string(soketFS, peticion->peticion, peticion->codigo_op);

    if(peticion->codigo_op==F_OPEN) proceso=NULL;
    
    free(peticion);

    

    unlock_mutex(peticiones.mutex, logger);
    return proceso;
}

//-----------------------crear_t_peticiones------------------------------------------------------------------------

t_peticiones crear_t_peticiones(void){
    t_peticiones r;
    r.peticiones= list_create();//cambiar a tlist @Bruno
    
	pthread_mutex_t* mutex_pet_FS = malloc(sizeof(pthread_mutex_t));
	if ( pthread_mutex_init(mutex_pet_FS, NULL) != 0){
		printf("ERROR INICIAR EL MUTEX");
		exit(3);
	}

    r.mutex = mutex_pet_FS;

    
    return r;
}


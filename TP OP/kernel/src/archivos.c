#include "../include/utils.h"

//-----------------------Iniciar_Diccionario_Global------------------------------------------------------------------------

t_archivos_kernel inicializar_diccionario_archivos (t_log* logger){
    t_archivos_kernel r;

	pthread_mutex_t* mutex_pcb_bloq = malloc(sizeof(pthread_mutex_t));
	
	if ( pthread_mutex_init(mutex_pcb_bloq, NULL) != 0){
		log_error(logger,"ERROR INICIAR EL MUTEX");
		exit(3);
	}
    r.mutex_pcb_bloq = mutex_pcb_bloq;
    r.Tabla_de_Archivos_Globales = dictionary_create();

    return r;
}

//-----------------------abrir_archivo-------------------------------------------------------------------------------------

int abrir_archivo (t_log* logger, t_archivos_kernel archivos, t_PCB* proceso, char* nombre_archivo, int socket_filesystem, sem_t* binario_open , t_peticiones peticiones){

    int r;
    lock_mutex(archivos.mutex_pcb_bloq, logger);
    if (dictionary_has_key(archivos.Tabla_de_Archivos_Globales, nombre_archivo)) //Caso YA ABIERTO
    {
        log_debug(logger,"CASO ARCHIVO ABIERTO ");
        t_archivos* archivo = dictionary_get(archivos.Tabla_de_Archivos_Globales, nombre_archivo);
        proceso->razon_bloque = ARCHIVO;
        list_add(archivo->pcb_bloqueados, proceso);
        log_info(logger,"PID: %d - Bloqueado por: %s", proceso->pid, archivo->nombre);
        r=1;
    }

    else //Caso NO ESTABA ABIERTO
    {
        log_debug(logger,"CASO ARCHIVO NO ABIERTO");
        char* peticion_char= string_duplicate(nombre_archivo);

        t_peticion* peticion = malloc(sizeof(t_peticion));
        peticion->codigo_op=F_OPEN;
        peticion->peticion=peticion_char;
        peticion->proceso=proceso;

        lock_mutex(peticiones.mutex, logger);

        list_add(peticiones.peticiones,peticion);

        unlock_mutex(peticiones.mutex, logger);


        //segir una vez q recibe
    
        t_archivos* archivo = malloc(sizeof(t_archivos));
        char* key_archivo_global= string_duplicate(nombre_archivo);
        archivo->nombre = key_archivo_global;
        archivo->pcb_bloqueados = list_create();

        list_add(archivo->pcb_bloqueados,proceso);
       
        int* p = malloc(sizeof(int));
        *p=0;
        log_debug(logger,"creacion del archivo en el diccionario de kernel");

        char* key_archivo= string_duplicate(nombre_archivo);
        dictionary_put( proceso->tabla_de_archivos , key_archivo , p);

        dictionary_put(archivos.Tabla_de_Archivos_Globales, key_archivo_global, archivo);

        log_debug(logger,"creado del archivo en el diccionario de kernel");

        r=0;
    

    }
    
    unlock_mutex(archivos.mutex_pcb_bloq, logger);

    return r;

}

//-----------------------cerrar_archivo------------------------------------------------------------------------

void cerrar_archivo (t_log* logger, t_archivos_kernel archivos, t_PCB* proceso, char* nombre_archivo, t_list* lista_ready, pthread_mutex_t* mutex_ready, sem_t* sem_habilitar_hilo3, t_temporal* temporizador_global, int alfa){
    //Chequeo para que no se rompa
    dictionary_remove_and_destroy(proceso->tabla_de_archivos, nombre_archivo, free);
    lock_mutex(archivos.mutex_pcb_bloq, logger);
    if(dictionary_has_key(archivos.Tabla_de_Archivos_Globales, nombre_archivo)){

        t_archivos* archivo = dictionary_get(archivos.Tabla_de_Archivos_Globales, nombre_archivo);
        list_remove(archivo->pcb_bloqueados, 0);

        //sacar del proceso pensarlo cuando esta mas armado el hilo 3

        if (list_is_empty(archivo->pcb_bloqueados))
        {
            list_destroy(archivo->pcb_bloqueados);
            free(archivo);
            dictionary_remove(archivos.Tabla_de_Archivos_Globales, nombre_archivo);
        }
        else
        {

            t_PCB* proceso_2 = list_get(archivo->pcb_bloqueados, 0);

            int* p = malloc(sizeof(int));
            *p=0;
            char* key_archivo= string_duplicate(nombre_archivo);
            dictionary_put( proceso_2->tabla_de_archivos , key_archivo , p);

            agregar_proceso_READY(logger, proceso_2, lista_ready, mutex_ready, sem_habilitar_hilo3, temporizador_global, alfa);
            log_info( logger , "PID: %d - Estado Anterior: BLOCK - Estado Actual: READY", proceso_2->pid);
        }
    }
    unlock_mutex(archivos.mutex_pcb_bloq, logger);
    
}



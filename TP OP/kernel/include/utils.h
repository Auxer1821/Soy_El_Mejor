#ifndef INCLUDE_UTILS_H_
#define INCLUDE_UTILS_H_

#include <commons/log.h>
#include <commons/config.h>
#include "../../Shared/include/cliente.h"
#include "../../Shared/include/estructuras_compartidas.h"
#include <stdbool.h>
#include <stdarg.h>
#include <commons/string.h>
#include <pthread.h>
#include <semaphore.h>
#include "recursos.h"

//-----------------------structs[controlFS.h]------------------------------------------------------------------------

typedef struct{
	t_PCB* proceso;
    char* peticion;
    int codigo_op;
}t_peticion;


typedef struct{
	t_list* peticiones;
    pthread_mutex_t* mutex;
}t_peticiones;

typedef struct
{
    char* nombre;
    t_list* pcb_bloqueados;


}t_archivos;

typedef struct
{
    t_dictionary* Tabla_de_Archivos_Globales;
    pthread_mutex_t* mutex_pcb_bloq;   

}t_archivos_kernel;

typedef struct
{
    t_list* pcbs_creados;
    pthread_mutex_t* mutex_memoria;   

}t_compactacion;

//-----------------------Funciones------------------------------------------------------------------------


t_compactacion* inicializar_t_compactacion(t_log* logger);

t_archivos_kernel inicializar_diccionario_archivos(t_log* logger);

int abrir_archivo (t_log* logger, t_archivos_kernel archivos, t_PCB* proceso, char* nombre_archivo, int socket_filesystem, sem_t* binario_open, t_peticiones peticiones);

void cerrar_archivo (t_log* logger, t_archivos_kernel archivos, t_PCB* proceso, char* nombre_archivo, t_list* lista_ready, pthread_mutex_t* mutex_ready, sem_t* sem_habilitar_hilo3, t_temporal* temporizador_global, int alfa);


void leer_config(t_log*, t_config*, char**, char**, char**, char**, char**, char**, char**);


//void crearConexiones(char*,char*,char*,char*,char*,char*);
//void crearConexion(char*, char*, int*);

void recibir_paquete_cpu(int socket_cpu, int socket_filesystem, int socket_memoria, t_dictionary* recursos, sem_t* sem_recursos, t_log* logger, t_PCB* proceso_enviado, t_archivos_kernel archivos_global, t_list* lista_ready, pthread_mutex_t* mutex_ready, sem_t* sem_habilitar_hilo3, t_temporal* temporizador_global, t_peticiones peticiones, sem_t* binario_open, pthread_mutex_t* mutex_pet_FS, t_compactacion lista_psbs_creados, int alfa, sem_t* semaforo_pet_fs);


void recibir_paquete_memoria(t_log* logger, int conexion_memoria, int socket_cpu, t_PCB* proceso_enviado, pthread_mutex_t* mutex_pet_FS, t_list* pcbs_creados);

void actualizar_tablas_segmentos(t_log* logger, int socket_memoria, t_list* lista_pcbs_creados);

void terminar_programa(t_log*, t_config*, int, int, int);
/*
void lock_mutex(pthread_mutex_t* mutex, t_log* logger);

void unlock_mutex(pthread_mutex_t* mutex, t_log* logger);
*/
void agregar_proceso_READY(t_log* logger, t_PCB* proceso, t_list* lista_ready, pthread_mutex_t* mutex, sem_t* semaforo_hay_algo_listo, t_temporal* temporizador_global, int alfa);

void controlFS(t_log* logger, t_peticiones peticiones, int socket_filesystem, t_PCB* proceso_enviado, char* data, int codifo_op);

char* pasar_lista_string(t_list* lista);

#endif /* INCLUDE_UTILS_H_ */

#ifndef controlFS_H_
#define controlFS_H_

#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include<commons/collections/queue.h>
#include <pthread.h>
#include <semaphore.h>
#include "../../Shared/include/servidor.h"
#include "../../Shared/include/estructuras_compartidas.h"
#include "utils.h"
#include "../../Shared/include/logs.h"

//-----------------------structs------------------------------------------------------------------------



typedef struct{
    int socket_filesystem;
	t_peticiones peticiones;
    t_list* lista_ready;
    pthread_mutex_t* mutex_pet_fs;
    pthread_mutex_t* mutex_ready;
    sem_t* semaforo_pet_fs;
    sem_t* binario_open;
    sem_t* sem_habilitar_hilo3;
    t_temporal* temporizador_global;
    int alfa;
}t_hilo_pet_FS;


//-----------------------hiloPeticionFS------------------------------------------------------------------------

void* hiloPeticionFS(void* args);

//-----------------------enviar_pet_fs------------------------------------------------------------------------

t_PCB* enviar_pet_fs(t_peticiones peticiones, int soketFS, t_log* logger);

//-----------------------IniciarArgumentos------------------------------------------------------------------------

t_hilo_pet_FS* inicializar_peticiones_FS(int socket_filesystem, t_peticiones peticiones, t_list* lista_ready, pthread_mutex_t* mutex_pet_fs, pthread_mutex_t* mutex_ready, sem_t* semaforo_pet_fs, sem_t* sem_habilitar_hilo3, t_temporal* temporizador_global, sem_t* binario_open,int alfa);

//-----------------------crear_t_peticiones------------------------------------------------------------------------

t_peticiones crear_t_peticiones(void);


#endif /* controlFS_H_ */

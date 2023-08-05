#ifndef INCLUDE_RECURSOS_H_
#define INCLUDE_RECURSOS_H_


#include<commons/collections/queue.h>
#include <commons/config.h>
#include <commons/string.h>
#include <commons/collections/dictionary.h>
#include <pthread.h>
#include <semaphore.h>

#include "../../Shared/include/estructuras_compartidas.h"
#include "utils.h"
#include "instrucciones.h"
#include "../../Shared/include/logs.h"



typedef struct{
	char * nombre;
    int instancias_totales;
    int instancias_usadas;
    t_queue* espera;
    pthread_mutex_t* mutex_instancia_usada;
    pthread_mutex_t* mutex_cola_espera;
}t_recurso;

t_dictionary* inicializarRecursos(t_config* config, t_log* logger);

void liberarRecursos(t_dictionary* recursos);

void liberar_recurso(char* key, void* element);

typedef struct{
	t_dictionary* recursos;
    sem_t* sem_recursos;
    t_list* lista_PCBs_READY;
    pthread_mutex_t* mutex_Lista_READY;
    sem_t* semaforo_hay_algo_listo; 
    t_temporal* temporizador_global;
    int alfa;
}hilo_Recursos;

hilo_Recursos* inicializar_Hilo_Recursos(t_dictionary* recursos, sem_t* sem_recursos, t_list* lista_PCBs_READY, pthread_mutex_t* mutex_Lista_READY, sem_t* semaforo_hay_algo_listo, t_temporal* temporizador_global,int alfa);

void* funcion_Hilo_Recursos(void*);

void funcion_recurso(char* key, void* element, sem_t* sem_recursos, t_list* lista_PCBs_READY, pthread_mutex_t* mutex_Lista_READY, sem_t* semaforo_hay_algo_listo, t_log* logger, t_temporal* temporizador_global,int alfa);


int asignar_recurso(t_recurso* recurso, t_log* logger);
void liberar_instancia_recurso(t_recurso* recurso,sem_t* sem_recursos, t_log* logger);

int string_to_int( char* numero );
void liberar_recursos_asignados(t_list* list,t_dictionary* ,sem_t* sem_recursos, t_log* logger);

#endif /* INCLUDE_RECURSOS_H_ */
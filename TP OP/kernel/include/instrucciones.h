#ifndef INCLUDE_INSTRUCCIONES_H_
#define INCLUDE_INSTRUCCIONES_H_

#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include<commons/collections/queue.h>

#include "../../Shared/include/estructuras_compartidas.h"
#include "utils.h"

t_PCB* crear_PCB(char*, t_config*, int);

char* inicializar_registro(int);

void inicializar_registros(t_registros_pcb*);

void mover_proceso_NEW_a_READY(t_log* logger, t_list* lista_PCBs_NEW, t_list* lista_PCBs_READY, pthread_mutex_t* mutex_ready, sem_t* sem_hay_algo_listo, t_temporal* temporizador_global,int socket_memoria, t_list* lista_pcbs_creados);

void mover_proceso_EXEC_a_READY(t_list*, t_PCB*);

void bloquear_proceso_temporalmente(t_list* lista_PCBs_READY, t_PCB* proceso, pthread_mutex_t* sem_Lista_Ready, int alfa, t_temporal* temporizador_global);

void terminar_proceso(t_PCB*, t_conexion*, int soket_memoria, t_list*lista_pcbs_creados);


#endif /* INCLUDE_INSTRUCCIONES_H_ */

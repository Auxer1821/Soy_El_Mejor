#ifndef HILOS_H_
#define HILOS_H_

#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include<commons/collections/queue.h>
#include <pthread.h>
#include <semaphore.h>
#include "../../Shared/include/servidor.h"
#include "../../Shared/include/estructuras_compartidas.h"
#include "../include/instrucciones.h"
#include "utils.h"
#include "planificacion.h"
#include "../../Shared/include/logs.h"



//void funcion_Hilo1(char* puerto_escucha, t_list* lista_de_conexiones, sem_t* sem_new_process, t_log* logger);

//void funcion_Hilo2 (t_list* lista_de_socket, t_list* lista_de_conexiones, t_queue* lista_de_PCBs_NEW, sem_t* sem_new_process, t_config* config);

//void funcion_Hilo3 (t_list* lista_de_PCBs_READY, int conexion_cpu, t_list* lista_de_PCBs_ENDED, sem_t* semaforo_FinReady_De_procesos, t_log* logger /*Posible configuracion por si FIFO o HRRN*/);

//void funcion_Hilo4(t_list* lista_PCBs_READY, t_PCB* PCB);

//void funcion_Hilo5 (t_list* lista_de_PCBs_ENDED, t_list* lista_de_conexiones, sem_t* semaforo_FinReady_De_procesos);

bool buscarConexionSegunPid (int pid, t_conexion* conexion);



typedef struct{
	t_list* lista_de_socket;
    sem_t* sem_new_process;
	char* puerto_escucha;
    pthread_mutex_t* mutex_soket;
}hilo_1;

hilo_1* inicializar_Hilo1(char* puerto_escucha, t_list* lista_de_socket, sem_t* sem_new_process,  pthread_mutex_t* mutex_soket);
void* funcion_Hilo1(void*);



typedef struct{
	t_list* lista_de_socket;
    t_list* lista_de_conexiones;
    t_list* lista_de_PCBs_NEW;
    sem_t* sem_new_process;
    sem_t* semaforo_FinReady_De_procesos;
    t_config* config;
    pthread_mutex_t* mutex_Lista_NEW;
    pthread_mutex_t* mutex_Lista_conexiones;
    pthread_mutex_t* mutex_soket;
}hilo_2;

hilo_2* inicializar_Hilo2 (t_list* lista_de_socket, t_list* lista_de_conexiones, t_list* lista_de_PCBs_NEW, sem_t* sem_new_process, sem_t* semaforo_FinReady_De_procesos, t_config* config, pthread_mutex_t* mutex_Lista_NEW,pthread_mutex_t* mutex_Lista_conexiones, pthread_mutex_t* mutex_soket );
void* funcion_Hilo2 (void*);


typedef struct{
	t_log* logger_hilo4;
    pthread_mutex_t* mutex_log;
}t_hilo_4_LOGS;

t_hilo_4_LOGS crear_t_hilo_4_LOGS(void);

typedef struct{
    int conexion_cpu; 
    int conexion_filesystem;
    int conexion_memoria;
    int alfa;
    sem_t* semaforo_FinReady_De_procesos; 
    sem_t* sem_recursos;
    sem_t* semaforo_pet_fs;
    sem_t* semaforo_hay_algo_listo;
    sem_t* binario_open;
    pthread_mutex_t* mutex_Lista_READY;
    pthread_mutex_t* mutex_Lista_PCBs_ENDED;
    pthread_mutex_t* mutex_pet_FS;
	t_list* lista_de_PCBs_READY; 
    t_list* lista_de_PCBs_ENDED; 
    t_dictionary* diccionario_recurso;
    t_temporal* temporizador_global;
    t_archivos_kernel archivos;
    t_config* config;
    t_peticiones peticiones;
    t_hilo_4_LOGS hilo_4_logs;
    t_compactacion* lista_pcbs_creados;
}hilo_3;
                            
hilo_3* inicializar_Hilo3 (t_list* lista_de_PCBs_READY, int conexion_cpu, int conexion_filesystem, int conexion_memoria, t_list* lista_de_PCBs_ENDED, t_dictionary* diccionario_recurso, sem_t* sem_recursos, sem_t* semaforo_FinReady_De_procesos, pthread_mutex_t* mutex_Lista_READY, pthread_mutex_t* mutex_Lista_PCBs_ENDED, sem_t* semaforo_hay_algo_listo, t_config* config, t_temporal* temporizador_global, t_archivos_kernel archivos, sem_t* semaforo_pet_fs, t_peticiones peticiones, t_hilo_4_LOGS hilo_4_logs, sem_t* binario_open, pthread_mutex_t* mutex_pet_FS, t_compactacion* lista_psbs_creados, int alfa);
void* funcion_Hilo3 (void* );



typedef struct{
	t_list* lista_PCBs_READY;
    t_PCB* PCB;
    pthread_mutex_t* mutex_Lista_READY;
    sem_t* semaforo_hay_algo_listo;
    t_config* config;
    t_temporal* temporizador_global;
    t_hilo_4_LOGS hilo_4_logs;
}hilo_4;

hilo_4* inicializar_Hilo4(t_list* lista_PCBs_READY, t_PCB* PCB, pthread_mutex_t* mutex_Lista_READY, sem_t* semaforo_hay_algo_listo, t_config* config, t_temporal* temporizador_global, t_hilo_4_LOGS hilo_4_logs);
void* funcion_Hilo4 (void* );



typedef struct{
    t_list* lista_de_PCBs_ENDED;
    t_list* lista_de_conexiones;
    sem_t* semaforo_FinReady_De_procesos;
    t_list* lista_PCBs_READY;
    t_list* lista_de_PCBs_NEW;
    pthread_mutex_t* mutex_Lista_READY;
    pthread_mutex_t* mutex_Lista_NEW;
    pthread_mutex_t* mutex_Lista_conexiones;
    pthread_mutex_t* mutex_Lista_PCBs_ENDED;
    sem_t* semaforo_hay_algo_listo;
    t_dictionary* diccionario_recurso;
    sem_t* sem_recursos;
    t_config* config;
    t_temporal* temporizador_global;
    t_compactacion* lista_pcbs_creados;
    int socket_memoria;
}hilo_5;

hilo_5* inicializar_Hilo5 (t_list* lista_de_PCBs_ENDED, t_list* lista_de_conexiones, sem_t* semaforo_FinReady_De_procesos, t_list* lista_PCBs_READY, t_list* lista_de_PCBs_NEW, pthread_mutex_t* mutex_Lista_READY , pthread_mutex_t* mutex_Lista_NEW, pthread_mutex_t* mutex_Lista_conexiones, pthread_mutex_t* mutex_Lista_PCBs_ENDED, sem_t* semaforo_hay_algo_listo,t_dictionary* diccionario_recurso,sem_t* sem_recursos, t_config* config, t_temporal* temporizador_global, t_compactacion* lista_pcbs_creados, int socket_memoria);
void* funcion_Hilo5 (void* );

void swich_Bloqueados (t_log* logger, t_PCB* pcb, t_list* lista_de_PCBs_READY, pthread_mutex_t* mutex_Lista_READY, sem_t* semaforo_hay_algo_listo, t_config* config, t_temporal* temporizador_global, t_dictionary* diccionario_recurso, t_archivos_kernel archivos, sem_t* semaforo_pet_fs, t_hilo_4_LOGS hilo_4_logs);

#endif /* HILOS_H_ */

#ifndef INCLUDE_UTILS_H_
#define INCLUDE_UTILS_H_

#include <commons/log.h>
#include <commons/config.h>
#include "../../Shared/include/cliente.h"
#include "../../Shared/include/estructuras_compartidas.h"
#include <commons/collections/list.h>

typedef struct {
    t_list* segmentos_libre; // Array de segmentos
    int tamanio_total_disponible; // Sumatoria de los tamaños de los segmentos libres
} t_segmentos_libres;

typedef struct {
    int base;
    int tamanio;
}t_segmento_libre;

typedef struct {
    int ID;
    int capacidad;
    t_list* segmentos;
} t_tabla_de_segmentos_mem;

// Estructura para representar la memoria
typedef struct {
    void* espacio_usuario; // Espacio de usuario
    t_list* lista_tabla_de_segmentos; // Tablas de segmentos por proceso
    t_segmento* segmento_0;
    t_segmentos_libres* lista_segmentos_libres;
    int tam_memoria;
    int segmentos_creados;
    char* algoritmo;
    pthread_mutex_t* mutex;
    int cant_max_segmentos;
    int retardo_compactacion;
    int retardo_memoria;
} t_memoria;


void leer_config(t_log* ,t_config* , char** , int* , int* , int* , int* , int* , char** );


t_memoria* inicializar_memoria(int, int, int, char*, int, t_log*);

t_Pet_Segmento* deserializar_Pet_de_Segmento(t_buffer* buffer);

void terminar_programa(t_log*, t_config*);

#include "segmentos.h"

#endif

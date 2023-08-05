#ifndef INCLUDE_SEGMENTOS_H_
#define INCLUDE_SEGMENTOS_H_

#include<stdio.h>
#include<stdlib.h>

#include "utils.h"
#include "../../Shared/include/estructuras_compartidas.h"


bool ordenar_por_tamanio_SL(void* element1, void* element2);

bool ordenar_por_bases(void* element1, void* element2);

bool ordenar_por_bases_SL(void* element1, void* element2);

void inicializar_tabla_de_segmentos(int conexion_kernel, int capacidad, int PID, t_memoria* memoria);

void crear_segmento(t_log* ,int conexion_kernel, int ID, int tamanio_segmento, int PID, t_memoria* memoria, char* algoritmo);

t_segmento_libre* elegir_segmento(t_segmentos_libres* lista_segmentos_libres, char* algoritmo, int tamanio_segmento);

void compactar(t_log* logger,t_memoria* memoria, int socket_kernel);

void unir_segmentos_y_agregar(t_list* lista_de_segmentos, int pos1, int pos2);

t_Par_de_Ints eliminar_segmento(int conexion_kernel, int PID, int ID, t_memoria* memoria);

void eliminar_tabla_segmento(int PID, t_memoria* memoria);

void destruir_segmento( t_segmento* segmento_eliminado, t_memoria* memoria);

void enviar_tablas_compactadas(int socket_kernel, t_memoria* memoria);

int calcular_peso_tablas(t_list* tablas_de_segmentos, int cant_tablas);

int calcular_peso_tabla_del_proceso(t_list* tabla_segmentos);



#endif /* INCLUDE_SEGMENTOS_H_ */

#ifndef INCLUDE_PLANIFICACION_H_
#define INCLUDE_PLANIFICACION_H_

#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include<commons/collections/queue.h>
#include <stdbool.h>


#include "../../Shared/include/estructuras_compartidas.h"

void ordenar_lista_HRRN(t_list*, t_temporal* tiempo_global);
bool ordenar_mayor_prioridad(void*, void*, int64_t tiempo_global);
void parar_contadores_ready(void* pcbvoid);
void reanudar_contadores_ready(void* pcbvoid);


#endif /* INCLUDE_PLANIFICACION_H_ */
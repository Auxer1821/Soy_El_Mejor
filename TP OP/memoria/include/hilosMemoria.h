#ifndef INCLUDE_HILOSMEMORIA_
#define INCLUDE_HILOSMEMORIA_

#include "segmentos.h"
#include "../../Shared/include/logs.h"
#include "../../Shared/include/estructuras_compartidas.h"



typedef struct{
	t_memoria* memoria;
	int socket;
}t_data_hilo;


void* funcion_De_Kernel (void *element);
void* funcion_De_CPU(void * element);
void* funcion_De_FS(void * element);




#endif

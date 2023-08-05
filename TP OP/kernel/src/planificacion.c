#include "../include/planificacion.h"

//-----------------------ordenar_HRRN------------------------------------------------------------------------

void ordenar_lista_HRRN(t_list* lista_de_procesos , t_temporal* tiempo_global){

	temporal_stop(tiempo_global);
	int64_t t = temporal_gettime(tiempo_global);

	bool ordenar_procesos (void *p1, void *p2){
		return ordenar_mayor_prioridad(p1,p2,t);
	};
	
	list_sort(lista_de_procesos, ordenar_procesos);

	temporal_resume(tiempo_global);
}

//-----------------------ordenar_mayor_prioridad------------------------------------------------------------------------

bool ordenar_mayor_prioridad(void* pcbvoid1, void* pcbvoid2, int64_t tiempo_global){

	t_PCB* pcb1 = (t_PCB*) pcbvoid1;
	t_PCB* pcb2 = (t_PCB*) pcbvoid2;

	float HRRN1 = 1 + (  ((float)tiempo_global - (float)pcb1->tiempo_de_llegada_ready)  /  ( (float)(pcb1->estimacion_proxima_rafaga) )  );
	float HRRN2 = 1 + (  ((float)tiempo_global - (float)pcb2->tiempo_de_llegada_ready)  /  ( (float)(pcb2->estimacion_proxima_rafaga) )  );


	return (HRRN2 < HRRN1);
}
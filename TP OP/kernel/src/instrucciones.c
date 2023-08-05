#include "../include/instrucciones.h"

//-----------------------crear_PCB------------------------------------------------------------------------

t_PCB* crear_PCB(char* string_de_instrucciones, t_config* config, int PID){
	t_PCB* nuevo_PCB = malloc(sizeof(t_PCB));

	//le asignamos un PID unico
	//semaforo mutex()
	nuevo_PCB->pid = PID;
	//semaforo mutex()

	//asignamos las instrucciones que nos envio la consola al PCB
	nuevo_PCB->instrucciones = string_de_instrucciones;

	//incializamos el program counter en 0
	nuevo_PCB->program_counter = 0;

	//inizializamos el estado en NEW
	nuevo_PCB->estado_actual = NEW;

	//inizializamos el tiempo de bloqueo en 0 ms
	nuevo_PCB->tiempo_de_bloqueo = 0;

	//Registros de la cpu
	inicializar_registros(&nuevo_PCB->registros_cpu);

	//Tabla de segmentos
	nuevo_PCB->tabla_segmentos = list_create();

	//estimado proxima rafaga, lo incizializamos en base al valor dado en el config
	nuevo_PCB->estimacion_proxima_rafaga = config_get_int_value(config,"ESTIMACION_INICIAL");

	//inicializamos tiempo de llegada a ready en 0
	//nuevo_PCB->tiempo_de_llegada_ready = 0;

	//inicializamos tiempo de ejecucion
	nuevo_PCB->tiempo_de_ejecucion=0;
	//llegada a ready
	nuevo_PCB->tiempo_de_llegada_ready=0;

	//Tabla de archivos
	nuevo_PCB->tabla_de_archivos = dictionary_create();

	//Razon del bloqueo
	nuevo_PCB->razon_bloque = IO;

	nuevo_PCB->recurso=malloc(1);
	memset(nuevo_PCB->recurso, '\0' , 1);

	//



	return nuevo_PCB;
}

//-----------------------inicializar_registro------------------------------------------------------------------------

char* inicializar_registro(int n){
	char * reg= malloc(n);
	memset(reg, '0', n);
	return reg;
}

//-----------------------inicializar_registros------------------------------------------------------------------------

void inicializar_registros(t_registros_pcb* regs){

	regs->ax = inicializar_registro(4);
	regs->bx = inicializar_registro(4);
	regs->cx = inicializar_registro(4);
	regs->dx = inicializar_registro(4);

	regs->eax = inicializar_registro(8);
	regs->ebx = inicializar_registro(8);
	regs->ecx = inicializar_registro(8);
	regs->edx = inicializar_registro(8);

	regs->rax = inicializar_registro(16);
	regs->rbx = inicializar_registro(16);
	regs->rcx = inicializar_registro(16);
	regs->rdx = inicializar_registro(16);

}

// HECHO ASÍ NO MÁS PARA HACER LAS PRUEBAS
// Retorna el primer proceso de una lista de procesos
t_PCB* tomar_proceso(t_list* lista_PCBs){
	t_PCB* PCB = list_get(lista_PCBs,0);
	return PCB;
}

//-----------------------mover_proceso_NEW_a_READY------------------------------------------------------------------------

// Mueve un proceso de la lista de procesos NEW a READY para que este entre en la cola de ejecucion
void mover_proceso_NEW_a_READY(t_log* logger, t_list* lista_PCBs_NEW, t_list* lista_PCBs_READY, pthread_mutex_t* mutex_ready, sem_t* sem_hay_algo_listo, t_temporal* temporizador_global, int socket_memoria, t_list* lista_pcbs_creados){
	t_PCB* PCB_a_READY = list_remove (lista_PCBs_NEW, 0);

	enviar_int(socket_memoria, &PCB_a_READY->pid, NEW_PROCESS);


	list_destroy(PCB_a_READY->tabla_segmentos);
//DESDE ACA--------------Crear-segmento----------------------------

	t_paquete* paquete = malloc(sizeof(t_paquete));
	paquete->buffer = malloc(sizeof(t_buffer));

	// Primero recibimos el codigo de operacion
	recv(socket_memoria, &(paquete->codigo_de_op), sizeof(int), 0);

	// Después ya podemos recibir el buffer. Primero su tamaño seguido del contenido
	recv(socket_memoria, &(paquete->buffer->size), sizeof(int), 0);
	paquete->buffer->stream = malloc(paquete->buffer->size);
	recv(socket_memoria, paquete->buffer->stream, paquete->buffer->size, 0);




	t_list* data = deserializar_tabla_de_segmentos(paquete->buffer);


	//list_destroy_and_destroy_elements(PCB_a_READY->tabla_segmentos,free);
	PCB_a_READY->tabla_segmentos=data;

//HASTA ACA---------Crear-segmento------------------------------

//	recibir_paquete_memoria(logger, socket_memoria, 0/*soket cpu no utiliza*/, PCB_a_READY, NULL /*MUTEX PTFS*/,lista_pcbs_creados);
	list_add(lista_pcbs_creados,PCB_a_READY);

	//agregar_proceso_READY(logger, PCB_a_READY, lista_PCBs_READY, mutex_ready, sem_hay_algo_listo, temporizador_global);


	PCB_a_READY->estado_actual=READY;
	PCB_a_READY->tiempo_de_llegada_ready=temporal_gettime(temporizador_global);
	list_add(lista_PCBs_READY, PCB_a_READY);
	sem_post(sem_hay_algo_listo);




	log_info(logger, "PID: %d - Estado Anterior: NEW - Estado Actual: READY", PCB_a_READY->pid);
}

//-----------------------mover_proceso_EXEC_a_READY------------------------------------------------------------------------

/** 
*	Una vez un proceso decida dejar de ejecutar este es enviado devuelta al Kernel para asi ser encolado
*	nuevamente en la lista PCBs_READY
*/
void mover_proceso_EXEC_a_READY(t_list* lista_PCBs_READY, t_PCB* proceso){
	proceso -> estado_actual = READY;
	list_add(lista_PCBs_READY, proceso);
}

//-----------------------bloquear_proceso_temporalmente------------------------------------------------------------------------

// Una vez el proceso se bloquea en CPU este es enviado al Kernel para que este cumpla el tiempo de bloqueo
// asignado por CPU y se vuelva a encolar en la lista de PCBs_READY
void bloquear_proceso_temporalmente(t_list* lista_PCBs_READY, t_PCB* proceso, pthread_mutex_t* sem_Lista_Ready, int alfa, t_temporal* temporizador_global){

	sleep(proceso->tiempo_de_bloqueo);//espera

	int estimador_nuevo = alfa * proceso->estimacion_proxima_rafaga + (1-alfa) * proceso->tiempo_de_ejecucion;
	proceso->estimacion_proxima_rafaga=estimador_nuevo;
	proceso->tiempo_de_ejecucion=0;

	//lock_mutex(sem_Lista_Ready, logger);// BLOQUEA EL SEMAFORO
	/*
	proceso->estado_actual=READY;
	pthread_mutex_lock(sem_Lista_Ready);

	proceso->tiempo_de_llegada_ready=temporal_gettime(temporizador_global);
	list_add(lista_PCBs_READY, proceso);


	pthread_mutex_unlock(sem_Lista_Ready);
*/
	//unlock_mutex(sem_Lista_Ready, logger);// BLOQUEA EL SEMAFORO
}

//-----------------------terminar_proceso------------------------------------------------------------------------

/* Cuando se reciba un mensaje de CPU con motivo de finalizar el proceso, se deberá pasar al mismo al estado
 * EXIT,liberar todos los recursos que tenga asignados y dar aviso al módulo Memoria para que éste libere
 * sus estructuras.Una vez hecho esto, se dará aviso a la Consola de la finalización del proceso.*/
void terminar_proceso(t_PCB* proceso, t_conexion* conexion_consola, int sockt_memoria, t_list* lista_pcb_creados){
	//Liberar recursos asignados

	//Avisar a memoria de liberar estructuras
	enviar_int(sockt_memoria, &proceso->pid,TERMINATE_PROCESS);
	recibir_int(sockt_memoria);
	list_remove_element(lista_pcb_creados,proceso);

	//Avisar a la Consola correspondiente de la finalizacion de su proceso
	int mensaje_fin = 1;
	send(conexion_consola->socket, &mensaje_fin, sizeof(int), 0);

	free(conexion_consola);
	eliminar_pcb(proceso);

	//Avisar del espacio liberado en el grado multiprogramacion


}

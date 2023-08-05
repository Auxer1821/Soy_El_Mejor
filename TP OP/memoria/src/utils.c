#include "../include/utils.h"

void leer_config(t_log* logger,t_config* config, char** puerto_escucha, int* tamanio_memoria, int* tamanio_segmento_0, int* cant_segmentos, int* retardo_memoria, int* retardo_compactacion, char** algoritmo_asignacion ){
	*puerto_escucha = config_get_string_value(config, "PUERTO_ESCUCHA");
	*tamanio_memoria = config_get_int_value(config, "TAM_MEMORIA");
	*tamanio_segmento_0 = config_get_int_value(config, "TAM_SEGMENTO_0");
	*cant_segmentos = config_get_int_value(config, "CANT_SEGMENTOS");
	*retardo_memoria = config_get_int_value(config, "RETARDO_MEMORIA");
	*retardo_compactacion = config_get_int_value(config, "RETARDO_COMPACTACION");
	*algoritmo_asignacion = config_get_string_value(config, "ALGORITMO_ASIGNACION");

	log_info(logger,"Puerto escuchado: %s \n tamanio_memoria: %d \n tamanio_segmento_0: %d \n cant_segmentos: %d \n retardo_memoria: %d \n retardo_compactacion: %d \n algoritmo_asignacion: %s \n", *puerto_escucha, *tamanio_memoria, *tamanio_segmento_0, *cant_segmentos, *retardo_memoria, *retardo_compactacion, *algoritmo_asignacion);
}


t_memoria* inicializar_memoria(int retardo_compactacion, int retardo_memoria, int tamanio_memoria, char* algoritmo,  int tamanio_segmento_0, t_log* logger){
	
	t_memoria* memoria = malloc(sizeof(t_memoria));
	memoria->espacio_usuario = malloc(tamanio_memoria);
	memoria->tam_memoria = tamanio_memoria;
	memoria->retardo_compactacion=retardo_compactacion;
	memoria->retardo_memoria = retardo_memoria;

	//Inicialización de segmento 0
	t_segmento* segmento_0 = malloc(sizeof(t_segmento));
	segmento_0->base = 0;
	segmento_0->tamanio = tamanio_segmento_0;
	segmento_0->ID = 0;
	memoria->segmento_0 = segmento_0;
	memoria->segmentos_creados = 1;

	//Inicialización de lista de tablas de segmentos por proceso
	t_list* lista_tabla_de_segmentos = list_create();
	memoria->lista_tabla_de_segmentos = lista_tabla_de_segmentos;

	//Inicialización de lista de segmentos libres
	t_segmento_libre* segmento_libre = malloc(sizeof(t_segmento_libre));
	segmento_libre->base = tamanio_segmento_0;
	int tamanio_disponible = tamanio_memoria - tamanio_segmento_0;
	segmento_libre->tamanio = tamanio_disponible;
	t_segmentos_libres* lista_segmentos_libre = malloc(sizeof(t_segmentos_libres));
	lista_segmentos_libre->tamanio_total_disponible = tamanio_disponible;
	lista_segmentos_libre->segmentos_libre = list_create();
	list_add(lista_segmentos_libre->segmentos_libre, segmento_libre);
	memoria->lista_segmentos_libres = lista_segmentos_libre;



//	pthread_mutex_t Aux_mutex_memory;
	pthread_mutex_t* mutex_memory = malloc(sizeof(pthread_mutex_t));		
	if ( pthread_mutex_init(mutex_memory, NULL) != 0){
		log_error(logger,"ERROR INICIAR EL MUTEX");
		exit(3);
	}
	memoria->mutex=mutex_memory;

	
	memoria->algoritmo = algoritmo;

	return memoria;
}

t_Pet_Segmento* deserializar_Pet_de_Segmento(t_buffer* buffer){
	t_Pet_Segmento* peticion = malloc(sizeof(t_Pet_Segmento));

	void* stream = buffer->stream;

	memcpy(&(peticion->PID), stream, sizeof(int));
	stream += sizeof(int);
	memcpy(&(peticion->ID), stream, sizeof(int));
	stream += sizeof(int);
	memcpy(&(peticion->tamanio), stream, sizeof(int));
	stream += sizeof(int);

	return peticion;
}

void terminar_programa(t_log* logger, t_config* config)
{
	/* Y por ultimo, hay que liberar lo que utilizamos (conexion, log y config)
	  con las funciones de las commons y del TP mencionadas en el enunciado */

	  log_destroy (logger);
	  config_destroy (config);
}

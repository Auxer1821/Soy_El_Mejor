#include "../include/utils.h"



void leer_config(t_log* logger, t_config* config, char** puerto_escucha,  char** puerto_memoria, char** ip_memoria, int* retardo_instruccion, int* tam_max_seg){
	*puerto_escucha = config_get_string_value(config, "PUERTO_ESCUCHA");
	*puerto_memoria = config_get_string_value(config, "PUERTO_MEMORIA");
	*ip_memoria = config_get_string_value(config, "IP_MEMORIA");
	*retardo_instruccion = config_get_int_value(config,"RETARDO_INSTRUCCION");
	*tam_max_seg = config_get_int_value(config,"TAM_MAX_SEGMENTO");

	log_info(logger,"Lei los siguientes datos: \n Puerto Kernel: %s \n"
												"Puerto Memoria: %s \t IP Memoria: %s \n",
			*puerto_escucha, *puerto_memoria, *ip_memoria);
}

void terminar_programa(t_log* logger, t_config* config, int conexion_memoria)
{
	/* Y por ultimo, hay que liberar lo que utilizamos (conexion, log y config)
	  con las funciones de las commons y del TP mencionadas en el enunciado */

	  log_destroy (logger);
	  config_destroy (config);
	  liberar_conexion(conexion_memoria);
}

int MMU(int* id_segmento,int* offset, int direccion_logica, int tam_max_segmento, t_contexto* contexto, int tamanio_a_leer, t_log* logger){
	
	t_list* tabla_de_segmentos = contexto->tabla_segmentos;

	*id_segmento = direccion_logica / tam_max_segmento;
	int desplazamiento_segmento = direccion_logica % tam_max_segmento;
	*offset=desplazamiento_segmento;
    bool comparar_IDs_segmentos(void* element){
        t_segmento* segmento = (t_segmento*)element;
        return segmento->ID == *id_segmento;};

	//Buscar segmento por ID en la tabla de segmentos
	t_segmento* segmento = list_find(tabla_de_segmentos, comparar_IDs_segmentos);
	if (segmento->tamanio < desplazamiento_segmento + tamanio_a_leer){
		//Enviar a kernel contexto de ejecución
		return -1;
	}
	else
	return segmento->base + desplazamiento_segmento;
}
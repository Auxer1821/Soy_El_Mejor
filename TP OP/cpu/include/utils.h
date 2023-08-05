#ifndef INCLUDE_UTILS_H_
#define INCLUDE_UTILS_H_

#include <commons/log.h>
#include <commons/config.h>
#include "../../Shared/include/cliente.h"
#include "../../Shared/include/estructuras_compartidas.h"


#include "../include/utils.h"

void leer_config(t_log* logger, t_config* config, char** puerto_escucha,  char** puerto_memoria, char** ip_memoria, int* retardo_instruccion, int* tam_max_seg);

void terminar_programa(t_log* logger, t_config* config, int conexion_memoria);

int MMU(int* id, int* offset, int direccion_logica, int tam_max_segmento, t_contexto* contexto, int tamanio_a_leer, t_log* logger);

int enviar_proceso_SEG_FAULT(int conexion, t_PCB* proceso);

void* serializar_proceso_SEG_FAULT(t_PCB* proceso, t_paquete* paquete);


#endif

#ifndef INCLUDE_UTILS_H_
#define INCLUDE_UTILS_H_

#include <commons/log.h>
#include <commons/config.h>
#include "../../Shared/include/cliente.h"
#include "../../Shared/include/estructuras_compartidas.h"

int enviar_instrucciones(char*, int);

void eliminar_paquete(t_paquete*);

void* serializar_instrucciones(char*, t_paquete*);

void esperar_respuesta(int, t_log* logger);

char* cargar_instrucciones(char*, t_log* logger);

void terminar_programa(int, t_log*, t_config*);

char** recibir_instrucciones_prueba();

#endif

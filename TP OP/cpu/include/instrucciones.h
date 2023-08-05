#ifndef INCLUDE_INSTRUCCIONES_H_
#define INCLUDE_INSTRUCCIONES_H_

#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#include <commons/log.h>
#include <commons/config.h>
#include "../../Shared/include/estructuras_compartidas.h"
#include <commons/collections/dictionary.h>
#include "utils.h"
//Preparación, antes de comenzar el programa

t_dictionary* inicializarRegistros (void); // Inicializa los registros de arriba


//Funciones analisis de datos y analisis de paquetes

//actualizar el actualizar_registro post senializacion
void actualizar_registro(t_dictionary*, t_registros_pcb); // Actualiza los registros internos del CPU segun el contexto del paquete

void realizar_instruccion (t_contexto* contexto, t_dictionary* registros, char** instruccion, int conexion_kernel, int conexion_memoria, t_log* logger, int retardo_de_instruccion, int tam_max_seg); // Chequea que instrucción es la que se pide e inicia la misma

//t_registro sacar_registro(Registros_CPU, char*); // Obtiene el registro desde el string que dice cual utilizar


//Funciones Auxiliares
int string_to_int (char*);
void resetCero(t_registro*);

//Funciones de procesamiento de CPU (1) [Funcionales]

void set (t_registro*, char*, int);
void yield(t_contexto*);
void exit_instruccion(t_contexto*, t_razon_finalizado);
void io (t_contexto* contexto, int unTiempo);
void wait_instruccion (t_contexto* contexto, char* nombre_recurso, int conexion_kernel, t_log* logger);
void signal_instruccion (t_contexto* contexto, char* nombre_recurso, int conexion_kernel, t_log* logger);




void mov_in (t_registro* registro, int direccion_logica, int tam_max_segmento, t_contexto* contexto, int conexion_memoria, t_log* logger);
void mov_out (t_registro* registro, int direccion_logica, int tam_max_segmento, t_contexto* contexto, int conexion_memoria, t_log* logger); 
void f_open ( char* nombre_archivo, int conexion_kernel, t_contexto* contexto, t_dictionary* registros, t_log* logger);
void f_close ( char* nombre_archivo, int conexion_kernel, t_contexto* contexto, t_dictionary* registros, t_log* logger);
void f_seek ( char* nombre_archivo, char* posicion, int conexion_kernel, t_contexto* contexto, t_dictionary* registros, t_log* logger);//verificar el char* [1] cuando entendamos memoria y sepamos de donde sale direccion logica
void f_read (char* nombre_archivo, char* direccion_logica, char* bytes, int conexion_kernel, t_contexto* contexto, t_log* logger, int seg_max);
void f_write (char* nombre_archivo, char* direccion_logica, char* bytes, int conexion_kernel, t_contexto* contexto, t_log* logger, int seg_max);
void f_truncate (char* nombre_archivo, char* tamanio, int conexion_kernel, t_contexto* contexto, t_dictionary* registros);
void create_segment (char* ID_segmento, char* tamanio, int conexion, t_contexto* contexto);
void delete_segment (char* ID_segmento, int conexion, t_contexto* contexto);


//Funciones de fin de programa

void liberarRegistros(t_dictionary*);
void liberarRegistro(char* key, void* element);

//enviar
void actualizar_contexto (t_contexto* contexto, t_dictionary* registros_propios);

#endif

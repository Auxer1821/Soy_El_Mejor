#ifndef INCLUDE_ESTRUCTURAS_COMPARTIDAS_H_
#define INCLUDE_ESTRUCTURAS_COMPARTIDAS_H_

#include <commons/collections/list.h>
#include <commons/config.h>
#include "../../Shared/include/cliente.h"
#include "../../Shared/include/logs.h"
#include <stdbool.h>
#include <stdarg.h>
#include <commons/string.h>
#include <commons/temporal.h>
#include <pthread.h>
#include <semaphore.h>

//-----------------------------ESTRUCTURAS DE MEMORIA----------------------------------------------

typedef struct {
    int base; 
    int tamanio;
	int ID;
} t_segmento;

//-------------------------------------------------------------------------------------------------

typedef struct{
	int longitud;
	char* valor;
}t_registro;

typedef struct{
	char *ax;
	char *bx;
	char *cx;
	char *dx;
	char *eax;
	char *ebx;
	char *ecx;
	char *edx;
	char *rax;
	char *rbx;
	char *rcx;
	char *rdx;
}t_registros_pcb;

typedef struct{
	int id;
	int tamanio_segmento;
	char* direcciones_base;
}t_tabla_segmentos;

typedef struct{
	char* nombre_archivo;
	int puntero_actual;
	int tamanio;
}t_archivo_pcb;
 
typedef struct{
	char* archivo;
	char* direccion_de_memoria;
}t_tabla_de_archivos;

typedef enum{
	NEW,
	READY,
	EXEC,
	BLOCK,
	ENDED
}t_estado;

typedef struct{
	char* nombre;
	bool falta;
}t_recurso_bloqueado;

typedef enum {
    RECURSO,
	ARCHIVO,
	IO,
	PET_FS,
} t_razon_bloq;

typedef struct{
	int pid;
	char* instrucciones;
	int program_counter;
	t_estado estado_actual;
	t_razon_bloq razon_bloque;
	int tiempo_de_bloqueo; //ms
	t_registros_pcb registros_cpu;
	t_list* tabla_segmentos;  //Cambiamos el tipo de dato, fijarse en serializacion y deserializacion
	int estimacion_proxima_rafaga;
	int64_t tiempo_de_llegada_ready;
	int64_t tiempo_de_ejecucion;
	t_dictionary* tabla_de_archivos; //t_tabla_de_archivos
	char* recurso;
	int razon_finalizado;
} t_PCB;

typedef struct 
{
	int pid;
	int program_counter;
	int razon_finalizado;
	t_estado estado_actual;
	int tiempo_de_bloqueo;
	t_registros_pcb registros_cpu;
	t_list* tabla_segmentos;
	char* instrucciones;
}t_contexto;


typedef struct {
    int size;
    void* stream;
} t_buffer;

typedef struct{
	int data1;
	int data2;
} t_Par_de_Ints;

typedef struct{
	int PID;
	int ID;
	int tamanio;
}t_Pet_Segmento;

typedef struct{
	int codigo_de_op;
	t_buffer* buffer;
}t_paquete;

typedef struct{
	int longitud;
	char* valor;
	int df;
}t_dato_DF;

typedef enum {
    CONSOLA,
	PCB,
	CONTEXTO,
	PREG_REC,
	SIGNAL_REC,
	RESP_REC,
	F_OPEN,
	F_CLOSE,
	F_SEEK,
	F_READ,
	F_WRITE,
	F_TRUNCATE,
	CREATE_SEGMENT,
	DELETE_SEGMENT,
	NEW_SEGMENT,
	RTA_NEW_SEGMENT,
	NEW_PROCESS,
	TERMINATE_PROCESS,
	PET_COMPACTAR,
	OUT_OF_MEMORY,
	REGISTRO,
	REGISTRO_CON_DF,
	MOV_IN,
	MOV_OUT,
	SEG_FAULT,
	PET_CRE_SEG,
	TS,
	// ...
} codigo_de_op;

typedef enum {
    FIN_SUCCESS,
	FIN_SEG_FAULT,
	FIN_INVALID_RESOURCE,
	FIN_OUT_OF_MEMORY,
	ERROR,
	// ...
} t_razon_finalizado;

//----------------SERIALIZACIÓN-----------------------------------------------------------------------------------------------//


int enviar_bool(int conexion, bool booleano);

bool recibir_bool(int conexion);

int enviar_int_tradicional(int conexion, int entero);

void liberar_contexto(t_contexto* contexto);

int recibir_int(int conexion);

char* recibir_string_tradicional(int socket_memoria, int tam);

int enviar_string_tradicional(int conexion, char* string);

int enviar_string(int conexion, char* string, codigo_de_op codigo_op);

void* serializar_string(char*, t_paquete*, codigo_de_op);

int enviar_contexto(int conexion, t_contexto* contexto);

void eliminar_paquete(t_paquete*);

void* serializar_contexto(t_contexto*, t_paquete*);

int obtener_tamanio_instrucciones(char**);

void* recibir_paquete(int);

t_contexto* deserializar_contexto(t_buffer*);

char* deserializar_string(t_buffer*);

int transformar_enum(t_estado);

t_estado detransformar_enum(int);

int enviar_int(int conexion, int* tamanio, codigo_de_op codigo_de_op);

void* serializar_int(int* tamanio, t_paquete* paquete, codigo_de_op codigo_de_op);

int* deserializar_int(t_buffer* buffer);

int enviar_tabla_de_segmentos(int conexion, t_list* tabla);

void* serializar_tabla_de_segmentos(t_list* tabla, t_paquete* paquete);

t_list* deserializar_tabla_de_segmentos(t_buffer* buffer);

int enviar_Par_de_Ints(int conexion, int data1, int data2, codigo_de_op codigo_de_op);

void* serializar_Par_de_Ints(t_Par_de_Ints*, t_paquete*, codigo_de_op codigo_de_op);

t_Par_de_Ints* deserializar_Par_de_Ints(t_buffer*);

int enviar_registro(int conexion, t_registro* registro);

int enviar_dato_con_DF(int conexion, int longitud, char* valor, int* DF, codigo_de_op codigo_de_op);

void* serializar_dato_con_DF(int longitud, char* valor, int* DF, t_paquete* paquete, codigo_de_op codigo_de_op);

t_dato_DF* deserializar_dato_DF(t_buffer* buffer);

int enviar_Pet_Segmento(int conexion, int PID, int ID, int tamanio);

void* serializar_pet_creacion_segmento(t_Pet_Segmento* peticion, t_paquete* paquete);

t_Pet_Segmento* deserializar_Pet_de_Segmento(t_buffer* buffer);

void* serializar_registro(t_registro* registro, t_paquete* paquete);

typedef struct{
	int socket;
	int PID;
}t_conexion;



void eliminar_pcb(t_PCB* pcb);
void liberar_lista_strings(t_list* lista);
int calcular_tam_lista_regs(t_list* lista);

//----------------EXTRAS-----------------------------------------------------------------------------------------------//

void sleep_ms(int);

pthread_mutex_t* inicializar_mutex (t_log* logger);

void lock_mutex(pthread_mutex_t* mutex, t_log* logger);

void unlock_mutex(pthread_mutex_t* mutex, t_log* logger);

#endif /* INCLUDE_ESTRUCTURAS_COMPARTIDAS_H_ */

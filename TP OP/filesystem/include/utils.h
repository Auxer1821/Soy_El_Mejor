#ifndef INCLUDE_UTILS_H_
#define INCLUDE_UTILS_H_

#include <commons/log.h>
#include <commons/config.h>
#include <commons/string.h>
#include <commons/bitarray.h>
#include <sys/mman.h>
#include <sys/stat.h>        /* For mode constants */
#include <fcntl.h>           /* For O_* constants */
#include "../../Shared/include/config.h"
#include "../../Shared/include/cliente.h"
#include "../../Shared/include/estructuras_compartidas.h"
#include <dirent.h>

//----------------ESTRUCTURAS-----------------------------------------------------------------------------------------------//

typedef struct{
	char* nombre_archivo;
	int tamanio_archivo;
    uint32_t direct_pointer;
    uint32_t indirect_pointer;
}t_FCB;

typedef struct{
	int cantBloques;
    int tamBloques;
    char* archivo_bloque;
    t_bitarray* BitMapBloque;
}t_info_bloques;

typedef struct{
    char* ip_memoria;
    char* puerto_memoria;
    char* puerto_escucha;
    char* path_super_bloque;
    char* path_bloques;
    char* path_bitmap;
    char* path_fcb;
    int retardo_acceso_bloque;
}t_config_fileSystem;


//----------------GENERALES-----------------------------------------------------------------------------------------------//


void leer_config(t_log* logger, t_config* config, t_config_fileSystem* config_filesystem);

void terminar_programa(t_log* logger, t_config* config, int conexion_memoria);

//----------------SUPERBLOQUE-----------------------------------------------------------------------------------------------//

void leer_superBloque(t_log* logger, char* path_superBloque, int* tambBloque, int* cantBloque);

//----------------ARCHIVOS DE BLOQUES-----------------------------------------------------------------------------------------------//

char* iniciar_archivo_bloques(t_log* logger, char* path_bloque, int cantBloque, int tamBloque);

//----------------BIT MAP-----------------------------------------------------------------------------------------------//

t_bitarray* InicializarBitMap (t_log* logger, char* path_BitMap, int cantBlock);

int enviar_lectura_archivo(int socket_memoria, t_dato_DF* dato_a_enviar);

void* serializar_lectura_archivo(t_dato_DF* dato_a_enviar, t_paquete* paquete);

#endif

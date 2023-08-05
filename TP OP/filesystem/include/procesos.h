#ifndef INCLUDE_PROCESOS_H_
#define INCLUDE_PROCESOS_H_

#include "utils.h"
#include "../../Shared/include/estructuras_compartidas.h"


//-----------------------FBCs------------------------------------------------------------------------
t_dictionary* abrir_FCBs(t_log* logger, char* path_FCBs, t_bitarray* bitMap);
t_FCB* leer_FCB(char* path_FCB);
t_FCB* crer_FCB (t_log* logger, char* path_FCB , char* nombre , t_bitarray* bitMapBloque);

//-----------------------BLOQUES------------------------------------------------------------------------
void leer_bloques (t_log* logger, char* bits_a_enviar, int necesito, int posicion_actual, t_FCB* fcb, t_info_bloques info_bloques, t_config_fileSystem config_fs);
char* get_bloque(t_info_bloques info_bloques , int pos, t_config_fileSystem config_fs);
uint32_t pedir_bloque(t_log* logger, t_bitarray* bitMapBloque);
void liberar_bloque (t_log* logger, t_bitarray* bitMapBloque,uint32_t pos);
char* buscarBloqueEnArchivo(t_log* logger, char* archivo_de_bloque, t_FCB* fcb, uint32_t bloqueActualDelFCB, t_info_bloques info_bloques, t_config_fileSystem config_fs);

//-----------------------FUNCIONES------------------------------------------------------------------------
void open_file(t_log* logger, char* path_FCBs, char* nombre, t_dictionary* FCBs_Dic, t_bitarray* bitMapBloque);
void truncate_file(t_log* logger, char* instruccion, char* path_FCBs, t_dictionary* FCBs_Dic, t_info_bloques info_bloques, t_config_fileSystem config_fs);
void escribir_archivo(t_log* logger, int socket_memoria, char* instruccion, t_config_fileSystem config, t_info_bloques info_bloques, t_dictionary* FCBs_Dic, t_config_fileSystem config_fs);

//-----------------------OTROS------------------------------------------------------------------------
void recibir_paquete_kernel(int socket_kernel, int socket_memoria, t_log* logger, t_config_fileSystem config_files, t_info_bloques info_bloques, t_dictionary* FCBs_Dic);
void realizar_instruccion(int socket_kernel, int socket_memoria, int instruccion, char* datos, t_log* logger,t_config_fileSystem config_files, t_info_bloques info_bloques, t_dictionary* FCBs_Dic);
void leer_archivo(t_log* logger, int socket_memoria, char* instruccion, t_config_fileSystem config, t_info_bloques info_bloques, t_dictionary* FCBs_Dic, t_config_fileSystem config_fs); //logger, datos, config_files, info_bloques, diccionario_FCBs

#endif
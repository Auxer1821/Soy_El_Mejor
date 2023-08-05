#include "../include/utils.h"

//----------------GENERALES-----------------------------------------------------------------------------------------------//
void leer_config(t_log* logger, t_config* config, t_config_fileSystem* config_filesystem){
	config_filesystem->puerto_escucha = config_get_string_value(config, "PUERTO_ESCUCHA");
	config_filesystem->puerto_memoria = config_get_string_value(config, "PUERTO_MEMORIA");
	config_filesystem->ip_memoria = config_get_string_value(config, "IP_MEMORIA");
	config_filesystem->path_super_bloque = config_get_string_value(config, "PATH_SUPERBLOQUE");
	config_filesystem->path_bitmap = config_get_string_value(config, "PATH_BITMAP");
	config_filesystem->path_bloques = config_get_string_value(config, "PATH_BLOQUES");
	config_filesystem->path_fcb = config_get_string_value(config, "PATH_FCB");
	config_filesystem->retardo_acceso_bloque = config_get_int_value(config, "RETARDO_ACCESO_BLOQUE");

	log_info(logger,"Lei los siguientes datos: \n Puerto Kernel: %s \n"
												"Puerto Memoria: %s \t IP Memoria: %s \n"
												"Y con un retardo de acceso bloque bloque de: %d \n",
			config_filesystem->puerto_escucha, config_filesystem->puerto_memoria, config_filesystem->ip_memoria, config_filesystem->retardo_acceso_bloque);

}

void terminar_programa(t_log* logger, t_config* config, int conexion_memoria){
	/* Y por ultimo, hay que liberar lo que utilizamos (conexion, log y config)
	  con las funciones de las commons y del TP mencionadas en el enunciado */

	  log_destroy (logger);
	  config_destroy (config);
	  liberar_conexion(conexion_memoria);
}

//----------------SUPERBLOQUE-----------------------------------------------------------------------------------------------//

void leer_superBloque(t_log* logger, char* path_superBloque, int* tambBloque, int* cantBloque){

	log_debug(logger, "Leyendo config del SUPERBLOQUE");

	t_config* superBloque = iniciar_config(path_superBloque);

	*tambBloque = config_get_int_value(superBloque, "BLOCK_SIZE");
	*cantBloque = config_get_int_value(superBloque, "BLOCK_COUNT");

	config_destroy(superBloque);

	log_debug(logger, "Lectura de la config del SUPERBLOQUE completada");

}

//-------------BLOQUES------------------------------------------------------------------------------------------------//

char* iniciar_archivo_bloques(t_log* logger, char* path_bloque, int cantBloque, int tamBloque){

	int fd;
	bool fileExiste = (access(path_bloque, F_OK) != -1);


	//---------------------------------------Si no existe el archivo, lo limpia y lo deja en 0
	if (!fileExiste){
		FILE* f = fopen(path_bloque,"w+b");

		char* buffer=malloc(tamBloque);
		for(int i =0 ; i<cantBloque; i++ ){
			fwrite(&buffer, tamBloque , 1, f);
		}
		free(buffer);
		
		fclose(f);

	}
	//------------------------------------------------------------


    fd = open(path_bloque, O_RDWR, S_IRUSR | S_IWUSR);
    if (fd == -1){
        log_error(logger,"error con shared memory");
        exit(777);
    }

	char* archivo_bloque = mmap(NULL, tamBloque , PROT_WRITE|PROT_READ, MAP_SHARED, fd, 0);

	
	return archivo_bloque;
}

//----------------BITMAP-----------------------------------------------------------------------------------------------//

t_bitarray* InicializarBitMap (t_log* logger, char* path_BitMap, int cantBlock){

	int fd;
	bool fileExiste = (access(path_BitMap, F_OK) != -1);

	int cant_Byte = cantBlock/8 + ((cantBlock%8 == 0)? 0:1);

	//------------------------------------Si no existe el archivo, lo limpia y lo deja en 0
	if (!fileExiste){
		FILE* f = fopen(path_BitMap,"w+b");

		char buffer = 0;
		for(int i =0 ; i<cant_Byte; i++ ){
			fwrite(&buffer, sizeof(char), 1, f);
		}
		
		fclose(f);

	}
	//------------------------------------------------------------


    fd = open(path_BitMap, O_RDWR, S_IRUSR | S_IWUSR); //parametros: (path, READ & WRITE, Permiso Leer | Permiso Escribir)
    if (fd == -1){
        log_error(logger,"error con shared memory");
        exit(777);
    }

	void* bitmap_mem = mmap(NULL, sizeof(char), PROT_WRITE|PROT_READ, MAP_SHARED, fd, 0); //RAM (mmap)

	t_bitarray* BitMap = bitarray_create (bitmap_mem, cant_Byte); //Modifica (mmap) RAM
	
	return BitMap;

}

int enviar_lectura_archivo(int socket_memoria, t_dato_DF* dato_a_enviar){
    t_paquete* paquete = malloc(sizeof(t_paquete));
	void* a_enviar = serializar_lectura_archivo(dato_a_enviar, paquete);
	int ret = send(socket_memoria, a_enviar, (paquete->buffer->size) + sizeof(int) + sizeof(int), 0);
	free(a_enviar);
	eliminar_paquete(paquete);
	return ret;
}

void* serializar_lectura_archivo(t_dato_DF* dato_a_enviar, t_paquete* paquete){
	t_buffer* buffer = malloc(sizeof(t_buffer));

	// Primero completo la estructura buffer interna del paquete.
	buffer->size = dato_a_enviar->longitud + sizeof(int)*2;
	void* stream = malloc(buffer->size);
	int offset = 0; // Desplazamiento

	memcpy(stream + offset, &(dato_a_enviar->df), sizeof(int));
	offset += sizeof(int);
	memcpy(stream + offset, &(dato_a_enviar->longitud), sizeof(int));
	offset += sizeof(int);
	memcpy(stream + offset, dato_a_enviar->valor, dato_a_enviar->longitud);
	offset += dato_a_enviar->longitud;

	buffer->stream = stream;

	// Segundo: completo el paquete.
	paquete->codigo_de_op = F_READ;
	paquete->buffer = buffer;

	void* a_enviar = malloc(buffer->size + sizeof(int) + sizeof(int));
	offset = 0;

	memcpy(a_enviar, &(paquete->codigo_de_op), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, &(paquete->buffer->size), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, paquete->buffer->stream, paquete->buffer->size);
	offset += paquete->buffer->size;

	free(dato_a_enviar);
	return a_enviar;
}
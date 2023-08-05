#include "../include/utils.h"


int enviar_instrucciones(char* lista_de_instrucciones,int conexion_kernel){
	t_paquete* paquete = malloc(sizeof(t_paquete));
	void* instrucciones_a_enviar = serializar_instrucciones(lista_de_instrucciones, paquete);
	int ret = send(conexion_kernel, instrucciones_a_enviar, (paquete->buffer->size) + sizeof(int) + sizeof(int), 0);
	free(instrucciones_a_enviar);
	eliminar_paquete(paquete);
	return ret;
}

void* serializar_instrucciones(char* lista_de_instrucciones, t_paquete* paquete){
	t_buffer* buffer = malloc(sizeof(t_buffer));
	int tamanio_instrucciones = strlen(lista_de_instrucciones)+ 1; // La longitud del string nombre. Le sumamos 1 para enviar tambien el caracter centinela '\0'.

	// Primero completo la estructura buffer interna del paquete.
	buffer->size = tamanio_instrucciones;
	void* stream = malloc(buffer->size);
	int offset = 0; // Desplazamiento

	memcpy(stream + offset, lista_de_instrucciones, tamanio_instrucciones);
	offset += tamanio_instrucciones;

	buffer->stream = stream;

	// Segundo: completo el paquete.
	paquete->codigo_de_op = CONSOLA;
	paquete->buffer = buffer;

	void* a_enviar = malloc(buffer->size + sizeof(int) + sizeof(int));
	offset = 0;

	memcpy(a_enviar, &(paquete->codigo_de_op), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, &(paquete->buffer->size), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, paquete->buffer->stream, paquete->buffer->size);
	offset += paquete->buffer->size;

	free(lista_de_instrucciones);
	return a_enviar;
}

void esperar_respuesta(int conexion_kernel, t_log* logger){
	log_info(logger, "Esperando mensaje de finalizacion del kernel");
	int mensaje_fin;
	recv(conexion_kernel, &mensaje_fin, sizeof(int), 0);
	if(mensaje_fin == 1){
		log_info(logger, "El proceso ha finalizado");
	}
}

char* cargar_instrucciones(char* ruta_pseudocodigo, t_log* logger){
	//abrimos el archivo de pseudocodigo
	FILE* archivo_pseudocodigo;
			if (!(archivo_pseudocodigo = fopen(ruta_pseudocodigo,"r"))){
				log_error(logger,"No se puedo abrir el archivo de pseudocodigo");
				exit(2);
			}

	//leemos linea por linea del archivo

	char* retorno = string_new();
	size_t len = 0;

	while (!feof(archivo_pseudocodigo))
	{
		char* buffer = NULL;
		//leemos una linea del archivo de pseudocodigo
		getline(&buffer,&len,archivo_pseudocodigo);
		strtok(buffer,"\n");

		//normalizamos el string para luego separarlo en el kernel
		string_append_with_format(&retorno,"%s|",buffer);

		//liberamos el buffer para su proxima utilizacion
		free(buffer);
	}

	//cerramos el archivo de pseudocodigo
	fclose(archivo_pseudocodigo);
	//retornamos el string ya normalizado y unificado
	return retorno;
}

void terminar_programa(int conexion, t_log* logger, t_config* config)
{
	//Liberamos lo que utilizamos (conexion, log y config)

	  log_destroy (logger);
	  config_destroy (config);
	  liberar_conexion(conexion);
}


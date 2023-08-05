#include "../include/estructuras_compartidas.h"
/*
void* serializar_mov_in(int direccion, t_paquete* paquete, codigo_de_op codigo_op){ //Este le enviará a memoria la dirección a acceder + tipo de operacion a operar
    t_buffer* buffer = malloc(sizeof(t_buffer));
	
	// Primero completo la estructura buffer interna del paquete.
	buffer->size = sizeof(codigo_de_op) + sizeof (int);
	void* stream = malloc(buffer->size);
	int offset = 0; // Desplazamiento

	memcpy(stream + offset, codigo_op, sizeof(codigo_de_op));
	offset += sizeof(codigo_de_op);

	memcpy(stream + offset, direccion, sizeof(int));
	offset += sizeof(int);

	buffer->stream = stream;

	// Segundo: completo el paquete.
	paquete->codigo_de_op = codigo_op;
	paquete->buffer = buffer;

	void* a_enviar = malloc(buffer->size + sizeof(int) + sizeof(int));
	offset = 0;

	memcpy(a_enviar, &(paquete->codigo_de_op), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, &(paquete->buffer->size), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, paquete->buffer->stream, paquete->buffer->size);
	offset += paquete->buffer->size;

	return a_enviar;
}








int enviar_mov_in(int conexion, int direccion, codigo_de_op codigo_op){
    t_paquete* paquete = malloc(sizeof(t_paquete));
	void* int_a_enviar = serializar_int(direccion, paquete, codigo_op);
	int ret = send(conexion, int_a_enviar, (paquete->buffer->size) + sizeof(int) + sizeof(int), 0);
	free(int_a_enviar);
	eliminar_paquete(paquete);
	return ret;
}








int deserializar_mov_in(t_buffer* buffer){
    int* direccion = malloc;
    codigo_de_op codigo = MOV_IN;

	void* stream = buffer->stream;

	int enum_como_int, tam_archivo, tam_direcc_mem;

	int ret;
    
    void* stream = buffer->stream;
	// Deserializamos los campos que tenemos en el buffer
	memcpy(ret, stream, buffer->size);

	return ret;
}

	t_PCB* proceso = malloc(sizeof(t_PCB));

	void* stream = buffer->stream;

	int enum_como_int, tam_archivo, tam_direcc_mem;















//---------------------------VER SI HAY QUE TOCAR ALGO------------------------------//

void* recibir_paquete(int socket){
	t_paquete* paquete = malloc(sizeof(t_paquete));
	paquete->buffer = malloc(sizeof(t_buffer));

	// Primero recibimos el codigo de operacion
	recv(socket, &(paquete->codigo_de_op), sizeof(int), 0);

	// Después ya podemos recibir el buffer. Primero su tamaño seguido del contenido
	recv(socket, &(paquete->buffer->size), sizeof(int), 0);
	paquete->buffer->stream = malloc(paquete->buffer->size);
	recv(socket, paquete->buffer->stream, paquete->buffer->size, 0);


	void* data;

	switch (paquete->codigo_de_op)
	{
	case CONSOLA:
		data = deserializar_string(paquete -> buffer);
		break;
	case PCB:
		data = deserializar_proceso(paquete -> buffer);
		break;
    case MOV_IN:
        data = deserializar_mov_in(paquete->buffer);
        break;
	case RESP_REC:
		break;
	default:
		break;
	}


	//free(instrucciones_como_string);
	free(paquete->buffer->stream);
	free(paquete->buffer);
	free(paquete);
	return data;
	}

char* deserializar_string(t_buffer* buffer){
	char* string = malloc(buffer->size);
	memset(string, 0, buffer->size);

	void* stream = buffer->stream;
	// Deserializamos los campos que tenemos en el buffer
	memcpy(string, stream, buffer->size);

	return string;
}*/
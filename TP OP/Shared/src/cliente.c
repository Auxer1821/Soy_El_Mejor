#include "../include/cliente.h"

#include <stdlib.h>

//Conexiones

int crear_conexion(char *ip, char* puerto)
{
	struct addrinfo hints;
	struct addrinfo *server_info;

	memset(&hints, 0, sizeof(hints));
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_flags = AI_PASSIVE;

	getaddrinfo(ip, puerto, &hints, &server_info);
	// Ahora vamos a crear el socket.
	int socket_cliente = socket(server_info->ai_family, server_info->ai_socktype, server_info->ai_protocol);
	// Ahora que tenemos el socket, vamos a conectarlo
	connect(socket_cliente, server_info->ai_addr, server_info->ai_addrlen);
	freeaddrinfo(server_info);
	return socket_cliente;
}

void handshake_cliente (t_log* logger, int socket, int* result, int* handshake){
	send(socket, handshake, sizeof(int), 0);
	log_debug(logger, "Enviando Handshake");

	recv(socket, result, sizeof(int), MSG_WAITALL);
	log_debug(logger, "Handshake recibido");
}

void liberar_conexion(int socket_cliente)
{
	close(socket_cliente);
}

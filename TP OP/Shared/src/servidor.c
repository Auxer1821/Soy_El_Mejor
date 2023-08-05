#include "../include/servidor.h"
#include <stdlib.h>

// Utils SERVIDOR


int iniciar_servidor(t_log* logger, char* puerto_escucha)
{
    int socket_servidor;
    int opt = 1;

    struct addrinfo hints, *servinfo, *p;

    memset(&hints, 0, sizeof(hints));
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;


    // Creamos el socket de escucha del servidor
    getaddrinfo(NULL , puerto_escucha, &hints, &servinfo);
    socket_servidor = socket(servinfo->ai_family,
                servinfo->ai_socktype,
                servinfo->ai_protocol);


    if (setsockopt(socket_servidor, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT, &opt, sizeof(opt))) 
    {
        log_error(logger,"NO SE PUDO LIBERAR EL PUERTO >:(");
        exit(EXIT_FAILURE);
    }

    // Asociamos el socket a un puerto
    bind(socket_servidor, servinfo->ai_addr, servinfo->ai_addrlen);


    // Escuchamos las conexiones entrantes
    listen(socket_servidor, SOMAXCONN);

    freeaddrinfo(servinfo);
    log_trace(logger, "Listo para escuchar a mi cliente");

    return socket_servidor;
}

int esperar_cliente(int socket_servidor,t_log* logger){
    // Aceptamos un nuevo cliente
    int socket_cliente = accept(socket_servidor, NULL, NULL);
    log_info(logger, "Se conecto un cliente!");

    return socket_cliente;
}

void handshake_servidor(t_log* logger, int socket, int* handshake_recv, int* resultOk, int* resultError, int handshake_cmp){
	recv(socket, handshake_recv, sizeof(int), MSG_WAITALL);
		if(*handshake_recv == handshake_cmp){
		   send(socket, resultOk, sizeof(int), 0);
		   log_debug (logger, "Handshake OK, Numero: %d", handshake_cmp);
		}
		else {
		   send(socket, resultError, sizeof(int), 0);
		   log_error (logger, "Handshake ERROR. ESPERABAMOS: %d, RECIBIMOS: %d", handshake_cmp, *handshake_recv);
		}

}

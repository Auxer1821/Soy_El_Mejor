#ifndef INCLUDE_CLIENTE_H_
#define INCLUDE_CLIENTE_H_

#include<stdio.h>
#include<stdlib.h>
#include<signal.h>
#include<unistd.h>
#include<sys/socket.h>
#include<netdb.h>
#include<string.h>
#include<commons/log.h>

int crear_conexion(char*, char*);
void liberar_conexion(int);

void handshake_cliente (t_log* logger, int socket, int* result, int* handshake);

#endif /* INCLUDE_CLIENTE_H_ */

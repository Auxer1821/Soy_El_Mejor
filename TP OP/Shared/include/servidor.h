#ifndef INCLUDE_SERVIDOR_H_
#define INCLUDE_SERVIDOR_H_

#include<stdio.h>
#include<stdlib.h>
#include<signal.h>
#include<unistd.h>
#include<sys/socket.h>
#include<netdb.h>
#include <netinet/in.h>
#include<string.h>
#include<commons/log.h>


int iniciar_servidor(t_log*, char*);

int esperar_cliente(int, t_log*);

void handshake_servidor(t_log*, int socket, int* handshake_recv, int* resultOk, int* resultError, int handshake_cmp);

#endif /* INCLUDE_SERVIDOR_H_ */

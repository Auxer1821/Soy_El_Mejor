#include "../include/logs.h"
#include <stdlib.h>


t_log* iniciar_logger(char* ruta, char* process_name)
{
	t_log* nuevo_logger;

	nuevo_logger = log_create (ruta, process_name, true, LOG_LEVEL_INFO);

	if (nuevo_logger == NULL){
		printf("No pude crear el logger\n");
		//exit(1);
	}
	return nuevo_logger;
}

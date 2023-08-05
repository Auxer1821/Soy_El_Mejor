#include "../include/config.h"
#include "../include/logs.h"
#include <stdlib.h>


t_config* iniciar_config(char* ruta)
{
	t_config* nuevo_config;
	nuevo_config = config_create(ruta);
	if (nuevo_config == NULL){
		printf ("No pude leer la config\n");
		//exit(2);
	}
	return nuevo_config;
}

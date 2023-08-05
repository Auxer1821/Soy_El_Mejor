#include "include/consola.h"

int main (int argc, char** argv){

	char* ip;
	char* puerto;
	char* ruta_configuracion ;
	char* ruta_pseudocodigo ;
	int handshake = 1;

	//Cargamos los valores pasados por parametro
	//strcpy(ruta_configuracion,argv[2]);
	//strcpy(ruta_pseudocodigo,argv[3]);

	//Inicializamos el log
	t_log* logger = iniciar_logger("./logsConsola.log","Log-consola");

	//Creamos el archivo de configuación y cargamos sus valores en variables locales
	if (argc != 3) {
        log_error(logger, "ERROR DE PARAMETROS");
        return 1;
    }

    ruta_configuracion = argv[1];
	ruta_pseudocodigo = argv[2];



	t_config* config = iniciar_config(ruta_configuracion);

	log_info(logger,"Config: leyendo el archivo de configuracion...");
	ip = config_get_string_value(config, "IP_KERNEL");
	puerto = config_get_string_value(config, "PUERTO_KERNEL");
	log_info(logger,"El archivo Config fue leido corectamente");

	//Leemos el archivo de pseudocódigo y lo cargamos en el string lista_de_instrucciones que sera enviado al kernel
	log_info(logger,"Pseudocodigo: leyendo el archivo de pseudocodigo...");
	char* lista_de_instrucciones = cargar_instrucciones(ruta_pseudocodigo, logger);
	log_info(logger,"El archivo de pseudocodigo fue leido correctamente");

	//Iniciamos la conexion con el kernel, utilizando los datos de configuración
	log_info(logger,"Conexion Kernel: intentando conectar con el Kernel...");
	int conexion_kernel = crear_conexion(ip, puerto);
	log_info(logger,"Se ha podido establecer una conexion con el Kernel");

	//Enviamos el handshakey recibimos el resultado en result
	//handshake_cliente(conexion_kernel, handshake,logger);

	//Enviamos las instrucciones al kernel
	log_info(logger, "Conexion Kernel: enviando las instrucciones al Kernel...");
	if(enviar_string(conexion_kernel,lista_de_instrucciones, CONSOLA) < 0){
		log_error(logger, "IMPOSIBLE ENVIAR DATA AL KERNEL, CERRANDO CONSOLA.");
		exit(4);
	}
	log_info(logger, "Conexion Kernel: instrucciones enviadas correctamente, esperando finalizacion del proceso...");
	//Esperamos la respuesta del kernel

	esperar_respuesta(conexion_kernel, logger);

	//Terminamos el programa liberando los datos utilizados
	log_debug(logger, "Cerrando consola");
	terminar_programa(conexion_kernel, logger, config);
	return 0;
}

#include "include/cpu.h"

int main(void){
	char* puerto_escucha;
	char* puerto_memoria;
	char* ip_memoria;
	int handshake_key = 4;
	int handshake_Kernel;
	int result_Memoria;
	int resultOk = 0;
	int resultError = -1;
	int retardo_de_instruccion;
	int tam_max_seg;



	t_dictionary* registros = inicializarRegistros();


	//Inicializamos el log
	t_log* logger = iniciar_logger("./logscpu.log","Log-CPU");
	log_debug(logger, "Se inició el Programa");


	//Creamos el archivo de configuación
	t_config* config = iniciar_config("./cpu.config");
	log_debug(logger, "Se generó el archivo config");

	//Cargamos la configuración en variables
	log_debug(logger, "Intentando leyendo la configuración...");
	leer_config(logger,config, &puerto_escucha,  &puerto_memoria, &ip_memoria, &retardo_de_instruccion, &tam_max_seg);




	//Creamos la conexión con la Memoria
	log_debug(logger, "Intentando crear el socket para la conexion con Memoria");
	int conexion_memoria= crear_conexion(ip_memoria, puerto_memoria);
	log_info(logger,"Se creo el socket para la conexion con la Memoria.");


	// Hacemos el handshake CPU - Memoria
	handshake_cliente (logger, conexion_memoria, &result_Memoria, &handshake_key);
	if (result_Memoria != resultOk){
		log_error(logger, "ERROR EN EL HANDSHAKE CPU -> MEMORIA!");
	//  exit(2);
	}
	else {
		log_debug(logger, "Hanshake CPU -> Memoria realizado correctamente");
	}





	//Iniciamos el servidor y esperamos el kernel
	int server_kernel = iniciar_servidor(logger, puerto_escucha);
	log_info(logger, "Servidor listo para recibir al kernel");
	int conexion_kernel = esperar_cliente(server_kernel, logger);



	// Hacemos el handshake CPU - Kernel
	log_debug (logger, "Iniciando el handshake Kernel");
	handshake_servidor (logger, conexion_kernel, &handshake_Kernel, &resultOk, &resultError, 1);
	log_debug (logger, "Handshake terminado Kernel");


//TRABAJAMOS CON PUNTEROS

while (1)
{
	t_contexto* contexto = recibir_paquete(conexion_kernel);
	log_debug(logger, "El pcb \"%d\" se recibio correctamente",contexto->pid);
	log_debug(logger, "INTRUCCIONES \n%s\n",contexto->instrucciones);

	actualizar_registro(registros, contexto->registros_cpu); //Cambia los registros del CPU segun el contexto del contexto
	log_debug(logger, "Se actualizo los registros");
	//supongamos esta hecho

	contexto->estado_actual = EXEC;
	log_debug(logger, "contexto en ejec");
	while (contexto->estado_actual == EXEC)
	{
		char** instruccionesAux = string_split(contexto->instrucciones, "|"); //"set ax hola|exit" ==> ["set ax hola","exit",NULL]
		char** instruccion = string_split(instruccionesAux[contexto->program_counter], " "); //"set ax hola" ==> ["set","ax","hola",NULL]
		realizar_instruccion(contexto, registros, instruccion, conexion_kernel, conexion_memoria, logger, retardo_de_instruccion, tam_max_seg);
		
		contexto->program_counter++;
   }
   
   if(contexto->estado_actual == ENDED || contexto->estado_actual == READY || contexto->estado_actual == BLOCK){
	actualizar_contexto(contexto, registros);
	log_debug(logger, "DEVOLVIENDO CONTEXTO");
	enviar_contexto(conexion_kernel,contexto);
	//1. senalizar
	//2. enviar al kernel
   }

	liberar_contexto(contexto);
	
}

	//Terminamos el progama y liberamos las conexiones, logger y config
	log_debug(logger, "Fin del programa. Liberando loggs, config, sockets y registros");
	liberarRegistros(registros);
	//terminar_programa(logger, config, conexion_memoria);
	return 0;
}

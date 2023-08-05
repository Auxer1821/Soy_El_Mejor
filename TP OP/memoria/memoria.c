#include "include/memoria.h"

int main (void){
	char* puerto_escucha;
	int handshake_Kernel;
	int handshake_CPU;
	int handshake_FileSystem;
	int resultOk = 0;
	int resultError = -1;

	int tamanio_memoria;
	int tamanio_segmento_0;
	int cant_segmentos;
	int retardo_memoria;
	int retardo_compactacion;
	char* algoritmo_asignacion;


	

//-------------------------LOGGERS+CONFIG--------------------------------------
	//Inicializamos el log
	t_log* logger = iniciar_logger("./logsmemoria.log", "Log-memoria");
	log_debug(logger, "Se inició el Programa");

	//Creamos el archivo de configuación
	t_config* config = iniciar_config("./memoria.config");
	log_debug(logger, "Se generó el archivo config");

	//Cargamos la configuración en variables
	log_debug(logger, "Intentando leyendo la configuración...");
	leer_config(logger,config, &puerto_escucha, &tamanio_memoria, &tamanio_segmento_0, &cant_segmentos, &retardo_memoria, &retardo_compactacion, &algoritmo_asignacion);

	//Iniciamos el servidor (General)
	log_debug(logger, "Intentando iniciar servidor");
	int server = iniciar_servidor(logger, puerto_escucha);
//---------------------------------------------------------------------------

	t_memoria* memoria = inicializar_memoria(retardo_compactacion, retardo_memoria, tamanio_memoria, algoritmo_asignacion, tamanio_segmento_0, logger);

//-------------------------CONEXIONES-----------------------------------------
	//conexion con CPU
	log_info(logger, "Servidor listo para recibir al CPU");
	int conexion_cpu = esperar_cliente(server, logger);


	//Realizamos el handshake con CPU
	log_debug (logger, "Iniciando el handshake CPU");
	handshake_servidor (logger, conexion_cpu, &handshake_CPU, &resultOk, &resultError, 4);
	log_debug (logger, "Handshake terminado CPU");


	//conexion FileSisten
	log_info(logger, "Servidor listo para recibir al File System");
	int conexion_file_system = esperar_cliente(server, logger);

	//Realizamos el handshake con FileSystem
	log_debug (logger, "Iniciando el handshake FileSystem");
	handshake_servidor (logger,conexion_file_system, &handshake_FileSystem, &resultOk, &resultError, 2);
	log_debug (logger, "Handshake terminado FileSystem");


	//Iniciamos el servidor y esperamos al Kernel, la CPU y el File System
//	log_debug(logger, "Intentando iniciar servidor para Kernel");
//	int server = iniciar_servidor(logger, puerto_escucha);


	//conexion kernel
	log_info(logger, "Servidor listo para recibir al Kernel");
	int conexion_kernel = esperar_cliente(server, logger);

	//Realizamos el handshake con Kernel
	log_debug (logger, "Iniciando el handshake Kernel");
	handshake_servidor (logger, conexion_kernel, &handshake_Kernel, &resultOk, &resultError, 1);
	log_debug (logger, "Handshake terminado Kernel");


//---------------------------------------------------------------------------

	pthread_mutex_t Aux_mutex_memory;
	pthread_mutex_t* mutex_memory = &Aux_mutex_memory;		
	if ( pthread_mutex_init(mutex_memory, NULL) != 0){
		log_error(logger,"ERROR INICIAR EL MUTEX");
		exit(3);
	}



	
//-----------------------------------------VALIDADO----------------------------------------------



//------------------------HILOS------------------------------------------------------------

pthread_t hilo_De_Kernel;
pthread_t hilo_De_CPU;
pthread_t hilo_De_FS;

t_data_hilo* arg_kernel = malloc(sizeof(t_data_hilo));
arg_kernel->memoria=memoria;
arg_kernel->socket=conexion_kernel;

t_data_hilo* arg_cpu = malloc(sizeof(t_data_hilo));
arg_cpu->memoria=memoria;
arg_cpu->socket=conexion_cpu;


t_data_hilo* arg_fs = malloc(sizeof(t_data_hilo));
arg_fs->memoria=memoria;
arg_fs->socket=conexion_file_system;

pthread_create (&hilo_De_Kernel, NULL, funcion_De_Kernel, arg_kernel);
pthread_create (&hilo_De_CPU, NULL, funcion_De_CPU, arg_cpu);
pthread_create (&hilo_De_FS, NULL, funcion_De_FS, arg_fs);


pthread_join(hilo_De_Kernel, NULL);
pthread_join(hilo_De_CPU, NULL);
pthread_join(hilo_De_FS, NULL);





































	
	
	
	
	
	
//-------------------------FINALIZACION--------------------------------------
	//Terminamos el progama y liberamos las conexiones, logger y config
	log_info(logger, "Fin del programa de Memoria");
	terminar_programa(logger, config);
	return 0;
}

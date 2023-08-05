#include "include/kernel.h"

int main (void){
	char* puerto_escucha = "48610";
	char* puerto_cpu;
	char* puerto_file_system;
	char* puerto_memoria = "46850";
	char* ip_cpu;
	char* ip_file_system;
	char* ip_memoria;
	int conexion_cpu;
	int conexion_file_system;
	int conexion_memoria;
//	t_instruccion handshake;
	int handshake_Consola;
	int handshake_key = 1;
	int result_CPU;
	int result_FileSystem;
	int result_Memoria;
	int resultOk = 0;
	int resultError = -1;

// HILOS:
	//pthread_t hilo1_Conexion;
//LISTAS Y COLAS
	t_list* lista_de_socket = list_create();
	t_list* lista_de_conexiones = list_create();
	t_list* lista_de_PCBs_NEW = list_create();
	t_list* lista_de_PCBs_READY = list_create();
	t_list* lista_de_PCBs_ENDED = list_create();

	t_temporal* tiempo_global = temporal_create();



//-----------------------Inicio_logger------------------------------------------------------------------------

	t_log* logger = iniciar_logger("./logskernel.log", "Log-kernel");
	log_debug(logger, "Se inició el programa");

	t_archivos_kernel diccionario_Archivos_Global = inicializar_diccionario_archivos(logger);

//-----------------------Inicio_config------------------------------------------------------------------------

	t_config* config = iniciar_config("./kernel.config");
	log_debug(logger, "Se inició la configuración");

	//Cargamos la configuración en variables
	leer_config(logger, config, &puerto_escucha, &puerto_cpu, &puerto_file_system, &puerto_memoria, &ip_cpu, &ip_file_system, &ip_memoria);
	int alfa = config_get_int_value(config, "ALGORITMO_PLANIFICACION");




//-----------------------Conexion_CPU------------------------------------------------------------------------

	log_debug(logger, "Intentando crear el socket para la conexion con CPU");
	conexion_cpu = crear_conexion(ip_cpu, puerto_cpu);
	log_info(logger,"Se creo el socket para la conexion con la CPU.");


	// Hacemos el handshake con el CPU
	handshake_cliente (logger, conexion_cpu, &result_CPU, &handshake_key);
	if (result_CPU != 0){
		log_error(logger, "ERROR EN EL HANDSHAKE KERNEL -> CPU!, exit 1");
//		exit(1);
	}
	else {
		log_debug(logger, "Hanshake Kernel -> CPU realizado correctamente");
	}







	

//-----------------------Conexion_Filesystem------------------------------------------------------------------------

	log_debug(logger, "Intentando crear el socket para la conexion con FileSystem");
	conexion_file_system = crear_conexion(ip_file_system, puerto_file_system);
	log_info(logger,"Se creo el socket para la conexion con la FileSystem.");


	// Hacemos el handshake con el FileSystem
	handshake_cliente (logger, conexion_file_system, &result_FileSystem, &handshake_key);
	if (result_FileSystem != 0){
		log_error(logger, "ERROR EN EL HANDSHAKE KERNEL -> FILESYSTEM!");
	//	exit(2);
	}

	else {
		log_info(logger, "Hanshake Kernel -> FileSystem realizado correctamente");
	}






//-----------------------Conexion_memoria------------------------------------------------------------------------

	log_debug(logger, "Intentando crear conexion");
	conexion_memoria = crear_conexion(ip_memoria, puerto_memoria);
	log_info(logger, "Se creó el socket con memoria");

	// Hacemos el handshake con el Memoria
	handshake_cliente (logger, conexion_memoria, &result_Memoria, &handshake_key);
	if (result_Memoria != resultOk){
		log_error(logger, "ERROR EN EL HANDSHAKE KERNEL -> MEMORIA!, exit 3");
//		exit(3);
	}

	else {
		log_info(logger, "Hanshake Kernel -> Memoria realizado correctamente");
	}





//-----------------------RECURSOS------------------------------------------------------------------------

	t_dictionary* diccionario_recursos =inicializarRecursos(config,logger);
	sem_t aux_sem_recursos;
	sem_t* sem_recursos = &aux_sem_recursos;
	sem_init (sem_recursos, 0, 0);


	sem_t Aux_semaforo_PCB_Creacion;
	sem_t* semaforo_PCB_Creacion = &Aux_semaforo_PCB_Creacion;
	sem_init (semaforo_PCB_Creacion, 0, 0);
	//sem_init (semaforo_PCB_Creacion, 0, 1);


	pthread_mutex_t Aux_mutex_soket;
	pthread_mutex_t* mutex_soket = &Aux_mutex_soket;		// hilos {2,5}
	if ( pthread_mutex_init(mutex_soket, NULL) != 0){
		log_error(logger,"ERROR INICIAR EL MUTEX");
		exit(3);
	}
	

	hilo_1* argumento_De_Hilo1 = inicializar_Hilo1 (puerto_escucha, lista_de_socket, semaforo_PCB_Creacion, mutex_soket);


	
	pthread_t hilo1;


	if (pthread_create (&hilo1, NULL, funcion_Hilo1, argumento_De_Hilo1) != 0){
		log_error(logger, "ERROR al intentar crear HILO 1");

	}


	



	sem_t Aux_semaforo_FinReady_De_procesos;
	sem_t* semaforo_FinReady_De_procesos = &Aux_semaforo_FinReady_De_procesos;		//{2,3,5}
	sem_init(semaforo_FinReady_De_procesos, 0, 0);


	pthread_mutex_t Aux_mutex_Lista_NEW;
	pthread_mutex_t* mutex_Lista_NEW = &Aux_mutex_Lista_NEW;		// hilos {2,5}
	if ( pthread_mutex_init(mutex_Lista_NEW, NULL) != 0){
		log_error(logger,"ERROR INICIAR EL MUTEX");
		exit(3);
	}

	pthread_mutex_t  Aux_mutex_Lista_conexiones;
	pthread_mutex_t*  mutex_Lista_conexiones = &Aux_mutex_Lista_conexiones;  // hilo{2,5}
	if ( pthread_mutex_init(mutex_Lista_conexiones, NULL) != 0){
		log_error(logger,"ERROR INICIAR EL MUTEX");
		exit(3);
	}



	pthread_t hilo2;
	hilo_2* argumento_De_Hilo2 = inicializar_Hilo2 (lista_de_socket, lista_de_conexiones, lista_de_PCBs_NEW, semaforo_PCB_Creacion, semaforo_FinReady_De_procesos, config, mutex_Lista_NEW, mutex_Lista_conexiones, mutex_soket);
	

	pthread_create (&hilo2, NULL, funcion_Hilo2, argumento_De_Hilo2);





	pthread_mutex_t Aux_mutex_Lista_READY;		// hilos {3,4,5}
	pthread_mutex_t* mutex_Lista_READY = &Aux_mutex_Lista_READY;
	if ( pthread_mutex_init(mutex_Lista_READY, NULL) != 0){
		log_error(logger,"ERROR INICIAR EL MUTEX");
		exit(3);
	}



	//Pasamos un PCB a READY
	//cuando se cree este hilo o se modifica el 5 hay q pasar el sem de lista_ready
	//mover_proceso_NEW_a_READY(lista_de_PCBs_NEW, lista_de_PCBs_READY);

	sem_t Aux_semaforo_hay_algo_listo;
	sem_t* semaforo_hay_algo_listo = &Aux_semaforo_hay_algo_listo;		//{3,5}
	sem_init(semaforo_hay_algo_listo, 0, 0);

	pthread_mutex_t Aux_mutex_Lista_PCBs_ENDED;		// hilos {3,4,5}
	pthread_mutex_t* mutex_Lista_PCBs_ENDED = &Aux_mutex_Lista_PCBs_ENDED;
	log_debug(logger, "A punto de iniciar el mutex de los cojones");
	if ( pthread_mutex_init(mutex_Lista_PCBs_ENDED, NULL) != 0){
		log_error(logger,"ERROR INICIAR EL MUTEX");
		exit(3);
	}

	pthread_t hiloRecursos;
	hilo_Recursos* argumento_H_Recurso = inicializar_Hilo_Recursos(diccionario_recursos, sem_recursos, lista_de_PCBs_READY, mutex_Lista_READY,semaforo_hay_algo_listo, tiempo_global, alfa);
	pthread_create (&hiloRecursos, NULL, funcion_Hilo_Recursos, argumento_H_Recurso);


	t_compactacion* lista_pcbs_creados = inicializar_t_compactacion(logger);

	pthread_t hilo5;
	hilo_5* argumento_De_Hilo5 = inicializar_Hilo5 (lista_de_PCBs_ENDED, lista_de_conexiones, semaforo_FinReady_De_procesos, lista_de_PCBs_READY , lista_de_PCBs_NEW, mutex_Lista_READY, mutex_Lista_NEW, mutex_Lista_conexiones,mutex_Lista_PCBs_ENDED ,semaforo_hay_algo_listo, diccionario_recursos , sem_recursos,  config, tiempo_global, lista_pcbs_creados, conexion_memoria);
	pthread_create (&hilo5, NULL, funcion_Hilo5, argumento_De_Hilo5);


	pthread_mutex_t Aux_mutex_pet_FS;		// hilos {3,ptfs}
	pthread_mutex_t* mutex_pet_FS = &Aux_mutex_pet_FS;
	if ( pthread_mutex_init(mutex_pet_FS, NULL) != 0){
		log_error(logger,"ERROR INICIAR EL MUTEX");
		exit(3);
	}

	sem_t Aux_semaforo_pet_fs;
	sem_t* semaforo_pet_fs = &Aux_semaforo_pet_fs;		//{3,petfs}
	sem_init(semaforo_pet_fs, 0, 0);

	t_peticiones peticiones = crear_t_peticiones();

	sem_t AUX_binario_open;
	sem_t* binario_open = &AUX_binario_open;		//{3,PET}
	sem_init(binario_open, 0, 0);



	pthread_t hilo_pet_FS;
	t_hilo_pet_FS* argumento_De_hilo_pet_FS = inicializar_peticiones_FS(conexion_file_system, peticiones, lista_de_PCBs_READY, mutex_pet_FS, mutex_Lista_READY, semaforo_pet_fs, semaforo_hay_algo_listo, tiempo_global, binario_open, alfa);
	pthread_create (&hilo_pet_FS, NULL, hiloPeticionFS, argumento_De_hilo_pet_FS);

	t_hilo_4_LOGS hilo_4_LOGS = crear_t_hilo_4_LOGS();

	hilo_3* argumento_De_Hilo3 = inicializar_Hilo3 (lista_de_PCBs_READY, conexion_cpu, conexion_file_system, conexion_memoria, lista_de_PCBs_ENDED, diccionario_recursos, sem_recursos, semaforo_FinReady_De_procesos, mutex_Lista_READY, mutex_Lista_PCBs_ENDED, semaforo_hay_algo_listo, config, tiempo_global, diccionario_Archivos_Global , semaforo_pet_fs, peticiones, hilo_4_LOGS, binario_open, mutex_pet_FS, lista_pcbs_creados, alfa);
	pthread_t hilo3;
	pthread_create (&hilo3, NULL, funcion_Hilo3, argumento_De_Hilo3);



	//Terminamos el progama y liberamos las conexiones, logger y config
	log_debug(logger, "Fin del programa. Liberando loggs, config y sockets");
	
	
	
	//terminar_programa(logger, config, conexion_cpu, conexion_file_system, conexion_memoria);
	

	//implementar funcion 
	//pthread_join(hilo1_Conexion, NULL);
	pthread_join(hilo1, NULL);
	pthread_join(hilo2, NULL);
	pthread_join(hilo3, NULL);
	pthread_join(hilo5, NULL);
	pthread_join(hiloRecursos,NULL);




	list_destroy(lista_de_PCBs_NEW);
	list_destroy(lista_de_conexiones);
	list_destroy(lista_de_socket);

	//implementar funcion
	sem_destroy(semaforo_PCB_Creacion);
	sem_destroy(semaforo_FinReady_De_procesos);
	temporal_destroy(tiempo_global);
	return 0;
}

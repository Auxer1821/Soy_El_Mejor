#include "include/filesystem.h"

int main(void){

	char* puerto_escucha;
	char* puerto_memoria;
	char* ip_memoria;
	int handshake_key = 2;
	int handshake_Memoria = 4;
	int handshake_Kernel;
	int result_Memoria;
	int resultOk = 0;
	int resultError = 1;
	t_info_bloques info_bloques;
	t_config_fileSystem config_fileSistem;

//----------CONFIG INICIAL------------------------------------------------------------------------

	//Inicializamos el log
	t_log* logger = iniciar_logger("./logsfilesystem.log", "Log-FILESYSTEM");
	log_debug(logger, "Se inicio el logger");

	//Creamos el archivo de configuación
	t_config* config = iniciar_config("./filesystem.config");
	log_debug(logger, "Se inicio el archivo config");


	//Cargamos la configuración en variables
	leer_config(logger, config, &config_fileSistem);
	log_debug(logger, "Se leyo la configuracion");

	leer_superBloque(logger, config_fileSistem.path_super_bloque, &info_bloques.tamBloques, &info_bloques.cantBloques);
	info_bloques.BitMapBloque =  InicializarBitMap(logger, config_fileSistem.path_bitmap, info_bloques.cantBloques);
	info_bloques.archivo_bloque = iniciar_archivo_bloques(logger, config_fileSistem.path_bloques, info_bloques.cantBloques, info_bloques.tamBloques);

		
//--------------------------------------------------------------------------------------------

//----------CONEXIONES Y HANDSHAKE------------------------------------------------------------------------
	
	//Creamos la conexión con la Memoria
	log_debug(logger, "Intentando crear el socket para la conexion con Memoria");
	int conexion_memoria = crear_conexion(config_fileSistem.ip_memoria, config_fileSistem.puerto_memoria);
	log_info(logger,"Se creo la conexion con la Memoria.");

	// Hacemos el handshake FILESYSTEM - Memoria
	handshake_cliente (logger, conexion_memoria, &result_Memoria, &handshake_key);
	if (result_Memoria != resultOk){
		log_error(logger, "ERROR EN EL HANDSHAKE FILESYSTEM -> MEMORIA!");
	//  exit(2);
	}
	else {
		log_debug(logger, "Hanshake FileSystem -> Memoria realizado correctamente");
	}

//--------------------------------------------------------------------------------------------





//--------------------------------------------------------------------------------------------
	//Iniciamos el servidor (general)
	int server = iniciar_servidor(logger, config_fileSistem.puerto_escucha);


	log_info(logger, "Servidor listo para recibir al kernel");
	int conexion_kernel = esperar_cliente(server, logger);


	// Hacemos el handshake FILESYSTEM - Kernel
		log_debug (logger, "Iniciando el handshake kernel");
	handshake_servidor(logger, conexion_kernel, &handshake_Kernel, &resultOk, &resultError,1);
	log_debug (logger, "Handshake terminado kernel");

//-------Inicia el diccionario y carga los datos-----------------------------------------------------------------------------

	t_dictionary* fcbs_dic = abrir_FCBs(logger, config_fileSistem.path_fcb, info_bloques.BitMapBloque);

	off_t n = 1;
	bool v = bitarray_test_bit(info_bloques.BitMapBloque , n);

	//realizar_instruccion(0,0,F_OPEN,"prueba.txt",logger,config_fileSistem,info_bloques,fcbs_dic);
	//Tenemos char* [nombre_archivo, dirección_memoria, bytes, puntero_posicion_archivo]]
	//Tenemos char* [nombre_archivo, dirección_memoria, bytes, puntero_posicion_archivo]]
	//realizar_instruccion(0,0,F_TRUNCATE,"prueba.txt|10",logger,config_fileSistem,info_bloques,fcbs_dic);
	//realizar_instruccion(0,0,F_READ,"prueba.txt|0|16|0",logger,config_fileSistem,info_bloques,fcbs_dic);

//-------PROGRAMA PRINCIPAL MAIN-----------------------------------------------------------------------------
	while (1)
	{
		recibir_paquete_kernel(conexion_kernel, conexion_memoria, logger, config_fileSistem, info_bloques, fcbs_dic);

	}	


//----------FIN DEL PROGRAMA------------------------------------------------------------------------

	//Terminamos el progama y liberamos las conexiones, logger y config
	terminar_programa(logger, config, conexion_memoria); 
	return 0;
}


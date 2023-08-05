#include "../include/procesos.h"

//-----------------------leer_FCB------------------------------------------------------------------------

t_FCB* leer_FCB(char* path_FCB)
{

	t_FCB* fcb = malloc(sizeof(t_FCB));
	
	t_config* FCB_config = iniciar_config(path_FCB);
	char* nombre = config_get_string_value(FCB_config, "NOMBRE_ARCHIVO");
	fcb->nombre_archivo = string_duplicate(nombre);
	fcb->tamanio_archivo = config_get_int_value(FCB_config, "TAMANIO_ARCHIVO");
	fcb->direct_pointer = config_get_int_value(FCB_config, "PUNTERO_DIRECTO");
	fcb->indirect_pointer = config_get_int_value(FCB_config, "PUNTERO_INDIRECTO");

	config_destroy(FCB_config);

	return fcb;

}
//---------------------BLOQUES----------------------------------------------------------------------------------------

//------------------------------------------------------------------
char* buscarBloqueEnArchivo(t_log* logger, char* archivo_de_bloque, t_FCB* fcb, uint32_t bloqueActualDelFCB, t_info_bloques info_bloques, t_config_fileSystem config_files){

	uint32_t pos_pointer;
	if(bloqueActualDelFCB==0)
	{
		pos_pointer=fcb->direct_pointer;
	}
	else
	{
		bloqueActualDelFCB--;//para movernos desde 0 en el puntero indirecto
		uint32_t* bloqueIndirecto = (uint32_t*)get_bloque(info_bloques,fcb->indirect_pointer,config_files);
		log_info(logger, "Acceso Bloque - Archivo: %s - Bloque File System %d (Bloque indirecto)", fcb->nombre_archivo, fcb->indirect_pointer);
		pos_pointer=bloqueIndirecto[bloqueActualDelFCB];
		bloqueActualDelFCB++;

	}

	sleep_ms(config_files.retardo_acceso_bloque);
	
	log_info(logger, "Acceso Bloque - Archivo: %s - Bloque Archivo: %d - Bloque File System %d", fcb->nombre_archivo, bloqueActualDelFCB, pos_pointer);
	return archivo_de_bloque + (pos_pointer*info_bloques.tamBloques); 
}

//------------------------------------------------------------------
char* get_bloque(t_info_bloques info_bloques, int pos, t_config_fileSystem config_files){ // Te devuelve el bloque respecto al file system
	sleep_ms(config_files.retardo_acceso_bloque);
	return info_bloques.archivo_bloque + (pos * info_bloques.tamBloques);
}

//------------------------------------------------------------------
uint32_t pedir_bloque(t_log* logger, t_bitarray* bitMapBloque)
{
	uint32_t r=0;
	int max = bitarray_get_max_bit(bitMapBloque);
	while(max > r  && bitarray_test_bit(bitMapBloque,r)){
		r++;
	}
		
	
	log_info(logger, "Acceso a Bitmap - Bloque: %u - Estado: 0", r);

	bitarray_set_bit(bitMapBloque,r);
	msync(bitMapBloque->bitarray, sizeof(char), MS_SYNC);

	return r;
}

//------------------------------------------------------------------
void liberar_bloque (t_log* logger, t_bitarray* bitMapBloque, uint32_t pos)
{
	log_info(logger, "Acceso a Bitmap - Bloque: %u - Estado: 1", pos);
	bitarray_clean_bit(bitMapBloque, pos);
	msync(bitMapBloque->bitarray, sizeof(char), MS_SYNC);
}

//-----------------------FCB------------------------------------------------------------------------

//------------------------------------------------------------------
t_FCB* crer_FCB (t_log* logger, char* path_FCB , char* nombre , t_bitarray* bitMapBloque) //Función que crea el archivo de FCB para un nuevo archivo de configuración
{
	t_FCB* fcb = malloc(sizeof(t_FCB));
	fcb->nombre_archivo=string_duplicate(nombre);
	fcb->tamanio_archivo= 0;
	fcb->direct_pointer = pedir_bloque(logger, bitMapBloque);
	fcb->indirect_pointer = pedir_bloque(logger, bitMapBloque);

	FILE* f = fopen(path_FCB,"w+b");
	fclose(f);
	t_config* FCB_config = iniciar_config(path_FCB);
	config_set_value(FCB_config,"NOMBRE_ARCHIVO",nombre);

	char* number=string_itoa(fcb->tamanio_archivo);
	config_set_value(FCB_config,"TAMANIO_ARCHIVO",number);

	number=string_itoa(fcb->direct_pointer);
	config_set_value(FCB_config,"PUNTERO_DIRECTO",number);

	number=string_itoa(fcb->indirect_pointer);
	config_set_value(FCB_config,"PUNTERO_INDIRECTO",number);

	config_save(FCB_config);
	config_destroy(FCB_config);


	return fcb;
}

//------------------------------------------------------------------
t_dictionary* abrir_FCBs(t_log* logger, char* path_FCBs, t_bitarray* bitMap){
	
	t_dictionary* diccionario = dictionary_create();

 	DIR *folder;
	folder = opendir(path_FCBs);

	struct dirent *entry;

    folder = opendir(path_FCBs);
    if(folder == NULL)
    {
        perror("Unable to read directory");//loggerError
        exit(1);
    }

	while(  (entry=readdir(folder))  )
    {
		if(strcmp(entry->d_name,"..")!=0 && strcmp(entry->d_name,".")!=0 )
			open_file( logger, path_FCBs, entry->d_name, diccionario, bitMap);
    }

    closedir(folder);

	return diccionario;

}
//-----------------------FUNCIONES------------------------------------------------------------------------

//-----------------------F_OPEN------------------------------------------------------------------------
void open_file(t_log* logger, char* path_FCBs, char* nombre, t_dictionary* FCBs_Dic, t_bitarray* bitMapBloque){


	char* path_complete = string_new();
	//char* nombre_aux = malloc(string_length(nombre)); POR SI NO ANDA NOMBRE
	//memcpy(nombre_aux, nombre, string_length(nombre));

	string_append(&path_complete, path_FCBs);
	string_append(&path_complete, "/");
	string_append(&path_complete, nombre);
	char* key = string_duplicate(nombre); 


	t_FCB* fcb;
	if(!dictionary_has_key(FCBs_Dic,key)){
		if(access(path_complete, F_OK) != -1)
		{
			log_info(logger, "Abrir Archivo: %s", nombre);
			log_debug(logger, "Leyendo config del FCB del archivo %s", nombre);
			fcb = leer_FCB(path_complete);
			log_debug(logger, "Lectura de la config del FCB completada");
		}
		else
		{
			log_info(logger, "Crear Archivo: %s", nombre);
			log_debug(logger, "Creando config del FCB del archivo %s", nombre);
			fcb = crer_FCB(logger, path_complete, nombre, bitMapBloque);
			log_debug(logger, "Creacion de la config del FCB completada");
		}
		dictionary_put(FCBs_Dic, key, fcb);
	}
}

//-----------------------F_TRUNCATE------------------------------------------------------------------------
void truncate_file(t_log* logger, char* instruccion, char* path_FCBs, t_dictionary* FCBs_Dic, t_info_bloques info_bloques, t_config_fileSystem config_files ){

	char** instruccionSeparada = string_split(instruccion,"|"); // [Nombre Archivo, Tamaño];
	t_FCB* fcb = dictionary_get(FCBs_Dic, instruccionSeparada[0]);
	int cant_Byte = atoi(instruccionSeparada[1]);


//ej:  tf300  p700   tb 200
	if(cant_Byte > fcb->tamanio_archivo){ // Caso AMPLIAR el archivo


		cant_Byte -= info_bloques.tamBloques;
		fcb->tamanio_archivo -= info_bloques.tamBloques;

		char* bloque_indirecto =  get_bloque(info_bloques, fcb->indirect_pointer, config_files);
		log_info(logger, "Acceso Bloque - Archivo: %s - Bloque File System %d (Bloque indirecto)", fcb->nombre_archivo, fcb->indirect_pointer);
		int pos_bloque_archivo = 0;
		while(cant_Byte>0 &&  pos_bloque_archivo < info_bloques.tamBloques/sizeof(uint32_t)  )
		{
			if(!(fcb->tamanio_archivo>0)){
				uint32_t pos = pedir_bloque(logger, info_bloques.BitMapBloque);
				char* direccion_con_offest = bloque_indirecto+(pos_bloque_archivo*sizeof(uint32_t));
				
				memcpy(direccion_con_offest, &pos, sizeof(uint32_t));
				msync(info_bloques.archivo_bloque, sizeof(char), MS_SYNC);
				log_info(logger, "Acceso Bloque - Archivo: %s - Bloque Archivo: %u - Bloque File System: %u", fcb->nombre_archivo, (pos_bloque_archivo+1), pos);
			}

			fcb->tamanio_archivo-=info_bloques.tamBloques;
			cant_Byte-=info_bloques.tamBloques;
			pos_bloque_archivo++;
		}
	}
	else if(cant_Byte < fcb->tamanio_archivo) // Caso REDUCIR el archivo
	{
		// 30  -- 5     1  2      [0, [1,2]]
		cant_Byte -= info_bloques.tamBloques;
		cant_Byte=(cant_Byte<0)?0:cant_Byte;
		fcb->tamanio_archivo-=info_bloques.tamBloques;
		//
		int cant_bloquesPerdidos = ( fcb->tamanio_archivo - cant_Byte) / info_bloques.tamBloques;
		//2
		uint32_t pos_bloque_archivo = fcb->tamanio_archivo / info_bloques.tamBloques + (   (fcb->tamanio_archivo%info_bloques.tamBloques == 0)?0:1   ) ;
		//4
		pos_bloque_archivo--;//RESTARLE EL UNICO DIRECTO
		//3
		uint32_t* bloque_indirecto = (uint32_t*) get_bloque(info_bloques, fcb->indirect_pointer,  config_files);
		log_info(logger, "Acceso Bloque - Archivo: %s - Bloque File System %d (Bloque indirecto)", fcb->nombre_archivo, fcb->indirect_pointer);
		
		while (cant_bloquesPerdidos>0){
			uint32_t pos_delBloqueIndirectoDirecto;
			memcpy(&pos_delBloqueIndirectoDirecto ,bloque_indirecto + pos_bloque_archivo , sizeof(uint32_t));
			liberar_bloque(logger, info_bloques.BitMapBloque , pos_delBloqueIndirectoDirecto);
			log_info(logger, "Acceso Bloque - Archivo: %s - Bloque Archivo: %u - Bloque File System %u", fcb->nombre_archivo, pos_bloque_archivo, *(bloque_indirecto + pos_bloque_archivo));
			pos_bloque_archivo--;
			cant_bloquesPerdidos--;
		}

	}





	int cant_Byte_Maximo = info_bloques.tamBloques + ( info_bloques.tamBloques/sizeof(uint32_t) ) * info_bloques.tamBloques;
	int tam_byte_fcb_pedido =atoi(instruccionSeparada[1]);
	int tam_fcb_nuevo= ( tam_byte_fcb_pedido < cant_Byte_Maximo ) ? tam_byte_fcb_pedido : cant_Byte_Maximo;


	fcb->tamanio_archivo=tam_fcb_nuevo;
	
	log_info(logger, "Truncar Archivo: %s - Tamaño: %d", fcb->nombre_archivo, tam_fcb_nuevo);

	//---actualizar disco-----------
	//---ACTUALIZAR FCB----
	char* path_complete = string_new();
	string_append(&path_complete, path_FCBs);
	string_append(&path_complete, "/");
	string_append(&path_complete, fcb->nombre_archivo);

	
	t_config* FCB_config = iniciar_config(path_complete);
	char* number = string_itoa(fcb->tamanio_archivo);
	config_set_value(FCB_config, "TAMANIO_ARCHIVO", number);

	config_save(FCB_config);
	config_destroy(FCB_config);

}

//-----------------------F_READ------------------------------------------------------------------------

void leer_archivo(t_log* logger, int socket_memoria, char* instruccion, t_config_fileSystem config, t_info_bloques info_bloques, t_dictionary* FCBs_Dic, t_config_fileSystem config_files){ //logger, datos, config_files, info_bloques, diccionario_FCBs
	
	char** instruccionSeparada = string_split(instruccion,"|"); //Tenemos char* [nombre_archivo, dirección_memoria, bytes, puntero_posicion_archivo]]

	char* nombre = instruccionSeparada[0];
	int tamanio = atoi(instruccionSeparada[2]);
	int posicion_actual = atoi(instruccionSeparada[3]);

	log_info(logger, "Leer Archivo: %s - Puntero: %d - Memoria: %s - Tamaño: %d", nombre, posicion_actual, instruccionSeparada[1], tamanio);
	
	t_FCB* fcb = dictionary_get(FCBs_Dic,nombre);

	char* bits_a_enviar = malloc (tamanio);

	
	t_dato_DF* dato_a_enviar=malloc(sizeof(t_dato_DF));
	dato_a_enviar->longitud=tamanio;
	dato_a_enviar->valor=bits_a_enviar;
	dato_a_enviar->df = atoi(instruccionSeparada[1]);
	

	leer_bloques(logger, bits_a_enviar, tamanio, posicion_actual, fcb, info_bloques , config_files);

	log_debug(logger, "Dato leido:%s" , bits_a_enviar );

	enviar_lectura_archivo(socket_memoria, dato_a_enviar);//memeria ver con los otros 2 se es asi

	free(bits_a_enviar);
}

//-----------------------Leer_Bloques------------------------------------------------------------------------

void leer_bloques (t_log* logger, char* bits_a_enviar, int necesito, int posicion_actual, t_FCB* fcb, t_info_bloques info_bloques, t_config_fileSystem config_files){
	
	char* archivo_de_bloque = info_bloques.archivo_bloque;
	int disponible;
	int tamCopiado;
	uint32_t bloqueActualDelFCB = posicion_actual / info_bloques.tamBloques;
	int posActualBloque = posicion_actual % info_bloques.tamBloques;

	char* aux = bits_a_enviar;

	int tamFcbDisponible = fcb->tamanio_archivo - posicion_actual ;

	while (necesito > 0 && tamFcbDisponible > 0 )
	{
		

		disponible = info_bloques.tamBloques - posActualBloque;
		tamCopiado = (necesito<disponible)?necesito:disponible;
		tamCopiado = (tamFcbDisponible<tamCopiado)?tamFcbDisponible:tamCopiado;


		char* bloqueVerdadero = buscarBloqueEnArchivo(logger, archivo_de_bloque, fcb, bloqueActualDelFCB, info_bloques, config_files);//Nos devuelve la posición del bloque del archivo de bloques
		memcpy(aux, bloqueVerdadero+posActualBloque, tamCopiado);

		bloqueActualDelFCB++;
		posActualBloque=0;

		aux += tamCopiado; //Actualiza el puntero de lo restante a copiar
		necesito -= disponible;
		tamFcbDisponible -= tamCopiado;

	}
	
}
//-----------------------F_WRITE------------------------------------------------------------------------

void escribir_archivo(t_log* logger, int socket_memoria, char* instruccion, t_config_fileSystem config, t_info_bloques info_bloques, t_dictionary* FCBs_Dic, t_config_fileSystem config_files)
{

	char** instruccionSeparada = string_split(instruccion,"|"); //Tenemos char* [nombre_archivo, dirección_memoria, bytes, puntero_posicion_archivo]]

	char* nombre = instruccionSeparada[0];
	int tamanio = atoi(instruccionSeparada[2]);
	int posicion_actual = atoi(instruccionSeparada[3]);
	int disponible;
	
	uint32_t bloqueActualDelFCB = posicion_actual / info_bloques.tamBloques;
	int posActualBloque = posicion_actual % info_bloques.tamBloques;

	log_info(logger, "Escribir Archivo: %s - Puntero: %d - Memoria: %s - Tamaño: %d", nombre, posicion_actual, instruccionSeparada[1], tamanio);

	//pedir a memoria
	
	int direccion =atoi(instruccionSeparada[1]);

	enviar_Par_de_Ints(socket_memoria, direccion , tamanio , F_WRITE);
	char* datos_a_escribir = recibir_string_tradicional(socket_memoria, tamanio); // =recibir_paquete_memoria();
	char* aux_datos_a_escribir = datos_a_escribir;
	
	char * archivo_de_bloques = info_bloques.archivo_bloque;

	t_FCB* fcb_aux = dictionary_get(FCBs_Dic, nombre);
	
	char* bloqueVerdadero = buscarBloqueEnArchivo(logger, archivo_de_bloques, fcb_aux, bloqueActualDelFCB, info_bloques, config_files);
	char* punteroAescribir = bloqueVerdadero + posActualBloque;
	
	
	int tamFcbDisponible = fcb_aux->tamanio_archivo - posicion_actual ;

	while(tamanio > 0 && tamFcbDisponible > 0){
		
		
		disponible = info_bloques.tamBloques - posActualBloque;
		
		disponible = (disponible<tamanio) ? disponible : tamanio;
		disponible = (tamFcbDisponible<disponible) ? tamFcbDisponible : disponible;
		
		memcpy(punteroAescribir, aux_datos_a_escribir, disponible);

		msync(archivo_de_bloques, sizeof(char), MS_SYNC); 

		aux_datos_a_escribir += disponible;

		posActualBloque = 0;

		tamanio -= disponible;
		tamFcbDisponible -= disponible;
		bloqueActualDelFCB++;
		
		if(tamanio > 0 && tamFcbDisponible > 0)
		punteroAescribir = buscarBloqueEnArchivo(logger, archivo_de_bloques, fcb_aux, bloqueActualDelFCB, info_bloques, config_files);
	}
	free(datos_a_escribir);
}

//-----------------------RECIBIR PAQUETE------------------------------------------------------------------------

void recibir_paquete_kernel(int socket_kernel, int socket_memoria, t_log* logger, t_config_fileSystem config_files, t_info_bloques info_bloques, t_dictionary* FCBs_Dic){
	t_paquete* paquete = malloc(sizeof(t_paquete));
	paquete->buffer = malloc(sizeof(t_buffer));

	// Primero recibimos el codigo de operacion
	recv(socket_kernel, &(paquete->codigo_de_op), sizeof(int), 0);

	// Después ya podemos recibir el buffer. Primero su tamaño seguido del contenido
	recv(socket_kernel, &(paquete->buffer->size), sizeof(int), 0);
	paquete->buffer->stream = malloc(paquete->buffer->size);
	recv(socket_kernel, paquete->buffer->stream, paquete->buffer->size, 0);

	char* data = deserializar_string(paquete->buffer);

	realizar_instruccion(socket_kernel, socket_memoria, paquete->codigo_de_op, data, logger, config_files, info_bloques, FCBs_Dic);
	free(data);
}


//-----------------------SWITCH REALIZAR INSTRUCCION------------------------------------------------------------------------

void realizar_instruccion(int socket_kernel, int socket_memoria, int instruccion, char* datos, t_log* logger, t_config_fileSystem config_files, t_info_bloques info_bloques, t_dictionary* FCBs_Dic){
    switch (instruccion)
    {
    case F_OPEN:/* NOMBRE*/

        open_file(logger, config_files.path_fcb, datos, FCBs_Dic, info_bloques.BitMapBloque);
        break;


	case F_TRUNCATE:/* NOMBRE , TAMANIO*/
		truncate_file(logger, datos, config_files.path_fcb, FCBs_Dic, info_bloques, config_files);
		break;


	case F_READ:
		leer_archivo(logger, socket_memoria, datos, config_files, info_bloques, FCBs_Dic, config_files);
		break;


	case F_WRITE:
		escribir_archivo(logger, socket_memoria, datos, config_files, info_bloques, FCBs_Dic, config_files);
		break;


    default:
        break;
    }
	enviar_bool(socket_kernel, true);
}

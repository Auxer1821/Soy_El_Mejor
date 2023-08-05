#include "../include/estructuras_compartidas.h"

void liberar_lista_strings(t_list* lista){
	list_destroy_and_destroy_elements(lista,free);
}

int enviar_bool(int conexion, bool booleano){
	int ret = send(conexion, &booleano, sizeof(bool), 0);
	return ret;
}

bool recibir_bool(int conexion){
	bool ret;
	recv(conexion, &ret, sizeof(bool), 0);
	return ret;
}

int enviar_int_tradicional(int conexion, int entero){
	int ret = send(conexion, &entero, sizeof(int), 0);
	return ret;
}

int recibir_int(int conexion){
	int ret;
	recv(conexion, &ret, sizeof(int), 0);
	return ret;
}

char* recibir_string_tradicional(int socket_memoria, int tam){
	char* ret = malloc(tam);
	recv(socket_memoria, ret, tam, 0);
	return ret;
}
int enviar_string_tradicional(int conexion, char* string){
	int tam = strlen(string);
	int ret = send(conexion, string, tam, 0);
	return ret;
}


int enviar_string(int conexion, char* string, codigo_de_op codigo_op){
    t_paquete* paquete = malloc(sizeof(t_paquete));
	void* string_a_enviar = serializar_string(string, paquete, codigo_op);
	int ret = send(conexion, string_a_enviar, (paquete->buffer->size) + sizeof(int) + sizeof(int), 0);
	free(string_a_enviar);
	eliminar_paquete(paquete);
	return ret;
}

void* serializar_string(char* string, t_paquete* paquete, codigo_de_op codigo_op){
    t_buffer* buffer = malloc(sizeof(t_buffer));
	int tamanio_string = strlen(string)+ 1; // La longitud del string nombre. Le sumamos 1 para enviar tambien el caracter centinela '\0'.

	// Primero completo la estructura buffer interna del paquete.
	buffer->size = tamanio_string;
	void* stream = malloc(buffer->size);
	int offset = 0; // Desplazamiento

	memcpy(stream + offset, string, tamanio_string);
	offset += tamanio_string;

	buffer->stream = stream;

	// Segundo: completo el paquete.
	paquete->codigo_de_op = codigo_op;
	paquete->buffer = buffer;

	void* a_enviar = malloc(buffer->size + sizeof(int) + sizeof(int));
	offset = 0;

	memcpy(a_enviar, &(paquete->codigo_de_op), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, &(paquete->buffer->size), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, paquete->buffer->stream, paquete->buffer->size);
	offset += paquete->buffer->size;

	free(string);
	return a_enviar;
}

int enviar_contexto(int conexion, t_contexto* contexto){
	t_paquete* paquete = malloc(sizeof(t_paquete));
	void* contexto_a_enviar = serializar_contexto(contexto, paquete);
	int ret = send(conexion, contexto_a_enviar, (paquete->buffer->size) + sizeof(int) + sizeof(int), 0);
	free(contexto_a_enviar);
	eliminar_paquete(paquete);
	return ret;
}

void eliminar_paquete(t_paquete* paquete){
	free(paquete->buffer->stream);
	free(paquete->buffer);
	free(paquete);
}

int calcular_tam_lista_regs(t_list* lista){

	void* sumaPesoString(void* av, void* bv){
		int* a = (int*)av;
		char* b=(char*)bv;
		*a = (*a) + sizeof(int) + strlen(b) + 1;
		return a;
	};

	int a = sizeof(int); 
	int* tam_total = list_fold(lista, &a , sumaPesoString);
	return *tam_total;
}

void* serializar_contexto(t_contexto* contexto, t_paquete* paquete){
	t_buffer* buffer = malloc(sizeof(t_buffer));

	int tamanio_TS = list_size(contexto->tabla_segmentos)*(sizeof(int)*3);

	int tam_instrucciones = strlen(contexto->instrucciones);

	int estado = transformar_enum(contexto->estado_actual);

	buffer->size = 	sizeof(int)/*pid*/+
					sizeof(int)/*pc*/+
					sizeof(int)/*estado*/+
					sizeof(int)/*tiempo_bloqueo*/+
					sizeof(int)/*razon exit*/+
					112/*registros*/+
					sizeof(int)/*cant_segmentos*/+
					tamanio_TS/*peso_ts*/+
					sizeof(int)/*tamanio_intrucciones*/+
					tam_instrucciones/*peso_intrucciones*/;
					

	void* stream = malloc(buffer->size);
	int offset = 0;



//-----------------------------------------------------------------	SERIALIZACION DEL P.I.D.
	memcpy(stream + offset, &(contexto->pid), sizeof(int));
	offset += sizeof(int);

//-----------------------------------------------------------------	SERIALIZACION DEL P.C.
	memcpy(stream + offset, &(contexto->program_counter), sizeof(int));
	offset += sizeof(int);

//-----------------------------------------------------------------	SERIALIZACION DEL ESTADO
	memcpy(stream + offset, &estado, sizeof(int));
	offset += sizeof(int);

//-----------------------------------------------------------------	SERIALIZACION DEL TIEMPO DE BLOQUEO
	memcpy(stream + offset, &(contexto->tiempo_de_bloqueo), sizeof(int));
	offset += sizeof(int);

//-----------------------------------------------------------------	SERIALIZACION DEL RAZON FINALIZADO
	memcpy(stream + offset, &(contexto->razon_finalizado), sizeof(int));
	offset += sizeof(int);

//-----------------------------------------------------------------	SERIALIZACION DE REGISTROS
	memcpy(stream + offset, contexto->registros_cpu.ax, 4);
	offset += 4;
	memcpy(stream + offset, contexto->registros_cpu.bx, 4);
	offset += 4;
	memcpy(stream + offset, contexto->registros_cpu.cx, 4);
	offset += 4;
	memcpy(stream + offset, contexto->registros_cpu.dx, 4);
	offset += 4;

	memcpy(stream + offset, contexto->registros_cpu.eax, 8);
	offset += 8;
	memcpy(stream + offset, contexto->registros_cpu.ebx, 8);
	offset += 8;
	memcpy(stream + offset, contexto->registros_cpu.ecx, 8);
	offset += 8;
	memcpy(stream + offset, contexto->registros_cpu.edx, 8);
	offset += 8;

	memcpy(stream + offset, contexto->registros_cpu.rax, 16);
	offset += 16;
	memcpy(stream + offset, contexto->registros_cpu.rbx, 16);
	offset += 16;
	memcpy(stream + offset, contexto->registros_cpu.rcx, 16);
	offset += 16;
	memcpy(stream + offset, contexto->registros_cpu.rdx, 16);
	offset += 16;

//----------------------------------------------------------------- SERIALIZACION DE LA TABLA DE SEGMENTOS
	int tam_list = list_size(contexto->tabla_segmentos);
	memcpy(stream + offset, &tam_list, sizeof(int));
	offset += sizeof(int);
	t_segmento* segmento;
	for(int i = 0; i<tam_list ; i++){
		segmento = list_get(contexto->tabla_segmentos,i);
		memcpy(stream + offset, &(segmento->base) , sizeof(int));
		offset += sizeof(int);
		memcpy(stream + offset, &(segmento->tamanio) , sizeof(int));
		offset += sizeof(int);
		memcpy(stream + offset, &(segmento->ID) , sizeof(int));
		offset += sizeof(int);
	}
//-----------------------------------------------------------------	SERIALIZACION DE LAS INSTRUCCIONES
	memcpy(stream + offset, &tam_instrucciones, sizeof(int));
	offset += sizeof(int);
	memcpy(stream + offset, contexto->instrucciones, tam_instrucciones);
	offset += tam_instrucciones;

	buffer->stream = stream;

	paquete->codigo_de_op = CONTEXTO;
	paquete->buffer = buffer;

	void* a_enviar = malloc(buffer->size + sizeof(int) + sizeof(int));
	offset = 0;

	memcpy(a_enviar, &(paquete->codigo_de_op), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, &(paquete->buffer->size), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, paquete->buffer->stream, paquete->buffer->size);
	offset += paquete->buffer->size;

	return a_enviar;
}

int transformar_enum(t_estado estado){
	switch(estado){
		case NEW:
			return 0;
		case READY:
			return 1;
		case BLOCK:
			return 2;
		case ENDED:
			return 4;
		default:
			return -1;
	}
}

t_estado detransformar_enum(int num){
	switch(num){
		case 0:
			return NEW;
		case 1:
			return READY;
		case 2:
			return BLOCK;
		case 4:
			return ENDED;
		default:
			exit(55);
	}
}

void* recibir_paquete(int socket){
	t_paquete* paquete = malloc(sizeof(t_paquete));
	paquete->buffer = malloc(sizeof(t_buffer));

	// Primero recibimos el codigo de operacion
	recv(socket, &(paquete->codigo_de_op), sizeof(int), 0);

	// Después ya podemos recibir el buffer. Primero su tamaño seguido del contenido
	recv(socket, &(paquete->buffer->size), sizeof(int), 0);
	paquete->buffer->stream = malloc(paquete->buffer->size);
	recv(socket, paquete->buffer->stream, paquete->buffer->size, 0);


	void* data;

	switch (paquete->codigo_de_op)
	{
	case CONSOLA:
		data = deserializar_string(paquete -> buffer);
		break;
	case CONTEXTO:
		data = deserializar_contexto(paquete -> buffer);
		break;
	case RESP_REC:
		break;
	case MOV_IN:  //Analizar si memoria utiliza este o otro recibir
		data = deserializar_string(paquete -> buffer);
		break;
	case MOV_OUT: //Analizar si memoria utiliza este o otro recibir. Si usa este, hay que completarlo para que separe el string
		data = deserializar_string(paquete -> buffer);
	case PET_CRE_SEG:	//tanto el envio de la creacion del segmento como las respuesta las encapsule en la misma poroque hacen lo mismo
		//data = deserializar_peticion(paquete->buffer);
		//@nico y @maxi
		break;
	default:
		break;
	}


	//free(instrucciones_como_string);
	free(paquete->buffer->stream);
	free(paquete->buffer);
	free(paquete);
	return data;
}

char* deserializar_string(t_buffer* buffer){
	char* string = malloc(buffer->size);
	memset(string, 0, buffer->size);

	void* stream = buffer->stream;
	// Deserializamos los campos que tenemos en el buffer
	memcpy(string, stream, buffer->size);

	return string;
}


t_contexto* deserializar_contexto(t_buffer* buffer){
	t_contexto* contexto = malloc(sizeof(t_contexto));

	void* stream = buffer->stream;

	int enum_como_int;


//------------------------------------------------------------------------------------------------------ DESERIALIZACION DE P.I.D.
	memcpy(&(contexto->pid), stream, sizeof(int));
	stream += sizeof(int);

//------------------------------------------------------------------------------------------------------ DESERIALIZACION DE P.C.
	memcpy(&(contexto->program_counter), stream, sizeof(int));
	stream += sizeof(int);

//------------------------------------------------------------------------------------------------------ DESERIALIZACION DE ESTADO
	memcpy(&enum_como_int, stream, sizeof(int));
	stream += sizeof(int);
	t_estado estado = detransformar_enum(enum_como_int);
	contexto->estado_actual = estado;

//------------------------------------------------------------------------------------------------------ DESERIALIZACION DE TIEMPO DE BLOQUEO
	memcpy(&(contexto->tiempo_de_bloqueo), stream, sizeof(int));
	stream += sizeof(int);

//------------------------------------------------------------------------------------------------------ DESERIALIZACION DE RAZON FINALIZADO
	memcpy(&(contexto->razon_finalizado), stream, sizeof(int));
	stream += sizeof(int);

//------------------------------------------------------------------------------------------------------ DESERIALIZACION DE REGISTROS
	contexto->registros_cpu.ax = malloc(4);
	contexto->registros_cpu.bx = malloc(4);
	contexto->registros_cpu.cx = malloc(4);
	contexto->registros_cpu.dx = malloc(4);

	memset(contexto->registros_cpu.ax, 0, 4);
	memset(contexto->registros_cpu.bx, 0, 4);
	memset(contexto->registros_cpu.cx, 0, 4);
	memset(contexto->registros_cpu.dx, 0, 4);

	memcpy(contexto->registros_cpu.ax, stream, 4);
	stream += 4;
	memcpy(contexto->registros_cpu.bx, stream, 4);
	stream += 4;
	memcpy(contexto->registros_cpu.cx, stream, 4);
	stream += 4;
	memcpy(contexto->registros_cpu.dx, stream, 4);
	stream += 4;


	contexto->registros_cpu.eax = malloc(8);
	contexto->registros_cpu.ebx = malloc(8);
	contexto->registros_cpu.ecx = malloc(8);
	contexto->registros_cpu.edx = malloc(8);

	memset(contexto->registros_cpu.eax, 0, 8);
	memset(contexto->registros_cpu.ebx, 0, 8);
	memset(contexto->registros_cpu.ecx, 0, 8);
	memset(contexto->registros_cpu.edx, 0, 8);

	memcpy(contexto->registros_cpu.eax, stream, 8);
	stream += 8;
	memcpy(contexto->registros_cpu.ebx, stream, 8);
	stream += 8;
	memcpy(contexto->registros_cpu.ecx, stream, 8);
	stream += 8;
	memcpy(contexto->registros_cpu.edx, stream, 8);
	stream += 8;


	contexto->registros_cpu.rax = malloc(16);
	contexto->registros_cpu.rbx = malloc(16);
	contexto->registros_cpu.rcx = malloc(16);
	contexto->registros_cpu.rdx = malloc(16);

	memset(contexto->registros_cpu.rax, 0, 16);
	memset(contexto->registros_cpu.rbx, 0, 16);
	memset(contexto->registros_cpu.rcx, 0, 16);
	memset(contexto->registros_cpu.rdx, 0, 16);

	memcpy(contexto->registros_cpu.rax, stream, 16);
	stream += 16;
	memcpy(contexto->registros_cpu.rbx, stream, 16);
	stream += 16;
	memcpy(contexto->registros_cpu.rcx, stream, 16);
	stream += 16;
	memcpy(contexto->registros_cpu.rdx, stream, 16);
	stream += 16;

//------------------------------------------------------------------------------------------------------ DESERIALIZACION DE TABLA DE SEGMENTOS
	int tam_lista = 0;
	contexto->tabla_segmentos = list_create();
	memcpy(&tam_lista, stream, sizeof(int));
	stream += sizeof(int);
	for(int i = 0; i < tam_lista; i++){
		t_segmento* segmento = malloc(sizeof(t_segmento));
		memcpy(&(segmento->base), stream, sizeof(int));
		stream += sizeof(int);
		memcpy(&(segmento->tamanio), stream, sizeof(int));
		stream += sizeof(int);
		memcpy(&(segmento->ID), stream, sizeof(int));
		stream += sizeof(int);

		list_add(contexto->tabla_segmentos,segmento);
	}

//------------------------------------------------------------------------------------------------------ DESERIALIZACION DE INSTRUCCIONES
	int tam_instrucciones;
	memcpy(&tam_instrucciones, stream, sizeof(int));
	stream += sizeof(int);
	contexto->instrucciones = malloc(tam_instrucciones);
	memset(contexto->instrucciones, 0, tam_instrucciones);
	memcpy(contexto->instrucciones, stream, tam_instrucciones);

	return contexto;
}

void liberar_contexto(t_contexto* contexto){
	free(contexto->instrucciones);

	free(contexto->registros_cpu.ax);
	free(contexto->registros_cpu.bx);
	free(contexto->registros_cpu.cx);
	free(contexto->registros_cpu.dx);

	free(contexto->registros_cpu.eax);
	free(contexto->registros_cpu.ebx);
	free(contexto->registros_cpu.ecx);
	free(contexto->registros_cpu.edx);

	free(contexto->registros_cpu.rax);
	free(contexto->registros_cpu.rbx);
	free(contexto->registros_cpu.rcx);
	free(contexto->registros_cpu.rdx);
	
	list_destroy_and_destroy_elements(contexto->tabla_segmentos,free);
	free(contexto);
}

int enviar_int(int conexion, int* tamanio, codigo_de_op codigo_de_op){
    t_paquete* paquete = malloc(sizeof(t_paquete));
	void* peticion_a_enviar = serializar_int(tamanio, paquete, codigo_de_op);
	int ret = send(conexion, peticion_a_enviar, (paquete->buffer->size) + sizeof(int) + sizeof(int), 0);
	free(peticion_a_enviar);
	eliminar_paquete(paquete);
	return ret;
}

void* serializar_int(int* tamanio, t_paquete* paquete, codigo_de_op codigo_de_op){

	t_buffer* buffer = malloc(sizeof(t_buffer));

	// Primero completo la estructura buffer interna del paquete.
	buffer->size = sizeof(int);
	void* stream = malloc(buffer->size);
	int offset = 0; // Desplazamiento

	memcpy(stream + offset, tamanio, sizeof(int));
	offset += sizeof(int);

	buffer->stream = stream;

	// Segundo: completo el paquete.
	paquete->codigo_de_op = codigo_de_op;
	paquete->buffer = buffer;

	void* a_enviar = malloc(buffer->size + sizeof(int) + sizeof(int));
	offset = 0;

	memcpy(a_enviar, &(paquete->codigo_de_op), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, &(paquete->buffer->size), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, paquete->buffer->stream, paquete->buffer->size);
	offset += paquete->buffer->size;

	return a_enviar;
}

int* deserializar_int(t_buffer* buffer){
	int* numero = malloc(buffer->size);
	void* stream = buffer->stream;
	// Deserializamos los campos que tenemos en el buffer
	memcpy(numero, stream, buffer->size);

	return numero;
}

int enviar_tabla_de_segmentos(int conexion, t_list* tabla){
	t_paquete* paquete = malloc(sizeof(t_paquete));
	void* tabla_a_enviar = serializar_tabla_de_segmentos(tabla, paquete);
	int ret = send(conexion, tabla_a_enviar, (paquete->buffer->size) + sizeof(int) + sizeof(int), 0);
	free(tabla_a_enviar);
	eliminar_paquete(paquete);
	return ret;
}

void* serializar_tabla_de_segmentos(t_list* tabla, t_paquete* paquete){
	t_buffer* buffer = malloc(sizeof(t_buffer));

	int tam_tabla = list_size(tabla);
	int tam_segmento = sizeof(int)*3;

	// Primero completo la estructura buffer interna del paquete.
	buffer->size = tam_tabla*tam_segmento + sizeof(int);
	void* stream = malloc(buffer->size);
	int offset = 0; // Desplazamiento

	int tam_list = list_size(tabla);
	memcpy(stream + offset, &tam_list, sizeof(int));
	offset += sizeof(int);
	for(int i = 0; i<tam_list ; i++){
		t_segmento* segmento = list_get(tabla,i);
		memcpy(stream + offset, &(segmento->base) , sizeof(int));
		offset += sizeof(int);
		memcpy(stream + offset, &(segmento->tamanio) , sizeof(int));
		offset += sizeof(int);
		memcpy(stream + offset, &(segmento->ID) , sizeof(int));
		offset += sizeof(int);
	}

	buffer->stream = stream;

	// Segundo: completo el paquete.
	paquete->codigo_de_op = TS;
	paquete->buffer = buffer;

	void* a_enviar = malloc(buffer->size + sizeof(int) + sizeof(int));
	offset = 0;

	memcpy(a_enviar, &(paquete->codigo_de_op), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, &(paquete->buffer->size), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, paquete->buffer->stream, paquete->buffer->size);
	offset += paquete->buffer->size;

	return a_enviar;
}

t_list* deserializar_tabla_de_segmentos(t_buffer* buffer){
	t_list* tabla;

	void* stream = buffer->stream;

	int tam_lista = 0;
	tabla = list_create();
	memcpy(&tam_lista, stream, sizeof(int));
	stream += sizeof(int);
	t_segmento* segmento;
	for(int i = 0; i < tam_lista; i++){
		segmento = malloc(sizeof(t_segmento));
		memcpy(&(segmento->base), stream, sizeof(int));
		stream += sizeof(int);
		memcpy(&(segmento->tamanio), stream, sizeof(int));
		stream += sizeof(int);
		memcpy(&(segmento->ID), stream, sizeof(int));
		stream += sizeof(int);

		list_add(tabla, segmento);
	}
	
	return tabla;
}

int enviar_Par_de_Ints(int conexion, int data1, int data2, codigo_de_op codigo_de_op){
	t_Par_de_Ints* peticion = malloc(sizeof(t_Par_de_Ints));
	peticion->data1 = data1;
	peticion->data2 = data2;
	t_paquete* paquete = malloc(sizeof(t_paquete));
	void* peticion_a_enviar = serializar_Par_de_Ints(peticion, paquete, codigo_de_op);
	int ret = send(conexion, peticion_a_enviar, (paquete->buffer->size) + sizeof(int) + sizeof(int), 0);
	free(peticion_a_enviar);
	eliminar_paquete(paquete);
	free(peticion);
	return ret;
}

void* serializar_Par_de_Ints(t_Par_de_Ints* peticion, t_paquete* paquete, codigo_de_op codigo_de_op){
	t_buffer* buffer = malloc(sizeof(t_buffer));

	// Primero completo la estructura buffer interna del paquete.
	buffer->size = sizeof(int)*2;
	void* stream = malloc(buffer->size);
	int offset = 0; // Desplazamiento

	memcpy(stream + offset, &(peticion->data1), sizeof(int));
	offset += sizeof(int);
	memcpy(stream + offset, &(peticion->data2), sizeof(int));
	offset += sizeof(int);

	buffer->stream = stream;

	// Segundo: completo el paquete.
	paquete->codigo_de_op = codigo_de_op;
	paquete->buffer = buffer;

	void* a_enviar = malloc(buffer->size + sizeof(int) + sizeof(int));
	offset = 0;

	memcpy(a_enviar, &(paquete->codigo_de_op), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, &(paquete->buffer->size), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, paquete->buffer->stream, paquete->buffer->size);
	offset += paquete->buffer->size;

	return a_enviar;
}

t_Par_de_Ints* deserializar_Par_de_Ints(t_buffer* buffer){

	t_Par_de_Ints* peticion = malloc(sizeof(t_Par_de_Ints));

	void* stream = buffer->stream;

	memcpy(&(peticion->data1), stream, sizeof(int));
	stream += sizeof(int);
	memcpy(&(peticion->data2), stream, sizeof(int));

	return peticion;
}

int enviar_registro(int conexion, t_registro* registro){

	t_paquete* paquete = malloc(sizeof(t_paquete));
	void* registro_a_enviar = serializar_registro(registro, paquete);
	int ret = send(conexion, registro_a_enviar, (paquete->buffer->size) + sizeof(int) + sizeof(int), 0);
	free(registro_a_enviar);
	eliminar_paquete(paquete);
	return ret;
}

void* serializar_registro(t_registro* registro, t_paquete* paquete){

	t_buffer* buffer = malloc(sizeof(t_buffer));

	// Primero completo la estructura buffer interna del paquete.
	buffer->size = sizeof(int) + registro->longitud;
	void* stream = malloc(buffer->size);
	int offset = 0; // Desplazamiento

	memcpy(stream + offset, &(registro->longitud), sizeof(int));
	offset += sizeof(int);
	memcpy(stream + offset, registro->valor, registro->longitud);
	offset += sizeof(int);

	buffer->stream = stream;

	// Segundo: completo el paquete.
	paquete->codigo_de_op = REGISTRO;
	paquete->buffer = buffer;

	void* a_enviar = malloc(buffer->size + sizeof(int) + sizeof(int));
	offset = 0;

	memcpy(a_enviar, &(paquete->codigo_de_op), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, &(paquete->buffer->size), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, paquete->buffer->stream, paquete->buffer->size);
	offset += paquete->buffer->size;

	return a_enviar;
}


int enviar_dato_con_DF(int conexion, int longitud, char* valor, int* DF, codigo_de_op codigo_de_op){

	t_paquete* paquete = malloc(sizeof(t_paquete));
	void* dato_a_enviar = serializar_dato_con_DF(longitud, valor, DF, paquete, codigo_de_op);
	int ret = send(conexion, dato_a_enviar, (paquete->buffer->size) + sizeof(int) + sizeof(int), 0);
	free(dato_a_enviar);
	eliminar_paquete(paquete);
	return ret;
}

void* serializar_dato_con_DF(int longitud, char* valor, int* DF, t_paquete* paquete, codigo_de_op codigo_de_op){

	t_buffer* buffer = malloc(sizeof(t_buffer));

	// Primero completo la estructura buffer interna del paquete.
	buffer->size = sizeof(int)*2 + longitud;
	void* stream = malloc(buffer->size);
	int offset = 0; // Desplazamiento

	memcpy(stream + offset, DF, sizeof(int));
	offset += sizeof(int);
	memcpy(stream + offset, &longitud, sizeof(int));
	offset += sizeof(int);
	//@nico y @maxi
	//memcpy(stream + offset, valor, registro->longitud);
	//offset += sizeof(int);
	memcpy(stream + offset, valor,longitud);
	offset += longitud;

	buffer->stream = stream;

	// Segundo: completo el paquete.
	paquete->codigo_de_op = codigo_de_op;
	paquete->buffer = buffer;

	void* a_enviar = malloc(buffer->size + sizeof(int) + sizeof(int));
	offset = 0;

	memcpy(a_enviar, &(paquete->codigo_de_op), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, &(paquete->buffer->size), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, paquete->buffer->stream, paquete->buffer->size);
	offset += paquete->buffer->size;

	return a_enviar;
}

t_dato_DF* deserializar_dato_DF(t_buffer* buffer){
	t_dato_DF* dato_con_DF = malloc(sizeof(t_dato_DF));

	void* stream = buffer->stream;

	memcpy(&(dato_con_DF->df), stream, sizeof(int));
	stream += sizeof(int);
	memcpy(&(dato_con_DF->longitud), stream, sizeof(int));
	stream += sizeof(int);
	dato_con_DF->valor = malloc(dato_con_DF->longitud);
	memcpy(dato_con_DF->valor, stream, dato_con_DF->longitud);

	return dato_con_DF;
}

int enviar_Pet_Segmento(int conexion, int PID, int ID, int tamanio){
	t_Pet_Segmento* peticion = malloc(sizeof(t_Pet_Segmento));
	peticion->PID = PID;
	peticion->ID = ID;
	peticion->tamanio = tamanio;
	t_paquete* paquete = malloc(sizeof(t_paquete));
	void* peticion_a_enviar = serializar_pet_creacion_segmento(peticion, paquete);
	int ret = send(conexion, peticion_a_enviar, (paquete->buffer->size) + sizeof(int) + sizeof(int), 0);
	free(peticion_a_enviar);
	eliminar_paquete(paquete);
	free(peticion);
	return ret;
}

void* serializar_pet_creacion_segmento(t_Pet_Segmento* peticion, t_paquete* paquete){
	t_buffer* buffer = malloc(sizeof(t_buffer));

	// Primero completo la estructura buffer interna del paquete.
	buffer->size = sizeof(int)*3;
	void* stream = malloc(buffer->size);
	int offset = 0; // Desplazamiento

	memcpy(stream + offset, &(peticion->PID), sizeof(int));
	offset += sizeof(int);
	memcpy(stream + offset, &(peticion->ID), sizeof(int));
	offset += sizeof(int);
	memcpy(stream + offset, &(peticion->tamanio), sizeof(int));
	offset += sizeof(int);

	buffer->stream = stream;

	// Segundo: completo el paquete.
	paquete->codigo_de_op = CREATE_SEGMENT;
	paquete->buffer = buffer;

	void* a_enviar = malloc(buffer->size + sizeof(int) + sizeof(int));
	offset = 0;

	memcpy(a_enviar, &(paquete->codigo_de_op), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, &(paquete->buffer->size), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, paquete->buffer->stream, paquete->buffer->size);
	offset += paquete->buffer->size;

	return a_enviar;
}

void eliminar_pcb(t_PCB* pcb){ // Añadir eliminación del diccionario de la tabla de archivos
/*
	free(pcb->tabla_segmentos.direcciones_base); 
	free(pcb->tabla_de_archivos.archivo);
	free(pcb->tabla_de_archivos.direccion_de_memoria);
	free(pcb->instrucciones);
*/
	
	free(pcb->registros_cpu.ax);
	free(pcb->registros_cpu.bx);
	free(pcb->registros_cpu.cx);
	free(pcb->registros_cpu.dx);

	free(pcb->registros_cpu.eax);
	free(pcb->registros_cpu.ebx);
	free(pcb->registros_cpu.ecx);
	free(pcb->registros_cpu.edx);

	free(pcb->registros_cpu.rax);
	free(pcb->registros_cpu.rbx);
	free(pcb->registros_cpu.rcx);
	free(pcb->registros_cpu.rdx);

	//liberar_lista_strings(pcb->recursos_asignados); //problema en CPU 
	list_destroy_and_destroy_elements(pcb->tabla_segmentos,free);

	free(pcb->recurso);

	free(pcb);

}

void sleep_ms(int miliseg){
	sleep (miliseg*0.001);
}

//----------------FUNCIONES_MUTEX-----------------------------------------------------------------------------------------------//
pthread_mutex_t* inicializar_mutex (t_log* logger){

	pthread_mutex_t Aux_mutex_pcb_bloq;
	pthread_mutex_t* mutex_pcb_bloq = &Aux_mutex_pcb_bloq;
	
	if ( pthread_mutex_init(mutex_pcb_bloq, NULL) != 0){
		log_error(logger,"ERROR INICIAR EL MUTEX");
		exit(3);
	}
	
	return mutex_pcb_bloq;
}


void lock_mutex(pthread_mutex_t* mutex, t_log* logger){
	int ret = pthread_mutex_lock(mutex);
    if ( ret != 0){
            log_error(logger,"ERROR BLOQUEANDO EL MUTEX; ERROR: %d", ret);
            exit(111);
        } // BLOQUEA EL SEMAFORO
}

void unlock_mutex(pthread_mutex_t* mutex, t_log* logger){
    int ret = pthread_mutex_unlock(mutex);
    if ( ret != 0){
            log_error(logger,"ERROR DESBLOQUEANDO EL MUTEX; ERROR: %d", ret);
            exit(112);
        } // DESBLOQUEA EL SEMAFORO
}

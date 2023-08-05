#include "../include/segmentos.h"


bool ordenar_por_tamanio_SL(void* element1, void* element2){
    t_segmento_libre* segmento1 = (t_segmento_libre*) element1;
    t_segmento_libre* segmento2 = (t_segmento_libre*) element2;
    
    return segmento1->tamanio < segmento2->tamanio;
}
bool ordenar_por_bases(void* element1, void* element2){
    t_segmento* segmento1 = (t_segmento*)element1;
    t_segmento* segmento2 = (t_segmento*)element2;
    return segmento1->base < segmento2->base;
}
bool ordenar_por_bases_SL(void* element1, void* element2){
    t_segmento_libre* segmento1 = (t_segmento_libre*)element1;
    t_segmento_libre* segmento2 = (t_segmento_libre*)element2;
    return segmento1->base < segmento2->base;
}


void inicializar_tabla_de_segmentos(int conexion_kernel, int capacidad, int PID, t_memoria* memoria){

    //Esta funcion se tiene que utilizar cuando se crea el PCB en kernel

    t_tabla_de_segmentos_mem* tabla_de_segmentos = malloc(sizeof(t_tabla_de_segmentos_mem));
    tabla_de_segmentos->ID = PID;
    tabla_de_segmentos->segmentos = list_create();
    list_add(tabla_de_segmentos->segmentos, memoria->segmento_0);
    tabla_de_segmentos->capacidad = capacidad-1;
    list_add(memoria->lista_tabla_de_segmentos, tabla_de_segmentos);

    enviar_tabla_de_segmentos(conexion_kernel, tabla_de_segmentos->segmentos); //Fijarse en estructuras.c si esta bien hecho
    
}

void crear_segmento(t_log* logger, int conexion_kernel, int ID, int tamanio_segmento, int PID, t_memoria* memoria, char* algoritmo){

	int buffer = 0;

    bool comparar_IDs_de_tablas(void* element){
        t_tabla_de_segmentos_mem* tabla = (t_tabla_de_segmentos_mem*)element;
        return tabla->ID == PID;};

    t_tabla_de_segmentos_mem* tabla_segmentos = list_find(memoria->lista_tabla_de_segmentos, comparar_IDs_de_tablas);

    t_segmento_libre* segmento_libre = elegir_segmento(memoria->lista_segmentos_libres, algoritmo, tamanio_segmento);
    
    if(memoria->lista_segmentos_libres->tamanio_total_disponible >= tamanio_segmento && segmento_libre == NULL){
        
        log_info(logger, "Solicitud de Compactación");
        
        enviar_int(conexion_kernel, &buffer, PET_COMPACTAR);

        unlock_mutex(memoria->mutex,logger);

        recibir_int(conexion_kernel);

        lock_mutex(memoria->mutex,logger);
        
        log_debug(logger,"SELEEP %d", memoria->retardo_compactacion);
        sleep_ms(memoria->retardo_compactacion);
        compactar(logger,memoria, conexion_kernel);

        recibir_int(conexion_kernel);

        log_debug(logger, "Confirmacion de Kernel recibida");

        //Porque despues de compactar cualquier algoritmo va a elegir directamente el primer segmento libre
        segmento_libre = list_get(memoria->lista_segmentos_libres->segmentos_libre, 0);
    }

    if(segmento_libre != NULL){
        t_segmento* segmento = malloc(sizeof(t_segmento));
        segmento->ID = ID;
        segmento->base = segmento_libre->base;
        segmento->tamanio = tamanio_segmento;
        
        log_info(logger,"PID: %d - Crear Segmento: %d - Base: %d - TAMAÑO: %d" ,PID,  ID, segmento->base, tamanio_segmento);

        list_add_sorted(tabla_segmentos->segmentos, segmento, ordenar_por_bases);

        //Reasignar el segmento en la tabla inversa?¿
        if(segmento_libre->tamanio > tamanio_segmento){
            segmento_libre->base = segmento_libre->base + tamanio_segmento; //Fijarse mejor ddp si hace falta el +1 o no
            segmento_libre->tamanio = segmento_libre->tamanio - tamanio_segmento;
        }else{
            bool comparar_bases_segmentos(void* element){
                t_segmento_libre* segmento = (t_segmento_libre*)element;
                return segmento->base == segmento_libre->base;};
        	list_remove_and_destroy_by_condition(memoria->lista_segmentos_libres->segmentos_libre, comparar_bases_segmentos, free);
        }
        memoria->lista_segmentos_libres->tamanio_total_disponible -= tamanio_segmento;

        enviar_tabla_de_segmentos(conexion_kernel, tabla_segmentos->segmentos);
    }
    if(segmento_libre == NULL){
        enviar_int(conexion_kernel, &buffer, OUT_OF_MEMORY);
    }
}

t_segmento_libre* elegir_segmento(t_segmentos_libres* lista_segmentos_libres, char* algoritmo, int tamanio_segmento){
    
    t_segmento_libre* segmento_libre;

    bool segmento_es_mayor(void* element){
        t_segmento_libre* segmento = (t_segmento_libre*) element;
        return segmento->tamanio >= tamanio_segmento;};

    t_list* lista_segmentos_usables = list_filter(lista_segmentos_libres->segmentos_libre, segmento_es_mayor);
    
    if (list_is_empty(lista_segmentos_usables)){
        return NULL;
    }

    t_segmento_libre* segmento_usable;
    if (strcmp(algoritmo, "FIRST")==0){
        segmento_usable = list_get(lista_segmentos_usables, 0);
        }
    else if (strcmp(algoritmo, "WORST")==0){
        list_sort(lista_segmentos_usables, ordenar_por_tamanio_SL);
        segmento_usable = list_get(lista_segmentos_usables, list_size(lista_segmentos_usables)-1);
        }
    else if (strcmp(algoritmo, "BEST")==0){
        list_sort(lista_segmentos_usables, ordenar_por_tamanio_SL);
        segmento_usable = list_get(lista_segmentos_usables, 0);
        }
    /*
    bool comparar_bases_SL(void* element){
        t_segmento_libre* segmento = (t_segmento_libre*)element;
    return segmento_usable->base == segmento->base;};

    //Actualizo en la lista de segmentos libres el segmento que voy a usar
    int pos = list_find(lista_segmentos_libres->segmentos_libre, comparar_bases_SL);
    segmento_libre = list_get(lista_segmentos_libres->segmentos_libre, pos);
    */
    return segmento_usable;
}

void compactar(t_log* logger, t_memoria* memoria, int socket_kernel){

    list_clean_and_destroy_elements(memoria->lista_segmentos_libres->segmentos_libre, free);

    t_list* lista_segmentos = list_create();
    list_add(lista_segmentos, memoria->segmento_0);

    //Desempaqueto los segmentos a la lista de tablas de segmentos
    t_list* lista_tabla_segmentos = memoria->lista_tabla_de_segmentos;

    //Recorro la lista de tablas de segmentos de memoria
    for (int i = 0; i<list_size(lista_tabla_segmentos); i++){

        //Recorro cada tabla de segmento y agrego los segmeentos a lista_segmentos (lista de la función)
        t_tabla_de_segmentos_mem* tabla_segmentos_aux = list_get(lista_tabla_segmentos, i);
        for (int j = 1; j<list_size(tabla_segmentos_aux->segmentos); j++){
            list_add(lista_segmentos, list_get(tabla_segmentos_aux->segmentos, j));
        }
    }

    list_sort(lista_segmentos, ordenar_por_bases);

    //Recorro la lista de segmentos
    for (int k = 1; k<list_size(lista_segmentos); k++){

        t_segmento* segmento_actual = list_get(lista_segmentos, k);
        t_segmento* segmento_anterior = list_get(lista_segmentos, k-1);

        //Verificamos si dos elementos son contiguos
        int supuesta_base_actual = segmento_anterior->base+segmento_anterior->tamanio; //<-- Fijarse si el tamaño +1 esta bien
        if (segmento_actual->base > supuesta_base_actual){
            
            char* espacio_memoria = (char*) memoria->espacio_usuario;

            char* dato = malloc(segmento_actual->tamanio);
            


            memcpy(dato, espacio_memoria + segmento_actual->base, segmento_actual->tamanio);
            memcpy(espacio_memoria + segmento_anterior->base + segmento_anterior->tamanio, dato, segmento_actual->tamanio); //<-- Fijarse si el tamaño +1 esta bien

            free(dato);
            
            

            segmento_actual->base = supuesta_base_actual;
        }
    }

    

    t_segmento_libre* segmento_libre = malloc(sizeof(t_segmento_libre));

    t_segmento* ultimo_segmento = list_get(lista_segmentos, list_size(lista_segmentos)-1);
    segmento_libre->base = ultimo_segmento->base + ultimo_segmento->tamanio; //<-- Fijarse si el tamaño +1 esta bien
    segmento_libre->tamanio = memoria->lista_segmentos_libres->tamanio_total_disponible;

    list_add(memoria->lista_segmentos_libres->segmentos_libre, segmento_libre);
    list_destroy(lista_segmentos);
    //---------------------------------------------------------
    // para el log obligatorio
    log_info(logger, "Resultado Compactacion: ");
    for (size_t i = 0; i < list_size(memoria->lista_tabla_de_segmentos); i++)
    {
        t_tabla_de_segmentos_mem* tabla_actual = list_get(memoria->lista_tabla_de_segmentos, i);
        log_debug(logger,"PID: %d, Cant_Seg: %d", tabla_actual->ID, list_size(tabla_actual->segmentos));
        for(int j = 0; j < list_size(tabla_actual->segmentos); j ++){
            
            t_segmento* segmento = list_get(tabla_actual->segmentos, j);
            log_info(logger, "PID: %d - Segmento: %d - Base: %d - Tamaño %d", tabla_actual->ID, segmento->ID, segmento->base, segmento->tamanio);
        }
    }
    log_debug(logger,"Enviando tablas ...");
    //-----------------------------------------------------------
    enviar_tablas_compactadas(socket_kernel, memoria);

    log_debug(logger,"  tablas enviadas");

    

}

void unir_segmentos_y_agregar(t_list* lista_de_segmentos, int pos1, int pos2){
    t_segmento_libre* segmento1 = list_get(lista_de_segmentos, pos1);
    t_segmento_libre* segmento2 = list_remove(lista_de_segmentos, pos2);
    segmento1->tamanio += segmento2->tamanio;
    free(segmento2);
}

t_Par_de_Ints eliminar_segmento(int conexion_kernel, int PID, int ID, t_memoria* memoria){
    t_Par_de_Ints ret;

    bool comparar_IDs_de_tablas(void* element){
        t_tabla_de_segmentos_mem* tabla = (t_tabla_de_segmentos_mem*)element;
        return tabla->ID == PID;};

    t_tabla_de_segmentos_mem* tabla_de_segmentos = list_remove_by_condition(memoria->lista_tabla_de_segmentos, comparar_IDs_de_tablas);


    bool comparar_IDs_segmentos(void* element){
        t_segmento* segmento = (t_segmento*)element;
        return segmento->ID == ID;};

    t_segmento* segmento_eliminado = list_remove_by_condition(tabla_de_segmentos->segmentos, comparar_IDs_segmentos);

    ret.data1 = segmento_eliminado->base;
    ret.data2 = segmento_eliminado->tamanio;

    t_segmento_libre* nuevo_segmento_libre = malloc(sizeof(t_segmento_libre));
    nuevo_segmento_libre->base = segmento_eliminado->base;
    nuevo_segmento_libre->tamanio = segmento_eliminado->tamanio;

    memoria->lista_segmentos_libres->tamanio_total_disponible += segmento_eliminado->tamanio;


    int pos = list_add_sorted(memoria->lista_segmentos_libres->segmentos_libre, nuevo_segmento_libre, ordenar_por_bases_SL);
    
    t_segmento* segmento_anterior = list_get(memoria->lista_segmentos_libres->segmentos_libre, pos-1);
    t_segmento* segmento_actual = list_get(memoria->lista_segmentos_libres->segmentos_libre, pos);

    if(segmento_anterior->base + segmento_anterior->tamanio == segmento_actual->base){

        unir_segmentos_y_agregar(memoria->lista_segmentos_libres->segmentos_libre, pos-1, pos);
        pos--;
 
    }
    t_segmento* segmento_proximo;
    if (list_size(memoria->lista_segmentos_libres->segmentos_libre)-1 >= pos+1){
        segmento_actual = list_get(memoria->lista_segmentos_libres->segmentos_libre, pos);
        segmento_proximo = list_get(memoria->lista_segmentos_libres->segmentos_libre, pos+1);
        if(segmento_actual->base + segmento_actual->tamanio == segmento_proximo->base){

        unir_segmentos_y_agregar(memoria->lista_segmentos_libres->segmentos_libre, pos, pos+1);

        }
    }


    list_add(memoria->lista_tabla_de_segmentos, tabla_de_segmentos);


    enviar_tabla_de_segmentos(conexion_kernel, tabla_de_segmentos->segmentos);

    free(segmento_eliminado);
    
    return ret;
 
   
}
void eliminar_tabla_segmento(int PID, t_memoria* memoria){

    //Extraigo la tabla de segmento de la memoria correspondiente al proceso
    t_list* lista_tabla_de_segmentos = memoria->lista_tabla_de_segmentos;

    bool comparar_IDs_de_tablas(void* element){
        t_tabla_de_segmentos_mem* tabla = (t_tabla_de_segmentos_mem*)element;
        return tabla->ID == PID;};
    bool comparar_IDs_de_segmentos(void* element){
            t_segmento* tabla = (t_segmento*)element;
            return tabla->ID != 0;};

    t_tabla_de_segmentos_mem* tabla_de_segmento = list_remove_by_condition(lista_tabla_de_segmentos, comparar_IDs_de_tablas);

    void destruir_segmento_aux(void* element){
    	t_segmento* segmento = (t_segmento*) element;
    	destruir_segmento(segmento, memoria);
    };

    list_remove_and_destroy_all_by_condition(tabla_de_segmento->segmentos, comparar_IDs_de_segmentos, destruir_segmento_aux);
    list_destroy(tabla_de_segmento->segmentos);

    free(tabla_de_segmento);

}

void destruir_segmento( t_segmento* segmento_eliminado, t_memoria* memoria){


    t_segmento_libre* nuevo_segmento_libre = malloc(sizeof(t_segmento_libre));
    nuevo_segmento_libre->base = segmento_eliminado->base;
    nuevo_segmento_libre->tamanio = segmento_eliminado->tamanio;

    memoria->lista_segmentos_libres->tamanio_total_disponible += segmento_eliminado->tamanio;
 
    int pos = list_add_sorted(memoria->lista_segmentos_libres->segmentos_libre, nuevo_segmento_libre, ordenar_por_bases_SL);

    t_segmento* segmento_anterior = list_get(memoria->lista_segmentos_libres->segmentos_libre, pos-1);
    t_segmento* segmento_actual = list_get(memoria->lista_segmentos_libres->segmentos_libre, pos);

    if(segmento_anterior->base + segmento_anterior->tamanio == segmento_actual->base){
        unir_segmentos_y_agregar(memoria->lista_segmentos_libres->segmentos_libre, pos-1, pos);
        pos--;
        segmento_actual=segmento_anterior;
    }
    t_segmento* segmento_proximo;
    if (list_size(memoria->lista_segmentos_libres->segmentos_libre)-1 >= pos+1){
        segmento_actual = list_get(memoria->lista_segmentos_libres->segmentos_libre, pos);
        segmento_proximo = list_get(memoria->lista_segmentos_libres->segmentos_libre, pos+1);
        if(segmento_actual->base + segmento_actual->tamanio == segmento_proximo->base){

        unir_segmentos_y_agregar(memoria->lista_segmentos_libres->segmentos_libre, pos, pos+1);

        }
    }

    free(segmento_eliminado);
}

void enviar_tablas_compactadas(int socket_kernel, t_memoria* memoria){
    t_buffer* buffer = malloc(sizeof(t_buffer));


    int cant_procesos = list_size(memoria->lista_tabla_de_segmentos);

    
    int peso_listas_pcbs = calcular_peso_tablas(memoria->lista_tabla_de_segmentos, cant_procesos);
    
    buffer->size = sizeof(int)/*cant_proceso*/+
                    cant_procesos*sizeof(int)/*peso de pid*/+
                    peso_listas_pcbs;
    
    void* stream = malloc(buffer->size);
    int offset = 0;

  
    memcpy(stream + offset, &cant_procesos, sizeof(int));
	offset += sizeof(int);

    for(int i = 0; i < cant_procesos; i++){
        
        t_tabla_de_segmentos_mem* tabla_del_proceso = list_get(memoria->lista_tabla_de_segmentos,i);

        memcpy(stream + offset, &tabla_del_proceso->ID, sizeof(int));
	    offset += sizeof(int);

        int cant_segmentos = list_size(tabla_del_proceso->segmentos);
        memcpy(stream + offset, &cant_segmentos, sizeof(int));
	    offset += sizeof(int);
        

        for(int j = 0; j  < cant_segmentos; j++){
            t_segmento* segmento = list_get(tabla_del_proceso->segmentos,j);
            memcpy(stream + offset, &segmento->ID, sizeof(int));
	        offset += sizeof(int);
            memcpy(stream + offset, &segmento->base, sizeof(int));
	        offset += sizeof(int);
            memcpy(stream + offset, &segmento->tamanio, sizeof(int));
	        offset += sizeof(int);
        }
        
    }
   	buffer->stream = stream;

	// Segundo: completo el paquete.

	void* a_enviar = malloc(buffer->size + sizeof(int));
	offset = 0;
	memcpy(a_enviar + offset, &(buffer->size), sizeof(int));
	offset += sizeof(int);
	memcpy(a_enviar + offset, buffer->stream, buffer->size);
	offset += buffer->size;
    

	send(socket_kernel, a_enviar, (buffer->size) + sizeof(int), 0);


}

int calcular_peso_tablas(t_list* tablas_de_segmentos, int cant_tablas){

    int peso_total = 0;
    for(int i=0; i<cant_tablas; i++){
        t_tabla_de_segmentos_mem* tabla_del_proceso = list_get(tablas_de_segmentos,i);
        peso_total += sizeof(int) + calcular_peso_tabla_del_proceso(tabla_del_proceso->segmentos);

    }
    return peso_total;
}

int calcular_peso_tabla_del_proceso(t_list* tabla_segmentos){
        int cant_segmentos = list_size(tabla_segmentos);
        return cant_segmentos*(sizeof(int)*3);
}

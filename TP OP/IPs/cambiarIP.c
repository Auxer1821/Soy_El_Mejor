#include "include/cambiarIP.h"

void ActualizarCPU (t_config*, char*, char*, char*);
void ActualizarKernel(t_config*, char*, char*, char*, char*, char*, char*, char*);
void ActualizarFileSystem(t_config*, char*, char*, char*, char*);
void ActualizarMemoria(t_config*, char*);
void ActualizarConsola(t_config*, char*, char*);
void ActualizarSuperBloque(t_config*, char*, char*);
void BorrarTodo(t_config*, t_config*, t_config*, t_config*, t_config*, t_config*, t_config*);

//---------------------------FUNCIONES.h--------------------------------------------------------------------------------------------------------------------------
//---------------------------FUNCIONES.h--------------------------------------------------------------------------------------------------------------------------
//---------------------------FUNCIONES.h--------------------------------------------------------------------------------------------------------------------------

int main(int argc, char** argv){

    char *ruta_fs = argv[1];


    printf("Iniciando las configuraciones\n");
    //---------------------------CONFIGS--------------------------------------------------------------------------------------------------------------------------
	t_config* configCPU = iniciar_config("../cpu/cpu.config");
    t_config* configKERNEL = iniciar_config("../kernel/kernel.config");
    t_config* configFILESYSTEM = iniciar_config("../filesystem/filesystem.config");
    t_config* configMEMORIA = iniciar_config("../memoria/memoria.config");
    t_config* configCONSOLA = iniciar_config("../consola/consola.config");
    char* rutaSB = string_duplicate(ruta_fs);
    string_append(&rutaSB, "/fs/sbloque.dat");
    t_config* configSuperBloque = iniciar_config(rutaSB);
    t_config* configIP = iniciar_config("./IP.config");

    //---------------------------Cargo Configs--------------------------------------------------------------------------------------------------------------------------

    printf("Cargando las configuraciones\n");
    char* SuperBloqueBLOCK_COUNT = config_get_string_value(configIP, "BLOCK_COUNT");
    char* SuperBloqueBLOCK_SIZE = config_get_string_value(configIP, "BLOCK_SIZE");

    char* IP_CPU = config_get_string_value(configIP, "IP_CPU");
    char* PUERTO_CPU = config_get_string_value(configIP, "PUERTO_CPU");

    char* IP_KERNEL = config_get_string_value(configIP, "IP_KERNEL");
    char* PUERTO_KERNEL = config_get_string_value(configIP, "PUERTO_KERNEL");

    char* IP_FILESYSTEM = config_get_string_value(configIP, "IP_FILESYSTEM");
    char* PUERTO_FILESYSTEM = config_get_string_value(configIP, "PUERTO_FILESYSTEM");
    

    char* IP_MEMORIA = config_get_string_value(configIP, "IP_MEMORIA");
    char* PUERTO_MEMORIA = config_get_string_value(configIP, "PUERTO_MEMORIA");

    char* IP_CONSOLA = config_get_string_value(configIP, "IP_CONSOLA");
    char* PUERTO_CONOSLA = config_get_string_value(configIP, "PUERTO_CONSOLA");

    //---------------------------ActualizoTodo--------------------------------------------------------------------------------------------------------------------------
    printf("Actualizando los archivos");
    ActualizarCPU(configCPU, PUERTO_CPU, IP_MEMORIA, PUERTO_MEMORIA);
    printf(".");
    ActualizarKernel(configKERNEL, PUERTO_KERNEL, PUERTO_MEMORIA, IP_MEMORIA, PUERTO_FILESYSTEM, IP_FILESYSTEM, PUERTO_CPU, IP_CPU);
    printf(".");
    ActualizarFileSystem(configFILESYSTEM, PUERTO_FILESYSTEM, PUERTO_MEMORIA, IP_MEMORIA, ruta_fs);
    printf(".");
    ActualizarMemoria(configMEMORIA, PUERTO_MEMORIA);
    printf(".");
    ActualizarConsola(configCONSOLA, PUERTO_KERNEL, IP_KERNEL);
    printf(".");
    ActualizarSuperBloque(configSuperBloque, SuperBloqueBLOCK_COUNT, SuperBloqueBLOCK_SIZE);
    printf("\nListo\n");

    //---------------------------FinArchivo--------------------------------------------------------------------------------------------------------------------------
    printf("Borrando los restos\n");
    BorrarTodo(configCPU, configKERNEL, configFILESYSTEM, configMEMORIA, configCONSOLA, configSuperBloque, configIP);

    printf("Fin del programa\n");

}

//---------------------------FUNCIONES.c--------------------------------------------------------------------------------------------------------------------------
//---------------------------FUNCIONES.c--------------------------------------------------------------------------------------------------------------------------
//---------------------------FUNCIONES.c--------------------------------------------------------------------------------------------------------------------------


void ActualizarCPU (t_config* configCPU, char* PUERTO_CPU, char* IP_MEMORIA, char* PUERTO_MEMORIA){
    config_set_value(configCPU, "PUERTO_ESCUCHA", PUERTO_CPU);
    config_set_value(configCPU, "IP_MEMORIA", IP_MEMORIA);
    config_set_value(configCPU, "PUERTO_MEMORIA", PUERTO_MEMORIA);

    config_save(configCPU);
}

void ActualizarKernel(t_config* configKERNEL, char* PUERTO_KERNEL, char* PUERTO_MEMORIA, char* IP_MEMORIA, char* PUERTO_FILESYSTEM, char* IP_FILESYSTEM, char* PUERTO_CPU, char* IP_CPU){
    config_set_value(configKERNEL, "PUERTO_ESCUCHA", PUERTO_KERNEL);
    config_set_value(configKERNEL, "IP_MEMORIA", IP_MEMORIA);
    config_set_value(configKERNEL, "PUERTO_MEMORIA", PUERTO_MEMORIA);
    config_set_value(configKERNEL, "IP_FILESYSTEM", IP_FILESYSTEM);
    config_set_value(configKERNEL, "PUERTO_FILESYSTEM", PUERTO_FILESYSTEM);
    config_set_value(configKERNEL, "IP_CPU", IP_CPU);
    config_set_value(configKERNEL, "PUERTO_CPU", PUERTO_CPU);

    config_save(configKERNEL);
}

void ActualizarFileSystem(t_config* configFILESYSTEM, char* PUERTO_FILESYSTEM, char* PUERTO_MEMORIA, char* IP_MEMORIA, char* ruta_fs){
    config_set_value(configFILESYSTEM, "PUERTO_ESCUCHA", PUERTO_FILESYSTEM);
    config_set_value(configFILESYSTEM, "PUERTO_MEMORIA", PUERTO_MEMORIA);
    config_set_value(configFILESYSTEM, "IP_MEMORIA", IP_MEMORIA);

    char* path_super = string_duplicate(ruta_fs);
    string_append(&path_super,"/fs/sbloque.dat");
    config_set_value(configFILESYSTEM, "PATH_SUPERBLOQUE", path_super);

    char* PATH_BLOQUES = string_duplicate(ruta_fs);
    string_append(&PATH_BLOQUES,"/fs/bloque.dat");
    config_set_value(configFILESYSTEM, "PATH_BLOQUES", PATH_BLOQUES);

    char* PATH_BITMAP = string_duplicate(ruta_fs);
    string_append(&PATH_BITMAP,"/fs/bitmap.dat");
    config_set_value(configFILESYSTEM, "PATH_BITMAP", PATH_BITMAP);

    char* PATH_FCB = string_duplicate(ruta_fs);
    string_append(&PATH_FCB,"/fs/fcb");
    config_set_value(configFILESYSTEM, "PATH_FCB", PATH_FCB);
    

    config_save(configFILESYSTEM);
}

void ActualizarMemoria(t_config* configMEMORIA, char* PUERTO_MEMORIA){
    config_set_value(configMEMORIA, "PUERTO_ESCUCHA", PUERTO_MEMORIA);

    config_save(configMEMORIA);
}

void ActualizarConsola(t_config* configCONSOLA, char* PUERTO_KERNEL, char* IP_KERNEL){
    config_set_value(configCONSOLA, "PUERTO_KERNEL", PUERTO_KERNEL);
    config_set_value(configCONSOLA, "IP_KERNEL", IP_KERNEL);

    config_save(configCONSOLA);
}

void ActualizarSuperBloque(t_config* configSuperBloque, char* BLOCK_COUNT, char* BLOCK_SIZE){
    config_set_value(configSuperBloque, "BLOCK_COUNT", BLOCK_COUNT);
    config_set_value(configSuperBloque, "BLOCK_SIZE", BLOCK_SIZE);

    config_save(configSuperBloque);
}

void BorrarTodo(t_config* configCPU, t_config* configKERNEL, t_config* configFILESYSTEM, t_config* configMEMORIA, t_config* configCONSOLA, t_config* configSuperBloque, t_config* configIP){
    config_destroy(configCPU);
    config_destroy(configKERNEL);
    config_destroy(configFILESYSTEM);
    config_destroy(configMEMORIA);
    config_destroy(configCONSOLA);
    config_destroy(configSuperBloque);
    config_destroy(configIP);
}
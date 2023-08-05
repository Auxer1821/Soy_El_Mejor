################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/Pruebas.c \
../src/cliente.c \
../src/config.c \
../src/estructuras_compartidas.c \
../src/logs.c \
../src/servidor.c 

C_DEPS += \
./src/Pruebas.d \
./src/cliente.d \
./src/config.d \
./src/estructuras_compartidas.d \
./src/logs.d \
./src/servidor.d 

OBJS += \
./src/Pruebas.o \
./src/cliente.o \
./src/config.o \
./src/estructuras_compartidas.o \
./src/logs.o \
./src/servidor.o 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.c src/subdir.mk
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


clean: clean-src

clean-src:
	-$(RM) ./src/Pruebas.d ./src/Pruebas.o ./src/cliente.d ./src/cliente.o ./src/config.d ./src/config.o ./src/estructuras_compartidas.d ./src/estructuras_compartidas.o ./src/logs.d ./src/logs.o ./src/servidor.d ./src/servidor.o

.PHONY: clean-src


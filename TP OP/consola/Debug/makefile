################################################################################
# Automatically-generated file. Do not edit!
################################################################################

-include ../makefile.init

RM := rm -rf

# All of the sources participating in the build are defined here
-include sources.mk
-include src/subdir.mk
-include obj/subdir.mk
-include subdir.mk
ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
endif

-include ../makefile.defs

OPTIONAL_TOOL_DEPS := \
$(wildcard ../makefile.defs) \
$(wildcard ../makefile.init) \
$(wildcard ../makefile.targets) \


BUILD_ARTIFACT_NAME := consola
BUILD_ARTIFACT_EXTENSION :=
BUILD_ARTIFACT_PREFIX :=
BUILD_ARTIFACT := $(BUILD_ARTIFACT_PREFIX)$(BUILD_ARTIFACT_NAME)$(if $(BUILD_ARTIFACT_EXTENSION),.$(BUILD_ARTIFACT_EXTENSION),)

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: main-build

# Main-build Target
main-build: consola

dependents:
	-cd /home/utnso/tp-2023-1c-maxikiosco/Shared/Debug && $(MAKE) clean all

# Tool invocations
consola: $(OBJS) $(USER_OBJS) /home/utnso/tp-2023-1c-maxikiosco/Shared/Debug/libShared.so makefile $(OPTIONAL_TOOL_DEPS)
	@echo 'Building target: $@'
	@echo 'Invoking: GCC C Linker'
	gcc -L"/home/utnso/tp-2023-1c-maxikiosco/Shared/Debug" -o "consola" $(OBJS) $(USER_OBJS) $(LIBS) -lShared -lcommons
	@echo 'Finished building target: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) consola
	-@echo ' '

.PHONY: all clean dependents main-build
/home/utnso/tp-2023-1c-maxikiosco/Shared/Debug/libShared.so:

-include ../makefile.targets

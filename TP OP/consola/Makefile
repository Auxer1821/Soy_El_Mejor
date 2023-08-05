# Globales
CC = gcc
OUTFILE = consola.exe

### Reglas ###

SRC = $(shell find ./src -type f -iname "*.c" -exec basename {} \;)
OBJ = $(SRC:%.c=./obj/%.o)

SHARED = $(shell find ../Shared/obj/*.o)

build: makefolder makeshared $(OBJ)
	$(CC) -o $(OUTFILE) consola.c $(OBJ) $(SHARED) $(LINK)

LINK = -lpthread -lcommons -lm
CFLAGS = -c

debug: CFLAGS := $(CFLAGS) -g
debug: makeshared_debug build

./obj/%.o: ./src/%.c
	$(CC) $(CFLAGS) $< -o $@ $(LINK)

makefolder:
	if [ ! -d ./obj ]; then mkdir obj; fi

makeshared:
	make -C ../Shared

makeshared_debug:
	make debug -C ../Shared

# Clean

clean:
	rm -rf obj
	rm -rf *.out
	rm -rf *.log
	rm -rf *.exe
	make clean -C ../Shared

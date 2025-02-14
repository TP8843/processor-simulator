# Processor Simulator

## Example Method to Run

### For MacOS/Linux
```shell
./gradlew build run --args="../compilation/test/iterative-fibonacci/iterative-fibonacci.bin" --console=plain
```

### For Windows
```shell
./gradlew.bat build run --args="../compilation/test/iterative-fibonacci/iterative-fibonacci.bin" --console=plain
```

## Architecture Definition
- [Cycles](docs/cycle.md)
- [Instructions](docs/instructions.md)
- [Memory](docs/memory.md)

## Compilation

To compile, first create the docker instance using the `docker-compose.yml` file.

### Create Container
Enter the `compilation` folder and type:
```aiignore
docker compose up
```

### Compile C Code
Wait for this to complete. Then, open a terminal into the container and run:
```aiignore
./compile.sh file_path/file_name.c
```
This will create two files: a .out file and a .bin file with the compiled code.

- The `.bin` is the one loaded into the processor's memory.
- The `.out` can be decompiled to check that the output is correct.

### Decompile Binary
Decompile from the ELF file.
```aiignore
./decompile.sh file_path/file_name.out
```

# RV32IM Processor Simulator

- Supports all of the RV32IM specification (ignoring system calls required for running an OS).
- Has system calls for printing characters and numbers to the terminal.
- Has an included console for breakpoints (partial), stepping through the code, and seeing program output.
- Supports displaying state of all components of the processor.
- Supports loading program from raw binary file (`.bin` file)

## Running the simulator

1. Install Java 23 SDK.
2. Run one of the below commands.

### For MacOS/Linux
```shell
./gradlew build run --args="../compilation/test/iterative-fibonacci/iterative-fibonacci.bin" --console=plain
```

### For Windows
```shell
./gradlew.bat build run --args="../compilation/test/iterative-fibonacci/iterative-fibonacci.bin" --console=plain
```

## Compilation

To set up an environment for compiling for the simulator, first create the docker instance using the `docker-compose.yml` file.

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
This will create two files: a .elf file and a .bin file with the compiled code.

- The `.bin` is the one loaded into the processor's memory.
- The `.elf` can be decompiled to check that the output is correct.

### Decompile Binary
Decompile from the ELF file.
```aiignore
./decompile.sh file_path/file_name.elf
```

# Benchmark List

```shell
./gradlew build run --args="../compilation/test/quick-sort/quick-sort.bin" --console=plain
```

```shell
./gradlew build run --args="../compilation/test/iterative-fibonacci/iterative-fibonacci.bin" --console=plain
```

```shell
./gradlew build run --args="../compilation/test/recursive-fibonacci/recursive-fibonacci.bin" --console=plain
```

```shell
./gradlew build run --args="../compilation/test/iterative-factorial/iterative-factorial.bin" --console=plain
```

```shell
./gradlew build run --args="../compilation/test/recursive-factorial/recursive-factorial.bin" --console=plain
```

```shell
./gradlew build run --args="../compilation/test/gcd/gcd.bin" --console=plain
```

```shell
./gradlew build run --args="../compilation/test/vec-mul-add/vec-mul-add.bin" --console=plain
```

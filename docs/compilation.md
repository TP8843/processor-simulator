# Compilation

To compile, first create the docker instance using the `docker-compose.yml` file.

## Create Container
Enter the folder and type:
```aiignore
docker compose up
```

## Compile C Code
Wait for this to complete. Then, open a terminal into the container and run:
```aiignore
./compile.sh file_name.c
```
This will create a .elf file with the compiled code.

## Generate Binary
To create a binary to load into the memory, run:
```aiignore
riscv64-unknown-elf-objcopy -O binary file.out file.bin
```

## Decompile Binary
Decompile from the ELF file.
```aiignore
riscv64-unknown-elf-objdump -d a.out
```
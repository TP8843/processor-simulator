OUTPUT_ARCH("riscv")
ENTRY(_start)

MEMORY {
  RAM (rwx) : ORIGIN = 0x0000, LENGTH = 12K
}

SECTIONS {
  .text :
  { 
      *(.text.startup); 
      *(.text); 
      *(.text.*) 
  } > RAM
  .bss : { *(.bss); *(.bss.*) } > RAM
  .data : { *(.data); *(.data.*) } > RAM
  .rodata : { *(.rodata); *(.rodata.*) } > RAM
  
  PROVIDE(__global_pointer$ = ORIGIN(RAM) + 0x800);
  PROVIDE(__stack_top = ORIGIN(RAM) + LENGTH(RAM));
}
/* Copyright (c) 2024 ETH Zurich and University of Bologna.
 * Licensed under the Apache License, Version 2.0, see LICENSE for details.
 * SPDX-License-Identifier: Apache-2.0
 *
 * Authors:
 * - Paul Scheffler <paulsc@iis.ee.ethz.ch>
 * - Philippe Sauter <phsauter@iis.ee.ethz.ch>
 */

OUTPUT_ARCH("riscv")
ENTRY(_start)

MEMORY {
  RAM (rwx) : ORIGIN = 0x0008, LENGTH = 6K
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
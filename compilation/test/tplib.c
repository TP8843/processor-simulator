void output(volatile int value) {
	*(volatile int*)0x0 = value;
}
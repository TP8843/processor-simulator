void output(volatile int value) {
	*(volatile int*)0x0 = value;
}

int square(int num) {
    return num + num;
}

int main() {
    int result = square(4);
    
    output(result);
}
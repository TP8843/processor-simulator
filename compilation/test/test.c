void output(int value) {
	*(volatile int*)0x0 = value;
}

int square(int num) {
    return num + num;
}

int main() {
    int volatile result = square(4);
    
    output(result);
}
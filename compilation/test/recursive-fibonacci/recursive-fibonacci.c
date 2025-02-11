#include "../tplib.c"

int fibonacci(int input){
	if(input < 3) return input - 1;
	
	return fibonacci(input - 1) + fibonacci(input - 2);
}

int main(){
	output(fibonacci(10));
}
#include "../tplib.c"

int fibonacci(int input){
	if(input < 2) return input;
	
	return fibonacci(input - 1) + fibonacci(input - 2);
}

int main(){
	output(fibonacci(20));
}
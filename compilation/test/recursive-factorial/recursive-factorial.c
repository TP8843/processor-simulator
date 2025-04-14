#include "../tplib.c"

int factorial(int input){
	if(input == 1) return 1;
	return factorial(input - 1) * input;
}

int main(){
	output(factorial(5));
}
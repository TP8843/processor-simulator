#include "../tplib.c"

int fibonacci(int input){
	if(input < 2) return input;
	
	return fibonacci(input - 1) + fibonacci(input - 2);
}

int main(){
    print("The ");
    printInt(20);
    print("th fibonacci number using recursion is ");
	printInt(fibonacci(20));
	printc('\n');
}
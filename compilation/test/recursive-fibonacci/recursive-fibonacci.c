#include "../tplib.c"

static int fibonacci(int input){
	if(input < 2) return input;
	
	return fibonacci(input - 1) + fibonacci(input - 2);
}

int main(){
    for(int i = 1; i < 31; i++) {
        print("The ");
        printInt(i);
        print("th fibonacci number using recursion is ");
     	printInt(fibonacci(i));
     	printc('\n');
    }
}
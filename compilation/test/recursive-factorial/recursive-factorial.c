#include "../tplib.c"

static int factorial(int input){
	if(input == 1) return 1;
	return factorial(input - 1) * input;
}

int main(){
    print("The ");
    printInt(5);
    print("th factorial using recursion is ");
	printInt(factorial(5));
	printc('\n');
}
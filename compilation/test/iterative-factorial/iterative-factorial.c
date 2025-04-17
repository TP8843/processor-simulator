#include "../tplib.c"

int factorial(int input){
	int out = 1;
	for(int i = input; i > 1; i--){
	    out *= i;
	}

	return out;
}

int main(){
    print("The ");
    printInt(5);
    print("th factorial using iteration is ");
	printInt(factorial(5));
	printc('\n');
}
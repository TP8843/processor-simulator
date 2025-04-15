#include "../tplib.c"

int fibonacci(int input){
	int one = 0;
	int two = 1;
	int temp;
	
	for(int i = 0; i < input - 1; i++){
		temp = two + one;
		one = two;
		two = temp;
	}
	
	return two;
}

int main(){
	output(fibonacci(46));
}
#include "../tplib.c"

int main(){
    volatile int out = 5;
    out *= 14;
    print("Output: ");
	printInt(out);
	printc('\n');
}
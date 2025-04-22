#include "../tplib.c"

int main(){
//    register int value asm("a0") = 0;
    volatile int value1 = 0;
    int temp;

    for(int i = 0; i < 5; i++) {
        value1 += 2;
    }

//    asm("addi a0,zero,5" : "=r"(value));
//    asm("addi a0,a0,5" : "=r"(value) : "r"(value));
//    asm("addi a0,a0,7": "=r"(value) : "r"(value));
    printInt(value1);
}
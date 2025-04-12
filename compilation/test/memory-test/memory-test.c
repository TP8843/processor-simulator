int main(){
    asm("li a0,5");
    asm("sw a0,4(zero)");
    asm("lw a1,4(zero)");
    asm("sw a1,0(zero)");
}
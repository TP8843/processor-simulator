#include "../tplib.c"

int square(int num) {
    return num + num;
}

int main() {
    int result = square(4);
    
    output(result);
}
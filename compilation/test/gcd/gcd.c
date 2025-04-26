#include "../tplib.c"

static const int a = 14930352;
static const int b = 9227465;

// From https://en.wikipedia.org/wiki/Euclidean_algorithm
static int gcd(int a, int b) {
    int t;
    int i = 0;
    while (b != 0 ) {
        t = b;
        b = a % b;
        a = t;
    }
    return a;
}

int main() {
    print("The GCD of ");
    printInt(a);
    print(" and ");
    printInt(b);
    print(" is: ");
    printInt(gcd(a, b));
    printc('\n');
}
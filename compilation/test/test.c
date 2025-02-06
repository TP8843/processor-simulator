int square(int num) {
    return num + num;
}

int main() {
    int volatile result = square(4);
}
#include "../tplib.c"

// Used https://en.wikipedia.org/wiki/Quicksort for reference for algorithm

int partition (int* array, int low, int high) {
    int pivot = array[low];

    int i = low - 1;
    int j = high + 1;
    int temp;

    while(1) {
        do { i = i + 1; } while (array[i] < pivot);
        do { j = j - 1; } while (array[j] > pivot);

        if (i >= j) return j;

        temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}

void quickSort(int* array, int low, int high) {
    if (low >= 0 && high >= 0 && low < high) {
        int p = partition(array, low, high);
        quickSort(array, low, p);
        quickSort(array, p + 1, high);
    }
}

int main() {
    const int arrayLength = 8;
    int array[] = { 5, 4, 3, 6, 4, 1, 6, 8 };

    print("Unsorted Array: ");
    printIntArray(array, arrayLength);
    printc('\n');

    quickSort(array, 0, 7);

    print("Sorted Array: ");
    printIntArray(array, arrayLength);
    printc('\n');
}
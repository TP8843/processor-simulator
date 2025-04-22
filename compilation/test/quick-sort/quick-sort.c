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
    const int arrayLength = 100;
    int array[] = { 176, 1922, 480, 104, 33, 927, 844, 1565, 1079, 1348, 1213, 394, 1415, 355, 1808, 1471, 1776, 301, 58, 1711, 1833, 466, 1543, 1145, 643, 1077, 43, 1395, 939, 1854, 542, 893, 1098, 548, 1547, 1094, 183, 1749, 1875, 1823, 1785, 134, 336, 446, 594, 756, 1370, 1958, 1173, 1404, 165, 817, 1689, 1656, 1869, 850, 1584, 1629, 937, 455, 1235, 1503, 1931, 1598, 1927, 150, 260, 1649, 1380, 130, 1570, 1767, 1082, 329, 1144, 1229, 601, 82, 16, 348, 1934, 602, 1331, 364, 206, 1177, 1883, 648, 1532, 750, 1997, 1636, 1350, 236, 1975, 1042, 577, 278, 1899, 242 };

    print("Unsorted Array: ");
    printIntArray(array, arrayLength);
    printc('\n');

    quickSort(array, 0, arrayLength - 1);

    print("Sorted Array: ");
    printIntArray(array, arrayLength);
    printc('\n');
}
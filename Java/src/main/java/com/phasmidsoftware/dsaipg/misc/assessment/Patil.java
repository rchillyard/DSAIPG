package com.phasmidsoftware.dsaipg.misc.assessment;

public class Patil {

    static Integer[] arr = new Integer[5000];


    public static void main(String[] args) {


    }

    private static int getHashIndex(int x) {
        return x % arr.length;
    }

    private static void put(int x) {
        int hashIndex = getHashIndex(x);
        if (arr[hashIndex] == null) {
            arr[hashIndex] = x;
        }
        for (int i = hashIndex; i < arr.length; i++) {
            if (arr[i] == null) {
                arr[i] = x;
                return;
            }
        }
    }
}

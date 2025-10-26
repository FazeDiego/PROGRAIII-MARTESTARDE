//package Clase 3.Actividad 4;

import java.util.*;

public class GreedyFlorist {
    public static int getMinimumCost(int k, int[] c) {
        Arrays.sort(c);                 // ascendente
        int n = c.length;
        long total = 0;                 // evitar overflow intermedio
        int idx = 0;

        // recorre de mayor a menor
        for (int i = n - 1; i >= 0; i--) {
            int mult = (idx / k) + 1;   // idx = 0..n-1 contado desde la más cara
            total += (long) mult * c[i];
            idx++;
        }
        return (int) total;             // el enunciado garantiza < 2^31
    }

    // Mini test
    public static void main(String[] args) {
        System.out.println(getMinimumCost(3, new int[]{2,5,6}));      // 13
        System.out.println(getMinimumCost(2, new int[]{2,5,6}));      // 15
        System.out.println(getMinimumCost(3, new int[]{1,3,5,7,9}));  // 29
    }
}

//package Clase 5.Actividad 2;
import java.util.*;

/** 0/1 Problema de la mochila - fuerza bruta y programación dinámica (con reconstrucción). */
public class ProblemaMochila {


    // ======= Modelo =======
    public static class Item {
        public final int weight, value;
        public final String name;
        public Item(String name, int weight, int value) {
            this.name = name; this.weight = weight; this.value = value;
        }
        @Override public String toString() { return name + "(w=" + weight + ", v=" + value + ")"; }
    }

    public static class Result {
        public final int bestValue, totalWeight;
        public final List<Item> taken;
        Result(int bestValue, int totalWeight, List<Item> taken) {
            this.bestValue = bestValue; this.totalWeight = totalWeight; this.taken = taken;
        }
        @Override public String toString() {
            return "bestValue=" + bestValue + ", totalWeight=" + totalWeight + ", taken=" + taken;
        }
    }

    // ======= A) Fuerza bruta =======
    public static Result bruteForce(List<Item> items, int capacity) {
        return bfRec(items, capacity, 0);
    }

    private static Result bfRec(List<Item> items, int cap, int i) {
        if (i == items.size() || cap == 0) return new Result(0, 0, new ArrayList<>());
        Item it = items.get(i);

        // Opción 1: NO tomar el item i
        Result skip = bfRec(items, cap, i + 1);

        // Opción 2: tomar el item i (si entra)
        Result take = new Result(Integer.MIN_VALUE, 0, List.of()); // inválido si no entra
        if (it.weight <= cap) {
            Result sub = bfRec(items, cap - it.weight, i + 1);
            take = new Result(sub.bestValue + it.value, sub.totalWeight + it.weight,
                    new ArrayList<>(sub.taken));
            ((ArrayList<Item>) take.taken).add(0, it); // prepend para que quede en orden
        }

        // Elegir mejor
        if (take.bestValue > skip.bestValue) return take;
        return skip;
    }

    // ======= B) Programación Dinámica =======
    public static Result dp(List<Item> items, int capacity) {
        int n = items.size();
        int[][] dp = new int[n + 1][capacity + 1];
        boolean[][] keep = new boolean[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            Item it = items.get(i - 1);
            for (int w = 0; w <= capacity; w++) {
                int no = dp[i - 1][w];
                int yes = (it.weight <= w) ? dp[i - 1][w - it.weight] + it.value : Integer.MIN_VALUE;
                if (yes > no) {
                    dp[i][w] = yes;
                    keep[i][w] = true;
                } else {
                    dp[i][w] = no;
                }
            }
        }

        // Reconstrucción
        List<Item> taken = new ArrayList<>();
        int w = capacity;
        for (int i = n; i >= 1; i--) {
            if (keep[i][w]) {
                Item it = items.get(i - 1);
                taken.add(0, it); // prepend
                w -= it.weight;
            }
        }
        int totalWeight = taken.stream().mapToInt(it -> it.weight).sum();
        return new Result(dp[n][capacity], totalWeight, taken);
    }

    // ======= Demo =======
public static void main(String[] args) {
    List<Item> items = List.of(
        new Item("1", 2, 4),
        new Item("2", 5, 2),
        new Item("3", 6, 1),
        new Item("4", 7, 6)
    );
    int capacity = 10;

    Result rBF = bruteForce(items, capacity);
    Result rDP = dp(items, capacity);

    System.out.println("Fuerza bruta: " + rBF); // bestValue=10, taken=[1,4]
    System.out.println("DP:           " + rDP); // bestValue=10, taken=[1,4]
}

}

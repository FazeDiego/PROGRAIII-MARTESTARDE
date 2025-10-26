//package Clase 5.Actividad 3;
import java.util.*;

public class SelecciondeProyectos {

    static class Item {
        final int cost, benefit;
        final String name;
        Item(String name, int cost, int benefit) {
            this.name = name; this.cost = cost; this.benefit = benefit;
        }
        double ratio() { return (double) benefit / cost; }
        @Override public String toString() { return name + "(c=" + cost + ", b=" + benefit + ")"; }
    }

    static class Result {
        final int bestBenefit, totalCost;
        final List<Item> chosen;
        Result(int bestBenefit, int totalCost, List<Item> chosen) {
            this.bestBenefit = bestBenefit; this.totalCost = totalCost; this.chosen = chosen;
        }
        @Override public String toString() {
            return "beneficio=" + bestBenefit + ", costo=" + totalCost + ", proyectos=" + chosen;
        }
    }

    // ----- A) Fuerza bruta -----
    public static Result bruteForce(List<Item> items, int budget) {
        return bfRec(items, budget, 0);
    }
    private static Result bfRec(List<Item> items, int cap, int i) {
        if (i == items.size()) return new Result(0, 0, new ArrayList<>());
        Item it = items.get(i);

        // Opción NO tomar
        Result skip = bfRec(items, cap, i + 1);

        // Opción tomar (si entra)
        Result take = new Result(Integer.MIN_VALUE, 0, List.of());
        if (it.cost <= cap) {
            Result sub = bfRec(items, cap - it.cost, i + 1);
            List<Item> ch = new ArrayList<>(sub.chosen);
            ch.add(0, it);
            take = new Result(sub.bestBenefit + it.benefit, sub.totalCost + it.cost, ch);
        }
        return (take.bestBenefit > skip.bestBenefit) ? take : skip;
    }

    // ----- B) Greedy (razón beneficio/costo) – heurístico -----
    public static Result greedyByRatio(List<Item> items, int budget) {
        List<Item> sorted = new ArrayList<>(items);
        sorted.sort((a, b) -> Double.compare(b.ratio(), a.ratio())); // desc
        List<Item> chosen = new ArrayList<>();
        int cost = 0, benefit = 0;
        for (Item it : sorted) {
            if (cost + it.cost <= budget) {
                chosen.add(it);
                cost += it.cost;
                benefit += it.benefit;
            }
        }
        return new Result(benefit, cost, chosen);
    }

    // ----- C) Programación Dinámica (óptimo) -----
    public static Result dp(List<Item> items, int budget) {
        int n = items.size();
        int[][] dp = new int[n + 1][budget + 1];
        boolean[][] keep = new boolean[n + 1][budget + 1];

        for (int i = 1; i <= n; i++) {
            Item it = items.get(i - 1);
            for (int c = 0; c <= budget; c++) {
                int no = dp[i - 1][c];
                int yes = (it.cost <= c) ? dp[i - 1][c - it.cost] + it.benefit : Integer.MIN_VALUE;
                if (yes > no) { dp[i][c] = yes; keep[i][c] = true; }
                else { dp[i][c] = no; }
            }
        }

        // Reconstrucción
        List<Item> chosen = new ArrayList<>();
        int c = budget;
        for (int i = n; i >= 1; i--) {
            if (keep[i][c]) {
                Item it = items.get(i - 1);
                chosen.add(0, it);
                c -= it.cost;
            }
        }
        int totalCost = chosen.stream().mapToInt(it -> it.cost).sum();
        return new Result(dp[n][budget], totalCost, chosen);
    }

    // ----- Demo con los datos de la consigna -----
    public static void main(String[] args) {
        List<Item> items = List.of(
            new Item("P1", 10, 100),
            new Item("P2", 15, 200),
            new Item("P3", 20, 150),
            new Item("P4", 25, 300)
        );
        int budget = 40;

        System.out.println("Fuerza bruta: " + bruteForce(items, budget));   // beneficio=500, proyectos=[P2,P4]
        System.out.println("Greedy      : " + greedyByRatio(items, budget)); // en este caso también 500
        System.out.println("DP          : " + dp(items, budget));            // beneficio=500, proyectos=[P2,P4]
    }
}

//package Clase 5.Actividad 4;
import java.util.*;

public class SelecciondeInversion {
    static class Item {
        final int cost, gain; final String name;
        Item(String name, int cost, int gain) { this.name = name; this.cost = cost; this.gain = gain; }
        double ratio() { return (double) gain / cost; }
        @Override public String toString() { return name + "(c=" + cost + ", g=" + gain + ")"; }
    }
    static class Result {
        final int gain, cost; final List<Item> chosen;
        Result(int gain, int cost, List<Item> chosen) { this.gain = gain; this.cost = cost; this.chosen = chosen; }
        @Override public String toString() { return "ganancia=" + gain + ", costo=" + cost + ", paquetes=" + chosen; }
    }

    // -------- Fuerza bruta --------
    static Result bruteForce(List<Item> a, int B) { return bf(a, B, 0); }
    static Result bf(List<Item> a, int B, int i) {
        if (i == a.size()) return new Result(0, 0, new ArrayList<>());
        Item it = a.get(i);
        Result skip = bf(a, B, i + 1);
        Result take = new Result(Integer.MIN_VALUE, 0, List.of());
        if (it.cost <= B) {
            Result r = bf(a, B - it.cost, i + 1);
            ArrayList<Item> ch = new ArrayList<>(r.chosen); ch.add(0, it);
            take = new Result(r.gain + it.gain, r.cost + it.cost, ch);
        }
        return (take.gain > skip.gain) ? take : skip;
    }

    // -------- Greedy (ratio) --------
    static Result greedy(List<Item> a, int B) {
        ArrayList<Item> s = new ArrayList<>(a);
        s.sort((x,y)->Double.compare(y.ratio(), x.ratio()));
        ArrayList<Item> ch = new ArrayList<>(); int cost = 0, gain = 0;
        for (Item it: s) if (cost + it.cost <= B) { ch.add(it); cost += it.cost; gain += it.gain; }
        return new Result(gain, cost, ch);
    }

    // -------- Programación Dinámica (óptimo) --------
    static Result dp(List<Item> a, int B) {
        int n = a.size();
        int[][] dp = new int[n+1][B+1];
        boolean[][] keep = new boolean[n+1][B+1];

        for (int i=1;i<=n;i++){
            Item it = a.get(i-1);
            for (int b=0;b<=B;b++){
                int no = dp[i-1][b];
                int yes = (it.cost<=b) ? dp[i-1][b-it.cost] + it.gain : Integer.MIN_VALUE;
                if (yes>no){ dp[i][b]=yes; keep[i][b]=true; } else dp[i][b]=no;
            }
        }
        ArrayList<Item> ch = new ArrayList<>();
        int b=B;
        for (int i=n;i>=1;i--) if (keep[i][b]) { Item it=a.get(i-1); ch.add(0,it); b-=it.cost; }
        int cost = ch.stream().mapToInt(t->t.cost).sum();
        return new Result(dp[n][B], cost, ch);
    }

    public static void main(String[] args) {
        List<Item> items = List.of(
            new Item("P1", 12, 150),
            new Item("P2", 20, 200),
            new Item("P3", 15, 100),
            new Item("P4", 25, 300)
        );
        int budget = 35;

        System.out.println("Fuerza bruta: " + bruteForce(items, budget)); // {P1,P2} -> 350
        System.out.println("Greedy      : " + greedy(items, budget));     // {P1,P2} -> 350 (heurístico)
        System.out.println("DP          : " + dp(items, budget));         // {P1,P2} -> 350
    }
}


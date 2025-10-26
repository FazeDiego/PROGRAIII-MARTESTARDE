//package Clase 9.Actividad 1;
import java.util.*;

public class DFSEjemplo {
    
    public static void main(String[] args) {
        Map<Integer, List<Integer>> g = new HashMap<>();
        g.put(0, Arrays.asList(1, 2));
        g.put(1, Arrays.asList(0, 3, 4));
        g.put(2, Arrays.asList(0, 5));
        g.put(3, Arrays.asList(1, 6));
        g.put(4, Arrays.asList(1, 7, 8));
        g.put(5, Arrays.asList(2));
        g.put(6, Arrays.asList(3));
        g.put(7, Arrays.asList(4));
        g.put(8, Arrays.asList(4));

        System.out.println("DFS recursivo: " + dfsRec(g, 0));
        System.out.println("DFS iterativo: " + dfsIter(g, 0));
    }

    static List<Integer> dfsRec(Map<Integer, List<Integer>> g, int start) {
        Set<Integer> vis = new HashSet<>();
        List<Integer> order = new ArrayList<>();
        dfs(g, start, vis, order);
        return order;
    }
    static void dfs(Map<Integer, List<Integer>> g, int u, Set<Integer> vis, List<Integer> order) {
        if (!vis.add(u)) return;
        order.add(u);
        for (int v : g.getOrDefault(u, List.of())) dfs(g, v, vis, order);
    }

    static List<Integer> dfsIter(Map<Integer, List<Integer>> g, int start) {
        Set<Integer> vis = new HashSet<>();
        List<Integer> order = new ArrayList<>();
        Deque<Integer> st = new ArrayDeque<>();
        st.push(start);
        while (!st.isEmpty()) {
            int u = st.pop();
            if (vis.add(u)) {
                order.add(u);
                // para imitar el orden ascendente, empujamos vecinos en reversa
                List<Integer> neigh = new ArrayList<>(g.getOrDefault(u, List.of()));
                Collections.reverse(neigh);
                for (int v : neigh) st.push(v);
            }
        }
        return order;
    }
}


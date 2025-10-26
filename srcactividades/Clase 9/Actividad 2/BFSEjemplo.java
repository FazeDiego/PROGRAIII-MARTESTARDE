//package Clase 9.Actividad 2;
  
import java.util.*;

public class BFSEjemplo {
  
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

        System.out.println("Recorrido BFS desde 0: " + bfs(g, 0));
    }

    static List<Integer> bfs(Map<Integer, List<Integer>> g, int start) {
        Queue<Integer> q = new LinkedList<>();
        Set<Integer> vis = new HashSet<>();
        List<Integer> order = new ArrayList<>();

        q.add(start);
        vis.add(start);

        while (!q.isEmpty()) {
            int u = q.poll();
            order.add(u);

            for (int v : g.getOrDefault(u, List.of())) {
                if (!vis.contains(v)) {
                    vis.add(v);
                    q.add(v);
                }
            }
        }
        return order;
    }
}

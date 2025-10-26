//package Clase 4.Actividad 4;
import java.util.*;

/** Actividad 4: Rutas mínimas con Dijkstra (tiempos en minutos) */
public class LogisticsDijkstra {

    // Mapeo nombre <-> índice
    private final Map<String, Integer> id = new HashMap<>();
    private final List<String> names = new ArrayList<>();
    private final List<List<Edge>> adj = new ArrayList<>();

    private static class Edge {
        int to; int w; // minutos
        Edge(int to, int w) { this.to = to; this.w = w; }
    }

    private int addCity(String name) {
        return id.computeIfAbsent(name, n -> {
            int idx = names.size();
            names.add(n);
            adj.add(new ArrayList<>());
            return idx;
        });
    }

    /** Agrega carretera. Usa 'bidirectional=true' para calle de doble mano. */
    public void addRoad(String a, String b, int minutes, boolean bidirectional) {
        if (minutes < 0) throw new IllegalArgumentException("Dijkstra no permite pesos negativos");
        int u = addCity(a), v = addCity(b);
        adj.get(u).add(new Edge(v, minutes));
        if (bidirectional) adj.get(v).add(new Edge(u, minutes));
    }

    /** Resultado de Dijkstra: distancias y predecesores. */
    public static class DResult {
        public final Map<String, Integer> dist;     // tiempo mínimo
        public final Map<String, String> previous;  // para reconstruir rutas
        DResult(Map<String, Integer> d, Map<String, String> p) { dist = d; previous = p; }
    }

    /** Dijkstra desde 'sourceName' a todos. O((V+E) log V). */
    public DResult dijkstra(String sourceName) {
        Integer sIdx = id.get(sourceName);
        if (sIdx == null) throw new IllegalArgumentException("Centro desconocido: " + sourceName);

        int n = names.size();
        int[] dist = new int[n];
        int INF = 1_000_000_000;
        Arrays.fill(dist, INF);
        String[] prev = new String[n];
        boolean[] vis = new boolean[n];

        // (dist, node)
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        dist[sIdx] = 0;
        pq.offer(new int[]{0, sIdx});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int d = cur[0], u = cur[1];
            if (vis[u]) continue;
            vis[u] = true;
            if (d > dist[u]) continue;

            for (Edge e : adj.get(u)) {
                int nd = d + e.w;
                if (nd < dist[e.to]) {
                    dist[e.to] = nd;
                    prev[e.to] = names.get(u);
                    pq.offer(new int[]{nd, e.to});
                }
            }
        }

        Map<String, Integer> outDist = new LinkedHashMap<>();
        Map<String, String> outPrev = new HashMap<>();
        for (int i = 0; i < n; i++) {
            outDist.put(names.get(i), dist[i] == INF ? -1 : dist[i]); // -1 = inalcanzable
            if (prev[i] != null) outPrev.put(names.get(i), prev[i]);
        }
        return new DResult(outDist, outPrev);
    }

    /** Reconstruye la ruta mínima desde 'source' a 'target' (si existe). */
    public List<String> buildPath(String source, String target, DResult res) {
        if (!res.dist.containsKey(target) || res.dist.get(target) == -1) return List.of();
        LinkedList<String> path = new LinkedList<>();
        String cur = target;
        while (cur != null && !cur.equals(source)) {
            path.addFirst(cur);
            cur = res.previous.get(cur);
        }
        if (cur == null) return List.of(); // sin ruta
        path.addFirst(source);
        return path;
    }

    // ---Demo---
    public static void main(String[] args) {
        LogisticsDijkstra g = new LogisticsDijkstra();

        // Centros y tiempos (minutos)
        g.addRoad("Central", "A", 7, true);
        g.addRoad("Central", "B", 9, true);
        g.addRoad("A", "C", 10, true);
        g.addRoad("B", "C", 2, true);
        g.addRoad("B", "D", 11, true);
        g.addRoad("C", "D", 3, true);
        g.addRoad("C", "E", 4, true);
        g.addRoad("D", "E", 1, true);

        // Tiempo mínimo desde el centro principal
        DResult r = g.dijkstra("Central");

        System.out.println("Tiempos mínimos desde 'Central' (min):");
        r.dist.forEach((city, time) -> System.out.println("  " + city + " -> " + time));

        // Ruta ejemplar
        String destino = "E";
        List<String> ruta = g.buildPath("Central", destino, r);
        System.out.println("Ruta a " + destino + ": " + ruta + " (min=" + r.dist.get(destino) + ")");
    }
}


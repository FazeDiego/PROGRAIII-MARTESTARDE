//package Clase 4.Actividad 3;
import java.util.*;

/** Red eléctrica: grafo no dirigido y ponderado con Prim (MST). */
public class ElectricGrid {

    // Mapeo nombre -> índice
    private final Map<String, Integer> id = new HashMap<>();
    private final List<String> names = new ArrayList<>();
    // Lista de adyacencia: u -> (v, w)
    private final List<List<Edge>> adj = new ArrayList<>();

    /** Arista básica u->v con peso w (para la lista). */
    private static class Edge {
        int to;
        int w;
        Edge(int to, int w) { this.to = to; this.w = w; }
    }

    /** Arista del MST con nombres para mostrar. */
    public static class Connection {
        public final String a, b;
        public final int cost;
        Connection(String a, String b, int cost) { this.a = a; this.b = b; this.cost = cost; }
        @Override public String toString() { return a + " --(" + cost + ")--> " + b; }
    }

    private int addStation(String name) {
        return id.computeIfAbsent(name, n -> {
            int idx = names.size();
            names.add(n);
            adj.add(new ArrayList<>());
            return idx;
        });
    }

    /** Agrega conexión NO dirigida entre estaciones con costo. Idempotente si se llama dos veces con el mismo costo. */
    public void addConnection(String from, String to, int cost) {
        int u = addStation(from);
        int v = addStation(to);
        adj.get(u).add(new Edge(v, cost));
        adj.get(v).add(new Edge(u, cost));
    }

    /** Resultado del MST: total y conexiones. Puede ser bosque si hay varias componentes. */
    public static class MSTResult {
        public final int totalCost;
        public final List<Connection> connections;
        MSTResult(int totalCost, List<Connection> connections) {
            this.totalCost = totalCost;
            this.connections = connections;
        }
    }

    /** Prim con heap: O((V+E) log V). Recorre todas las componentes (bosque mínimo). */
    public MSTResult primMST() {
        int n = names.size();
        boolean[] vis = new boolean[n];
        List<Connection> result = new ArrayList<>();
        int total = 0;

        // (peso, u, v) donde u es el origen dentro del árbol ya tomado y v el nuevo candidato
        class Item {
            int w, u, v;
            Item(int w, int u, int v) { this.w = w; this.u = u; this.v = v; }
        }
        PriorityQueue<Item> pq = new PriorityQueue<>(Comparator.comparingInt(it -> it.w));

        for (int start = 0; start < n; start++) {
            if (vis[start]) continue;

            // iniciar componente
            vis[start] = true;
            for (Edge e : adj.get(start)) pq.offer(new Item(e.w, start, e.to));

            while (!pq.isEmpty()) {
                Item cur = pq.poll();
                if (vis[cur.v]) continue;           // ya incorporado
                vis[cur.v] = true;
                // añadir arista al MST
                result.add(new Connection(names.get(cur.u), names.get(cur.v), cur.w));
                total += cur.w;
                // relajar vecinos del nuevo nodo
                for (Edge e : adj.get(cur.v)) if (!vis[e.to]) pq.offer(new Item(e.w, cur.v, e.to));
            }
        }
        return new MSTResult(total, result);
    }

    // --- Demo ---
    public static void main(String[] args) {
        ElectricGrid g = new ElectricGrid();

        // Estaciones y costos (distancia/terreno)
        g.addConnection("CiudadA", "CiudadB", 4);
        g.addConnection("CiudadA", "CiudadC", 2);
        g.addConnection("CiudadB", "CiudadC", 5);
        g.addConnection("CiudadB", "CiudadD", 10);
        g.addConnection("CiudadC", "CiudadD", 3);
        g.addConnection("CiudadC", "CiudadE", 7);
        g.addConnection("CiudadD", "CiudadE", 1);

        MSTResult r = g.primMST();
        System.out.println("Conexiones seleccionadas (MST):");
        for (Connection c : r.connections) System.out.println("  " + c);
        System.out.println("Costo total = " + r.totalCost);
    }
}


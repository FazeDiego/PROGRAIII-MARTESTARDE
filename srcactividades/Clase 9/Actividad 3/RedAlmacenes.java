//package Clase 9.Actividad 3;
import java.util.*;

/** ---- Dominio ---- */
class Almacen {
    private final int id;
    private final String nombre;

    public Almacen(int id, String nombre) {
        this.id = id; this.nombre = nombre;
    }
    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override public String toString() { return id + ":" + nombre; }
}

/** ---- Grafo con lista de adyacencia ---- */
class GrafoAlmacenes {

    // almacenes por id
    private final Map<Integer, Almacen> nodos = new HashMap<>();
    // adyacencia: id -> vecinos (ids)
    private final Map<Integer, List<Integer>> adj = new HashMap<>();
    private final boolean dirigido;

    public GrafoAlmacenes(boolean dirigido) { this.dirigido = dirigido; }

    /** Agrega un almacén (id único). Idempotente. */
    public boolean agregarAlmacen(Almacen a) {
        if (nodos.containsKey(a.getId())) return false;
        nodos.put(a.getId(), a);
        adj.put(a.getId(), new ArrayList<>());
        return true;
    }

    /** Conecta dos almacenes con ruta directa. */
    public void conectar(int idA, int idB) {
        validar(idA); validar(idB);
        adj.get(idA).add(idB);
        if (!dirigido) adj.get(idB).add(idA);
    }

    private void validar(int id) {
        if (!nodos.containsKey(id)) throw new IllegalArgumentException("No existe almacén id=" + id);
    }

    /** DFS recursivo: devuelve orden de visita (ids). */
    public List<Integer> dfs(int idInicio) {
        validar(idInicio);
        Set<Integer> vis = new HashSet<>();
        List<Integer> orden = new ArrayList<>();
        dfsRec(idInicio, vis, orden);
        return orden;
        // Si quisieras cubrir grafo no conexo: iterar por todos los nodos y llamar dfsRec si no visitado.
    }
    private void dfsRec(int u, Set<Integer> vis, List<Integer> orden) {
        if (!vis.add(u)) return;
        orden.add(u);
        for (int v : adj.getOrDefault(u, List.of())) dfsRec(v, vis, orden);
    }

    /** BFS: devuelve orden de visita (ids). */
    public List<Integer> bfs(int idInicio) {
        validar(idInicio);
        Queue<Integer> q = new ArrayDeque<>();
        Set<Integer> vis = new HashSet<>();
        List<Integer> orden = new ArrayList<>();

        q.add(idInicio); vis.add(idInicio);
        while (!q.isEmpty()) {
            int u = q.poll();
            orden.add(u);
            for (int v : adj.getOrDefault(u, List.of())) {
                if (vis.add(v)) q.add(v);
            }
        }
        return orden;
    }

    /** Utilidades de presentación */
    public String nombres(List<Integer> ids) {
        List<String> ns = new ArrayList<>();
        for (int id : ids) ns.add(nodos.get(id).getNombre());
        return ns.toString();
    }

    /** Muestra la lista de adyacencia (para debug). */
    public void imprimirGrafo() {
        System.out.println("Adyacencias:");
        for (var e : adj.entrySet()) {
            System.out.print(e.getKey() + " -> ");
            System.out.println(e.getValue());
        }
    }
}

/** ---- Demo mínima ---- */
public class RedAlmacenes {
    public static void main(String[] args) {
        GrafoAlmacenes g = new GrafoAlmacenes(false); // false => no dirigido

        g.agregarAlmacen(new Almacen(0, "Central"));
        g.agregarAlmacen(new Almacen(1, "Norte"));
        g.agregarAlmacen(new Almacen(2, "Oeste"));
        g.agregarAlmacen(new Almacen(3, "Sur"));
        g.agregarAlmacen(new Almacen(4, "Este"));

        g.conectar(0, 1);
        g.conectar(0, 2);
        g.conectar(1, 3);
        g.conectar(2, 4);
        g.conectar(3, 4);

        g.imprimirGrafo();

        var dfs = g.dfs(0);
        var bfs = g.bfs(0);

        System.out.println("DFS desde 0 (ids):  " + dfs);
        System.out.println("DFS (nombres):      " + g.nombres(dfs));

        System.out.println("BFS desde 0 (ids):  " + bfs);
        System.out.println("BFS (nombres):      " + g.nombres(bfs));
    }
}

//package Clase 9.Actividad 4;
import java.util.*;

/** ---- Dominio ---- */
class Usuario {
    private final int id;
    private final String nombre;
    public Usuario(int id, String nombre) { this.id = id; this.nombre = nombre; }
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    @Override public String toString() { return id + ":" + nombre; }
}

/** ---- Grafo social (no dirigido) con lista de adyacencia ---- */
class GrafoSocial {
    private final Map<Integer, Usuario> usuarios = new HashMap<>();
    private final Map<Integer, List<Integer>> amigos = new HashMap<>();

    /** Agrega usuario (id único). */
    public boolean agregarUsuario(Usuario u) {
        if (usuarios.containsKey(u.getId())) return false;
        usuarios.put(u.getId(), u);
        amigos.put(u.getId(), new ArrayList<>());
        return true;
    }

    /** Conecta dos usuarios como amigos (relación bidireccional). */
    public void conectarAmigos(int idA, int idB) {
        validar(idA); validar(idB);
        if (idA == idB) return;
        if (!amigos.get(idA).contains(idB)) amigos.get(idA).add(idB);
        if (!amigos.get(idB).contains(idA)) amigos.get(idB).add(idA);
    }

    /** DFS desde idInicio (lista de IDs en orden de visita). */
    public List<Integer> dfs(int idInicio) {
        validar(idInicio);
        List<Integer> orden = new ArrayList<>();
        Set<Integer> vis = new HashSet<>();
        dfsRec(idInicio, vis, orden);
        return orden;
    }
    private void dfsRec(int u, Set<Integer> vis, List<Integer> orden) {
        if (!vis.add(u)) return;
        orden.add(u);
        for (int v : amigos.getOrDefault(u, List.of())) dfsRec(v, vis, orden);
    }

    /** BFS desde idInicio (lista de IDs en orden de visita). */
    public List<Integer> bfs(int idInicio) {
        validar(idInicio);
        Queue<Integer> q = new ArrayDeque<>();
        Set<Integer> vis = new HashSet<>();
        List<Integer> orden = new ArrayList<>();
        q.add(idInicio); vis.add(idInicio);
        while (!q.isEmpty()) {
            int u = q.poll();
            orden.add(u);
            for (int v : amigos.getOrDefault(u, List.of())) {
                if (vis.add(v)) q.add(v);
            }
        }
        return orden;
    }

    /** Utilidad: convierte IDs a nombres para mostrar. */
    public List<String> nombres(List<Integer> ids) {
        List<String> out = new ArrayList<>();
        for (int id : ids) out.add(usuarios.get(id).getNombre());
        return out;
    }

    private void validar(int id) {
        if (!usuarios.containsKey(id)) throw new IllegalArgumentException("No existe usuario id=" + id);
    }
}

/** ---- Demo mínima ---- */
public class RedSocial {
    public static void main(String[] args) {
        GrafoSocial g = new GrafoSocial();

        g.agregarUsuario(new Usuario(0, "Ana"));
        g.agregarUsuario(new Usuario(1, "Beto"));
        g.agregarUsuario(new Usuario(2, "Cata"));
        g.agregarUsuario(new Usuario(3, "Dami"));
        g.agregarUsuario(new Usuario(4, "Ema"));
        g.agregarUsuario(new Usuario(5, "Fede"));

        g.conectarAmigos(0, 1);
        g.conectarAmigos(0, 2);
        g.conectarAmigos(1, 3);
        g.conectarAmigos(2, 4);
        g.conectarAmigos(3, 5);

        List<Integer> rDfs = g.dfs(0);
        List<Integer> rBfs = g.bfs(0);

        System.out.println("DFS (ids):      " + rDfs);
        System.out.println("DFS (nombres):  " + g.nombres(rDfs));
        System.out.println("BFS (ids):      " + rBfs);
        System.out.println("BFS (nombres):  " + g.nombres(rBfs));
    }
}

//package Clase 4.Actividad 1;
import java.util.*;

/** Grafo de seguidores con listas de adyacencia (dirigido). */
public class SocialGraph {

    // u -> conjunto de usuarios a los que u sigue
    private final Map<String, Set<String>> following = new HashMap<>();
    // u -> conjunto de usuarios que siguen a u
    private final Map<String, Set<String>> followers = new HashMap<>();

    // --- Utilidad interna ---
    private void ensureUser(String id) {
        following.computeIfAbsent(id, k -> new HashSet<>());
        followers.computeIfAbsent(id, k -> new HashSet<>());
    }

    // --- Operaciones requeridas ---

    /** Agrega un usuario (id único). Idempotente. */
    public boolean agregarUsuario(String id) {
        boolean nuevo = !following.containsKey(id);
        ensureUser(id);
        return nuevo;
    }

    /** a sigue a b. No permite self-follow ni duplicados. */
    public boolean seguir(String a, String b) {
        if (a == null || b == null || a.equals(b)) return false;
        if (!following.containsKey(a) || !following.containsKey(b)) return false;
        boolean agregado = following.get(a).add(b);
        if (agregado) followers.get(b).add(a);
        return agregado;
    }

    /** a deja de seguir a b. Idempotente. */
    public boolean dejarDeSeguir(String a, String b) {
        if (!following.containsKey(a) || !following.containsKey(b)) return false;
        boolean quitado = following.get(a).remove(b);
        if (quitado) followers.get(b).remove(a);
        return quitado;
    }

    /** Lista de usuarios que id sigue (out-neighbors). */
    public List<String> listaSeguidos(String id) {
        if (!following.containsKey(id)) return List.of();
        return ordenado(following.get(id));
    }

    /** Lista de seguidores de id (in-neighbors). */
    public List<String> listaSeguidores(String id) {
        if (!followers.containsKey(id)) return List.of();
        return ordenado(followers.get(id));
    }

    private static List<String> ordenado(Set<String> s) {
        List<String> r = new ArrayList<>(s);
        Collections.sort(r); // opcional; quitar si no querés costo extra
        return r;
    }

    // --- Demo ---
    public static void main(String[] args) {
        SocialGraph g = new SocialGraph();
        g.agregarUsuario("ana");
        g.agregarUsuario("beto");
        g.agregarUsuario("cata");
        g.agregarUsuario("dami");

        g.seguir("ana", "beto");
        g.seguir("ana", "cata");
        g.seguir("beto", "cata");
        g.seguir("cata", "ana");
        g.dejarDeSeguir("ana", "beto");

        System.out.println("Seguidos por ana: " + g.listaSeguidos("ana"));      // [cata]
        System.out.println("Seguidores de cata: " + g.listaSeguidores("cata")); // [ana, beto]
        System.out.println("Seguidores de ana: " + g.listaSeguidores("ana"));   // [cata]
    }
}

//package Clase 4.Actividad 2;
import java.util.*;

/** Grafo dirigido con matriz de adyacencia (0/1). */
public class AdjacencyMatrixGraph {

    private final int n;         // cantidad de vértices
    private final byte[][] mat;  // 0/1 para ausencia/presencia de arista

    /** Inicialización del grafo con n vértices (0..n-1). */
    public AdjacencyMatrixGraph(int n) {
        if (n <= 0) throw new IllegalArgumentException("n debe ser > 0");
        this.n = n;
        this.mat = new byte[n][n];
    }

    private void check(int v) {
        if (v < 0 || v >= n) throw new IndexOutOfBoundsException("vértice fuera de rango: " + v);
    }

    /** Agregar arista dirigida u -> v. Idempotente. */
    public void agregarArista(int u, int v) {
        check(u); check(v);
        mat[u][v] = 1;
    }

    /** Eliminar arista dirigida u -> v. Idempotente. */
    public void eliminarArista(int u, int v) {
        check(u); check(v);
        mat[u][v] = 0;
    }

    /** Verificar si existe arista u -> v. */
    public boolean existeArista(int u, int v) {
        check(u); check(v);
        return mat[u][v] == 1;
    }

    /** Lista de adyacentes (vecinos de salida) de u. */
    public List<Integer> adyacentesDe(int u) {
        check(u);
        List<Integer> res = new ArrayList<>();
        for (int v = 0; v < n; v++) if (mat[u][v] == 1) res.add(v);
        return res;
    }

    /** Grado de salida: cantidad de aristas que salen de u. */
    public int gradoSalida(int u) {
        check(u);
        int cont = 0;
        for (int v = 0; v < n; v++) cont += mat[u][v];
        return cont;
    }

    /** Grado de entrada: cantidad de aristas que entran a u. */
    public int gradoEntrada(int u) {
        check(u);
        int cont = 0;
        for (int v = 0; v < n; v++) cont += mat[v][u];
        return cont;
    }

    /** Utilidad: cantidad de vértices. */
    public int cantidadVertices() { return n; }

    // --- Demo mínima ---
    public static void main(String[] args) {
        AdjacencyMatrixGraph g = new AdjacencyMatrixGraph(5); // 0..4

        g.agregarArista(0, 1);
        g.agregarArista(0, 3);
        g.agregarArista(1, 2);
        g.agregarArista(3, 2);
        g.agregarArista(4, 0);

        System.out.println("Existe 0->1 ? " + g.existeArista(0, 1)); // true
        System.out.println("Adyacentes de 0: " + g.adyacentesDe(0)); // [1, 3]
        System.out.println("Grado salida 0: " + g.gradoSalida(0));   // 2
        System.out.println("Grado entrada 2: " + g.gradoEntrada(2)); // 2

        g.eliminarArista(0, 1);
        System.out.println("Existe 0->1 ? " + g.existeArista(0, 1)); // false
    }
}


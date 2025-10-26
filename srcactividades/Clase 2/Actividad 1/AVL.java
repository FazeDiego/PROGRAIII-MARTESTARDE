import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de un Árbol AVL (Adelson-Velsky y Landis)
 * Un árbol AVL es un árbol binario de búsqueda balanceado donde:
 * - La diferencia de alturas entre subárboles de cualquier nodo es como máximo 1
 * - Se realizan rotaciones para mantener esta propiedad tras cada inserción
 */
public class AVL<T extends Comparable<T>> {
    private Nodo<T> root;

    /**
     * Nodo interno del árbol AVL que almacena:
     * - Un valor de tipo genérico T
     * - Referencias a hijos izquierdo y derecho
     * - La altura del subárbol que tiene este nodo como raíz
     */
    public static class Nodo<T> {
        T valor;
        Nodo<T> izquierdo, derecho;
        int altura;

        public Nodo(T valor) {
            this.valor = valor;
            this.altura = 1;
        }
        @Override
        public String toString() {
            return valor.toString();
        }
    }

    public void insertar(T valor) {
        root = insertarRec(root, valor);
    }

    private Nodo<T> insertarRec(Nodo<T> nodo, T valor) {
        if (nodo == null) {
            return new Nodo<>(valor);
        }
        int cmp = valor.compareTo(nodo.valor);
        if (cmp < 0) {
            nodo.izquierdo = insertarRec(nodo.izquierdo, valor);
        } else if (cmp > 0) {
            nodo.derecho = insertarRec(nodo.derecho, valor);
        } else {
            return nodo;
        }
        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));
        int balance = balance(nodo);

        // Rotaciones para balancear
        if (balance > 1 && valor.compareTo(nodo.izquierdo.valor) < 0)
            return rotarDerecha(nodo);
        if (balance < -1 && valor.compareTo(nodo.derecho.valor) > 0)
            return rotarIzquierda(nodo);
        if (balance > 1 && valor.compareTo(nodo.izquierdo.valor) > 0) {
            nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
            return rotarDerecha(nodo);
        }
        if (balance < -1 && valor.compareTo(nodo.derecho.valor) < 0) {
            nodo.derecho = rotarDerecha(nodo.derecho);
            return rotarIzquierda(nodo);
        }
        return nodo;
    }

    private int altura(Nodo<T> nodo) {
        return nodo == null ? 0 : nodo.altura;
    }

    private int balance(Nodo<T> nodo) {
        return nodo == null ? 0 : altura(nodo.izquierdo) - altura(nodo.derecho);
    }

    private Nodo<T> rotarDerecha(Nodo<T> y) {
        Nodo<T> x = y.izquierdo;
        Nodo<T> T2 = x.derecho;
        x.derecho = y;
        y.izquierdo = T2;
        y.altura = Math.max(altura(y.izquierdo), altura(y.derecho)) + 1;
        x.altura = Math.max(altura(x.izquierdo), altura(x.derecho)) + 1;
        return x;
    }

    private Nodo<T> rotarIzquierda(Nodo<T> x) {
        Nodo<T> y = x.derecho;
        Nodo<T> T2 = y.izquierdo;
        y.izquierdo = x;
        x.derecho = T2;
        x.altura = Math.max(altura(x.izquierdo), altura(x.derecho)) + 1;
        y.altura = Math.max(altura(y.izquierdo), altura(y.derecho)) + 1;
        return y;
    }

    /**
     * Realiza un recorrido inOrder (en-orden) del árbol.
     * El recorrido inOrder en un BST visita los nodos en orden ascendente:
     * 1. Recorre subárbol izquierdo
     * 2. Visita nodo actual
     * 3. Recorre subárbol derecho
     * 
     * Complejidad: O(n) donde n es el número de nodos
     * - Cada nodo se visita exactamente una vez
     * - El orden de recorrido es: IZQUIERDO -> RAÍZ -> DERECHO
     */
    public void inOrder() {
        inOrder(root);
    }

    private void inOrder(Nodo<T> nodo) {
        if(nodo==null) return;
        inOrder(nodo.izquierdo);
        System.out.println(nodo.toString());
        inOrder(nodo.derecho);
    }

    public Nodo<T> getRoot() {
        return root;
    }
}
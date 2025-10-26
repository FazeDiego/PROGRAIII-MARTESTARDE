package gamescore;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de un árbol AVL especializado para el sistema GameScore.
 * Permite búsquedas eficientes por rango de puntajes.
 * 
 * ¿Por qué AVL es óptimo para este problema?
 * 1. Balanceo automático: Garantiza altura O(log n)
 * 2. Búsqueda por rango eficiente: Solo visitamos nodos relevantes
 * 3. Ordenamiento implícito: Facilita encontrar los k mejores
 */
public class GameScoreAVL {
    private Nodo root;
    
    private static class Nodo {
        Jugador jugador;
        Nodo izquierdo, derecho;
        int altura;
        int size;  // cantidad de nodos en el subárbol

        Nodo(Jugador jugador) {
            this.jugador = jugador;
            this.altura = 1;
            this.size = 1;
        }
    }

    public void insertar(Jugador jugador) {
        root = insertarRec(root, jugador);
    }

    private Nodo insertarRec(Nodo nodo, Jugador jugador) {
        if (nodo == null) {
            return new Nodo(jugador);
        }

        if (jugador.getPuntaje() < nodo.jugador.getPuntaje()) {
            nodo.izquierdo = insertarRec(nodo.izquierdo, jugador);
        } else if (jugador.getPuntaje() > nodo.jugador.getPuntaje()) {
            nodo.derecho = insertarRec(nodo.derecho, jugador);
        } else {
            if (jugador.getNombre().compareTo(nodo.jugador.getNombre()) < 0) {
                nodo.izquierdo = insertarRec(nodo.izquierdo, jugador);
            } else if (jugador.getNombre().compareTo(nodo.jugador.getNombre()) > 0) {
                nodo.derecho = insertarRec(nodo.derecho, jugador);
            } else {
                return nodo;
            }
        }

        actualizarAlturaYSize(nodo);
        return balancear(nodo);
    }

    public List<Jugador> findInRange(int p_min, int p_max, int k) {
        List<Jugador> resultado = new ArrayList<>();
        findInRangeRec(root, p_min, p_max, k, resultado);
        return resultado;
    }

    private void findInRangeRec(Nodo nodo, int p_min, int p_max, int k, List<Jugador> resultado) {
        if (nodo == null || resultado.size() >= k) {
            return;
        }

        if (nodo.jugador.getPuntaje() <= p_max) {
            findInRangeRec(nodo.derecho, p_min, p_max, k, resultado);
        }

        if (resultado.size() < k && 
            nodo.jugador.getPuntaje() >= p_min && 
            nodo.jugador.getPuntaje() <= p_max) {
            resultado.add(nodo.jugador);
        }

        if (resultado.size() < k && nodo.jugador.getPuntaje() >= p_min) {
            findInRangeRec(nodo.izquierdo, p_min, p_max, k, resultado);
        }
    }

    private void actualizarAlturaYSize(Nodo nodo) {
        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));
        nodo.size = 1 + size(nodo.izquierdo) + size(nodo.derecho);
    }

    private int altura(Nodo nodo) {
        return nodo == null ? 0 : nodo.altura;
    }

    private int size(Nodo nodo) {
        return nodo == null ? 0 : nodo.size;
    }

    private int getBalance(Nodo nodo) {
        return nodo == null ? 0 : altura(nodo.izquierdo) - altura(nodo.derecho);
    }

    private Nodo rotarDerecha(Nodo y) {
        Nodo x = y.izquierdo;
        Nodo T2 = x.derecho;

        x.derecho = y;
        y.izquierdo = T2;

        actualizarAlturaYSize(y);
        actualizarAlturaYSize(x);

        return x;
    }

    private Nodo rotarIzquierda(Nodo x) {
        Nodo y = x.derecho;
        Nodo T2 = y.izquierdo;

        y.izquierdo = x;
        x.derecho = T2;

        actualizarAlturaYSize(x);
        actualizarAlturaYSize(y);

        return y;
    }

    private Nodo balancear(Nodo nodo) {
        int balance = getBalance(nodo);

        if (balance > 1) {
            if (getBalance(nodo.izquierdo) < 0) {
                nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
            }
            return rotarDerecha(nodo);
        }
        if (balance < -1) {
            if (getBalance(nodo.derecho) > 0) {
                nodo.derecho = rotarDerecha(nodo.derecho);
            }
            return rotarIzquierda(nodo);
        }

        return nodo;
    }
}
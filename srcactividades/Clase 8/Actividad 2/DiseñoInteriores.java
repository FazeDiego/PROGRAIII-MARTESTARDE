//package Clase 8.Actividad 2;
import java.util.*;

public class DiseñoInteriores {
    



    // N = tamaño de la habitación (N x N)
    static int N = 4;

    public static void main(String[] args) {
        List<int[]> soluciones = new ArrayList<>();
        // cols[r] = columna elegida para la fila r
        int[] cols = new int[N];
        Arrays.fill(cols, -1);

        // columnas ocupadas
        boolean[] usedCol = new boolean[N];

        backtrack(0, cols, usedCol, soluciones);

        // Mostrar soluciones
        int idx = 1;
        for (int[] s : soluciones) {
            System.out.println("Diseño #" + (idx++));
            imprimirPlano(s);
            System.out.println("Coordenadas: " + coords(s));
            System.out.println();
        }
        System.out.println("Total de combinaciones: " + soluciones.size()); // 24 para N=4
    }

    // Backtracking fila por fila
    private static void backtrack(int fila, int[] cols, boolean[] usedCol, List<int[]> soluciones) {
        if (fila == N) {
            soluciones.add(cols.clone());
            return;
        }
        for (int c = 0; c < N; c++) {
            if (usedCol[c]) continue; // no compartir columna
            // colocar elemento en (fila, c)
            cols[fila] = c;
            usedCol[c] = true;

            backtrack(fila + 1, cols, usedCol, soluciones);

            // deshacer
            usedCol[c] = false;
            cols[fila] = -1;
        }
    }

    // Impresión de un plano 2D simple
    private static void imprimirPlano(int[] cols) {
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                System.out.print(cols[r] == c ? "E " : ". ");
            }
            System.out.println();
        }
    }

    // Lista de coordenadas (fila,columna) 1-indexadas
    private static List<String> coords(int[] cols) {
        List<String> out = new ArrayList<>();
        for (int r = 0; r < N; r++) out.add("(" + (r+1) + "," + (cols[r]+1) + ")");
        return out;
    }
}


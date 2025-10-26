//package Clase 8.Actividad 3;
import java.util.*;
public class DistribucionEquipos {
   
    static final int N = 4; // tablero 4x4
    static int soluciones = 0;

    public static void main(String[] args) {
        char[][] board = new char[N][N];
        for (char[] fila : board) Arrays.fill(fila, '.');

        // Backtracking: primero colocar 4 computadoras, luego impresoras
        placeComputers(board, 0, new boolean[N], new boolean[N]);
        System.out.println("Total de configuraciones: " + soluciones);
    }

    // ----------------- Colocar COMPUTADORAS -----------------
    private static void placeComputers(char[][] board, int fila, boolean[] usedRowC, boolean[] usedColC) {
        if (fila == N) {
            // todas las computadoras colocadas → colocar impresoras
            placePrinters(board, 0, new boolean[N], new boolean[N]);
            return;
        }

        for (int col = 0; col < N; col++) {
            if (!usedColC[col]) {
                board[fila][col] = 'C';
                usedColC[col] = true;

                placeComputers(board, fila + 1, usedRowC, usedColC);

                // backtrack
                board[fila][col] = '.';
                usedColC[col] = false;
            }
        }
    }

    // ----------------- Colocar IMPRESORAS -----------------
    private static void placePrinters(char[][] board, int fila, boolean[] usedRowP, boolean[] usedColP) {
        if (fila == N) {
            soluciones++;
            System.out.println("Configuración #" + soluciones);
            printBoard(board);
            System.out.println();
            return;
        }

        for (int col = 0; col < N; col++) {
            // restricción: una impresora por fila y columna, y no sobreescribir una computadora
            if (!usedColP[col] && board[fila][col] == '.') {
                board[fila][col] = 'P';
                usedColP[col] = true;

                placePrinters(board, fila + 1, usedRowP, usedColP);

                // backtrack
                board[fila][col] = '.';
                usedColP[col] = false;
            }
        }
    }

    // ----------------- Utilidad: imprimir tablero -----------------
    private static void printBoard(char[][] board) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) System.out.print(board[i][j] + " ");
            System.out.println();
        }
    }
}

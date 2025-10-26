//package Clase 8.Actividad 1;
import java.util.*;

public class SumaSubconjunto {
    
    // Función principal
    public static void main(String[] args) {
        int[] numeros = {2, 3, 5};
        int objetivo = 5;

        System.out.println("Subconjuntos que suman " + objetivo + ":");
        backtrack(numeros, 0, objetivo, new ArrayList<>(), 0);
    }

    /**
     * @param arr       conjunto de números
     * @param i         índice actual
     * @param objetivo  suma deseada
     * @param actual    subconjunto construido hasta ahora
     * @param suma      suma parcial
     */
    private static void backtrack(int[] arr, int i, int objetivo, List<Integer> actual, int suma) {
        // Caso base: llegamos al final del arreglo
        if (i == arr.length) {
            if (suma == objetivo) {
                System.out.println(actual);
            }
            return;
        }

        // Opción 1: incluir arr[i]
        actual.add(arr[i]);
        backtrack(arr, i + 1, objetivo, actual, suma + arr[i]);

        // Opción 2: no incluir arr[i]
        actual.remove(actual.size() - 1);
        backtrack(arr, i + 1, objetivo, actual, suma);
    }
}

/* Actividad 1.1 a) .java 6/8/2025 Martinez Diego Lu: 1140019 
Realizar un programa en Java, dado un array, calcular el maximo.
¿Cual es el orden de complejidad asintótica del algoritmo? 
 */

public class Actividad1a {
    public static int max(int[] arr) {
        int max = arr[0]; // O(1)
        for (int i = 1; i < arr.length; i++) { // O(n)
            if (arr[i] > max) { // O(1)
                max = arr[i]; // O(1)
            }
        }
        return max; // O(1)
    }
    // Complejidad temporal total: O(n)
}


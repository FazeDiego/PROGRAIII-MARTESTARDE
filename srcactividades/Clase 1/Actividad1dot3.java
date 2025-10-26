/*
Actividad 1.3 .java 6/8/2025 Martinez Diego Lu: 1140019 
*/

public class Actividad1dot3 {

    // Método recursivo para sumar los primeros n enteros
    public static int sumaRecursiva(int n) {
        if (n == 0) { // Caso base
            return 0;
        } else {
            return n + sumaRecursiva(n - 1); // Llamada recursiva
        }
    }

    public static void main(String[] args) {
        int n = 10;
        int suma = sumaRecursiva(n);
        System.out.println("La suma de los primeros " + n + " números enteros es: " + suma);
    }
}

/*
Análisis de recurrencia:

Sea T(n) el tiempo de ejecución para sumar los primeros n enteros:

T(n) = T(n-1) + c   (por la llamada recursiva y la suma)
T(0) = d            (caso base)

Resolviendo la recurrencia:
T(n) = T(n-1) + c
     = T(n-2) + 2c
     = ...
     = T(0) + n*c
     = d + n*c

Por lo tanto, la complejidad es O(n)
*/

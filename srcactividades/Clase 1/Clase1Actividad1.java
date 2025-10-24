// Clase1Actividad1 5/8/2025
public class Clase1Actividad1 {
    public static void main(String[] args) {
        int[][] mat = {
            {4, 5, 6},
            {7, 8, 9},
            {5, 6, 7}
        }; //1
        int n = mat.length; //1
        int suma = 0; //1
        int contador = 0; //1

        // recorre la matriz y suma todos los elementos
        for (int i = 0; i < n; i++) { //1 + 2(n+1)
            for (int j = 0; j < n; j++) { //1 + 2(n+1)n
                suma += mat[i][j]; //n*n
                contador++; //n*n
            }
        }

        double promedio = (double) suma / contador; //1
        System.out.println("El promedio de la matriz es: " + promedio); //1
    }
}
/*  
constante o(1) -> asignaciones, operaciones aritméticas, acceso a elementos de la matriz
lineal o(n) -> recorrer filas o columnas de la matriz
cuadrática o(n^2) -> recorrer filas y columnas de la matriz

conteo de instrucciones:
total: 1+1+1+1+1+2(n+1)+1+2(n+1)n+n*n+n*n+1+1
≈ 2n^2 + 2n + 9

Complejidad asintótica: O(n^2)
*/

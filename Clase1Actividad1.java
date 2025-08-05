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

        // recorrer la matriz y sumar todos los elementos
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
conteo de instrucciones:
1 (declaración mat)
1 (declaración n)
1 (declaración suma)
1 (declaración contador)
1 (inicio for i)
2(n+1) (condición y actualización for i)
1 (inicio for j)
2(n+1)n (condición y actualización for j)
n*n (suma)
n*n (contador)
1 (cálculo promedio)
1 (print)

total: 1+1+1+1+1+2(n+1)+1+2(n+1)n+n*n+n*n+1+1
≈ 2n^2 + 2n + 9

Complejidad asintótica: O(n^2)
*/
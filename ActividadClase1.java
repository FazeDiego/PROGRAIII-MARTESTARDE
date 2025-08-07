
package org.example;

public class ActividadClase1 {

    int[][] mat = {
            {4, 5, 6},
            {7, 8, 9},
            {5, 6, 7}
    };

    public int sumar (int[] numeros ){
        int suma = 0; //1
        int i = 0; //1
        while (i < numeros.length){ //2 (n+1)
            suma += numeros[i]; //3n
            i++; //n
        }
        return suma; //1
    }
/*
conteo de instrucciones

f(n) = 1 + 1 + 2 (n+1) + 3n + n + 1
f(n) = 1 + 1 + 2n + 2 + 3n + n + 1
f(n) = 6n + 5

hago la demostración matemática para ver la eficiencia

f(n) <= c g(n)
6n + 5 <= c.n
6n + 5 <= 7n           (tomo 7n porque es mayor a 6n. podria tomar otro )
6n/n + 5/n <= 7n/n
6 + 5/n <= 7

para n = 1, no se cumple. 6 + 5/1 <= 7. 11 no es menor.
para n = 2, no se cumple
para n = 3, no se cumple
para n = 5, 6 + 5/5 <= 7. 7 <= 7. se cumple!!!
f(n) pertenece a O(n) para n >= 5 y c = 7
 */

    public double promedio (){
        int suma = 0; //1
        int n = this.mat.length; //2
        for (int i = 0; i < n; i++){ //1 + (n+1) + n
            for (int j = 0; j < n; j++){ //1 + (n+1) + n
                suma += this.mat[i][j]; //3n
            }
        }
        int cantidad = n * n; //1
        double promedio = (double) suma/cantidad; //3
        System.out.println("promedio = " + promedio); //2
        return promedio; //1
    }
/*
conteo de instrucciones

f(n) = 10 + 1 + (n+1) + n + n( 1 + (n+1) + n + 3n)
f(n) = 10 + 1 + (n+1) + n + n( 1 + 5n)
f(n) = 10 + 1 + n + 1 + n + 5n^2
f(n) = 12 + 2n + 5n^2

hago la demostración matemática para ver la eficiencia
para f(n) <= c g(n^2)
propongo -> g(n) = n^2 y c = 6

12 + 2n + 5n^2 <= 6n^2
divido ambos lados por n^2

12/n^2 + 2n/n^2 + 5n^2/n^2 <= 6n^2/n^2
12/n^2 + 2/n + 5 <= 6

evaluo para diferentes valores de n
para n = 1, no cumple
para n = 2, no cumple
para n = 3, no cumple
para n = 4, no cumple
para n = 5, cumple!! 0.48 + 0.4 + 5 = 5.88

f(n) pertenece a O(n^2) para n >= 5 y c = 6
 */

















}

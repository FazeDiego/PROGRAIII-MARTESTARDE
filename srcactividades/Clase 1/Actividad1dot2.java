/*
Actividad 1.2 .java 6/8/2025 Martinez Diego Lu: 1140019 
 Para calcular el factorial de un número en Java y evitar problemas de desbordamiento se puede usar el tipo de dato
 long y BigInteger. El long tiene un limite de 20! mientras que BigInteger puede manejar números mucho más grandes.
 Esta implementación muestra cómo calcular el factorial de un número usando ambos tipos de datos.
 */

import java.math.BigInteger;

public class Actividad1dot2{
    // Factorial usando long (iterativo)
    public static long factorialLong(int n) {
        long resultado = 1L;
        for (int i = 2; i <= n; i++) {
            resultado *= i;
        }
        return resultado;
    }

    // Factorial usando BigInteger (iterativo)
    public static BigInteger factorialBigInteger(int n) {
        BigInteger resultado = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            resultado = resultado.multiply(BigInteger.valueOf(i));
        }
        return resultado;
    }

    public static void main(String[] args) {
        int number = 20;

        // Usando long
        long factLong = factorialLong(number);
        System.out.println("Factorial de " + number + " usando long: " + factLong);

        // Usando BigInteger
        BigInteger factBig = factorialBigInteger(number);
        System.out.println("Factorial de " + number + " usando BigInteger: " + factBig);
    }
}

package org.example;
public class Main {
    public static void main(String[] args) {
        ActividadClase1 actividad = new ActividadClase1();

        int[] numeros = {1, 2, 3, 4, 5};
        int suma = actividad.sumar(numeros);
        System.out.println("suma = " + suma);

        actividad.promedio();

    }
}
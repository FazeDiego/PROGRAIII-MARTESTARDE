package gamescore;

/**
 * Representa un jugador en el sistema GameScore.
 * Almacena el nombre del jugador y su puntaje.
 */
public class Jugador implements Comparable<Jugador> {
    private String nombre;
    private int puntaje;

    public Jugador(String nombre, int puntaje) {
        this.nombre = nombre;
        this.puntaje = puntaje;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntaje() {
        return puntaje;
    }

    @Override
    public int compareTo(Jugador otro) {
        // Primero comparamos por puntaje (orden descendente)
        int comparacionPuntaje = Integer.compare(otro.puntaje, this.puntaje);
        if (comparacionPuntaje != 0) {
            return comparacionPuntaje;
        }
        // Si tienen el mismo puntaje, ordenamos por nombre (orden ascendente)
        return this.nombre.compareTo(otro.nombre);
    }

    @Override
    public String toString() {
        return nombre + " (" + puntaje + " pts)";
    }
}
package gamescore;

/**
 * Programa de prueba para el sistema GameScore
 * Demuestra la búsqueda de los k mejores jugadores en un rango de puntajes
 */
public class MainGameScore {
    public static void main(String[] args) {
        // Crear árbol de puntajes
        GameScoreAVL ranking = new GameScoreAVL();
        
        // Insertar algunos jugadores de prueba
        ranking.insertar(new Jugador("Alice", 1000));
        ranking.insertar(new Jugador("Bob", 850));
        ranking.insertar(new Jugador("Charlie", 920));
        ranking.insertar(new Jugador("David", 750));
        ranking.insertar(new Jugador("Eve", 890));
        ranking.insertar(new Jugador("Frank", 980));
        ranking.insertar(new Jugador("Grace", 800));
        ranking.insertar(new Jugador("Henry", 930));

        // Prueba 1: Buscar los 3 mejores jugadores con puntaje entre 800 y 950
        System.out.println("=== Top 3 jugadores entre 800 y 950 puntos ===");
        var resultado1 = ranking.findInRange(800, 950, 3);
        for (var jugador : resultado1) {
            System.out.println(jugador);
        }

        // Prueba 2: Buscar los 2 mejores jugadores con puntaje entre 900 y 1000
        System.out.println("\n=== Top 2 jugadores entre 900 y 1000 puntos ===");
        var resultado2 = ranking.findInRange(900, 1000, 2);
        for (var jugador : resultado2) {
            System.out.println(jugador);
        }

        // Prueba 3: Buscar el mejor jugador con puntaje entre 700 y 800
        System.out.println("\n=== Top 1 jugador entre 700 y 800 puntos ===");
        var resultado3 = ranking.findInRange(700, 800, 1);
        for (var jugador : resultado3) {
            System.out.println(jugador);
        }
    }
}
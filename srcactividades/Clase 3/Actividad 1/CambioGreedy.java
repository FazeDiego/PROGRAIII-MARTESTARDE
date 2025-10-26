//package Clase3.Actividad 1;
import java.util.*;


public class CambioGreedy {

    /** Resultado: si se pudo y qué monedas se usaron (si se pudo) */
    public static class Resultado {
        public final boolean posible;
        public final List<Integer> usadas;

        public Resultado(boolean posible, List<Integer> usadas) {
            this.posible = posible;
            this.usadas = usadas;
        }
    }

    /**
     * Greedy con stock limitado:
     * - monedasDisponibles: lista real de monedas (p.ej. [25,10,10,5,1,1,1])
     * - monto: objetivo de cambio
     * Retorna si fue posible y la lista de monedas usadas (si posible).
     */
    public static Resultado puedeDarCambioGreedy(List<Integer> monedasDisponibles, int monto) {
        if (monto < 0) throw new IllegalArgumentException("Monto negativo");
        if (monto == 0) return new Resultado(true, new ArrayList<>());

        // 1) contar stock por denominación
        Map<Integer, Integer> stock = new HashMap<>();
        for (int m : monedasDisponibles) {
            stock.merge(m, 1, Integer::sum);
        }

        // 2) ordenar denominaciones de mayor a menor
        List<Integer> denoms = new ArrayList<>(stock.keySet());
        denoms.sort(Collections.reverseOrder());

        // 3) seleccionar greedy respetando stock
        List<Integer> usadas = new ArrayList<>();
        int restante = monto;

        for (int d : denoms) {
            int disponibles = stock.get(d);
            int maxQueSirven = restante / d;
            int tomar = Math.min(disponibles, maxQueSirven);

            for (int i = 0; i < tomar; i++) {
                usadas.add(d);
                restante -= d;
            }
            if (restante == 0) {
                return new Resultado(true, usadas);
            }
        }

        // Greedy no logró exacto (puede que no exista o que greedy no lo encuentre)
        return new Resultado(false, usadas);
    }

    // Demo 
    public static void main(String[] args) {
        // Caso 1: se puede (convencional, hay unidades de 1)
        List<Integer> stock1 = Arrays.asList(25, 10, 10, 5, 1, 1, 1);
        int monto1 = 36;
        Resultado r1 = puedeDarCambioGreedy(stock1, monto1);
        System.out.println("Caso 1 -> posible=" + r1.posible + " usadas=" + r1.usadas);

        // Caso 2: no alcanza el stock de 1, falla
        List<Integer> stock2 = Arrays.asList(25, 10, 1); // solo un '1'
        int monto2 = 12; // con greedy: 10 + 1 = 11, falta 1 y no hay más '1'
        Resultado r2 = puedeDarCambioGreedy(stock2, monto2);
        System.out.println("Caso 2 -> posible=" + r2.posible + " usadas=" + r2.usadas);

        // Caso 3: ejemplo donde greedy puede fallar aun existiendo combinación
        // Denominaciones {1,3,4}, monto 6 y stock = [3,3] (dos de 3) SIN 1s
        // Greedy toma 4 y queda 2 (imposible). Pero 3+3 sí suma 6.
        List<Integer> stock3 = Arrays.asList(4, 3, 3);
        int monto3 = 6;
        Resultado r3 = puedeDarCambioGreedy(stock3, monto3);
        System.out.println("Caso 3 -> posible=" + r3.posible + " usadas=" + r3.usadas + "  (ojo: greedy)");
    }
}

// Complejidad algoritmica 
/* n = cantidad total de monedas en la lista, osea el stock.
    k = cantidad de denominaciones distintas.

    Como cuenta el stock de cada denominacion en un mapa, la complejidad de esa parte es O(n).
    Luego ordena las denominaciones, lo cual tiene una complejidad de O(k log k).
 *  Se da en el peor de los casos O(n) cuando usas cada moneda.
 * 
 * 
*/

// pseudo codigo
/* func puedeDarCambioGreedy(monedasDisponibles, monto):
    if monto == 0: return (true, [])

    conteo := mapa<denominacion, cantidad>
    para cada m en monedasDisponibles:
        conteo[m]++
    
    denoms := claves(conteo)
    ordenarDesc(denoms)

    usadas := []

    para cada d en denoms:
        maxQueSirven := piso(monto / d)
        tomar := min(maxQueSirven, conteo[d])

        repetir tomar veces:
            usadas.agregar(d)
            monto := monto - d
        

        si monto == 0:
            return (true, usadas)  
    

    retornar (false, usadas) 

*/
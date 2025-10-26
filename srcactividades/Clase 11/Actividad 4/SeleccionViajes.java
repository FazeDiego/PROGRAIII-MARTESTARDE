import java.util.*;

public class SeleccionViajes {

    static class Arista {
        int destino, costo;
        Arista(int destino, int costo) {
            this.destino = destino;
            this.costo = costo;
        }
    }

    static class Grafo {
        Map<Integer, List<Arista>> adj = new HashMap<>();

        void agregarRuta(int origen, int destino, int costo) {
            adj.computeIfAbsent(origen, k -> new ArrayList<>()).add(new Arista(destino, costo));
            // si las rutas son bidireccionales (ida y vuelta), agregar la inversa:
            // adj.computeIfAbsent(destino, k -> new ArrayList<>()).add(new Arista(origen, costo));
        }

        /** Algoritmo de Dijkstra */
        Map<Integer, Integer> dijkstra(int origen) {
            Map<Integer, Integer> dist = new HashMap<>();
            for (int v : adj.keySet()) dist.put(v, Integer.MAX_VALUE);
            dist.put(origen, 0);

            PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
            pq.add(new int[]{origen, 0});

            while (!pq.isEmpty()) {
                int[] cur = pq.poll();
                int u = cur[0], d = cur[1];
                if (d > dist.get(u)) continue;

                for (Arista e : adj.getOrDefault(u, List.of())) {
                    int nuevo = dist.get(u) + e.costo;
                    if (nuevo < dist.getOrDefault(e.destino, Integer.MAX_VALUE)) {
                        dist.put(e.destino, nuevo);
                        pq.add(new int[]{e.destino, nuevo});
                    }
                }
            }
            return dist;
        }
    }

    public static void main(String[] args) {
        Grafo g = new Grafo();

        // Ejemplo de conexiones (pueden representarse con IDs o nombres)
        g.agregarRuta(0, 1, 100); // A -> B
        g.agregarRuta(0, 2, 300); // A -> C
        g.agregarRuta(1, 2, 50);  // B -> C
        g.agregarRuta(1, 3, 200); // B -> D
        g.agregarRuta(2, 3, 100); // C -> D

        int origen = 0; // A
        int destino = 3; // D

        Map<Integer, Integer> dist = g.dijkstra(origen);
        System.out.println("Costo mínimo de " + origen + " a " + destino + " = " + dist.get(destino));
    }
}

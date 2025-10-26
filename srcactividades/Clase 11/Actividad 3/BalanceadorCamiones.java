////package Clase 11.Actividad 3;
    
import java.util.*;
public class BalanceadorCamiones {


/** Distribución de cargas en m camiones idénticos: minimizar el camión más cargado. */

    /** Resultado con asignación y makespan. */
    public static class Result {
        public final List<List<Integer>> asignacion; // por camión: índices de paquetes
        public final int[] cargas;                    // carga por camión
        public final int makespan;
        public Result(List<List<Integer>> asignacion, int[] cargas) {
            this.asignacion = asignacion;
            this.cargas = cargas;
            int mx = 0; for (int v : cargas) mx = Math.max(mx, v);
            this.makespan = mx;
        }
        @Override public String toString() {
            StringBuilder sb = new StringBuilder("Makespan = " + makespan + "\n");
            for (int k = 0; k < asignacion.size(); k++) {
                sb.append("Camión ").append(k).append(" (")
                  .append(cargas[k]).append(" kg): ").append(asignacion.get(k)).append('\n');
            }
            return sb.toString();
        }
    }

    /* ===================== 1) Greedy LPT ===================== */
public static Result lpt(int[] w, int m) {
    int n = w.length;
    Integer[] idx = new Integer[n];
    for (int i = 0; i < n; i++) idx[i] = i;
    Arrays.sort(idx, (a, b) -> Integer.compare(w[b], w[a])); // desc por peso

    // Sencillo: clase con constructor
    class Truck {
        int id, load;
        Truck(int id) { this.id = id; this.load = 0; }
    }

    PriorityQueue<Truck> pq = new PriorityQueue<>(Comparator.comparingInt(t -> t.load));
    for (int k = 0; k < m; k++) pq.add(new Truck(k));   // <- ya no hay captura de 'k'

    List<List<Integer>> asg = new ArrayList<>();
    for (int k = 0; k < m; k++) asg.add(new ArrayList<>());
    int[] cargas = new int[m];

    for (int j : idx) {
        Truck t = pq.poll();
        asg.get(t.id).add(j);
        t.load += w[j];
        cargas[t.id] = t.load;
        pq.add(t);
    }
    return new Result(asg, cargas);
}


    /* ========== 2) Óptimo: binary search + backtracking (feasibility) ========== */

    /** Óptimo: devuelve asignación con makespan mínimo. */
    public static Result optimal(int[] weights, int m) {
        int n = weights.length;

        // ordenar paquetes desc y guardar mapeo a índices originales
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (a,b) -> Integer.compare(weights[b], weights[a]));
        int[] w = new int[n];
        int[] mapOrig = new int[n];
        int sum = 0, maxw = 0;
        for (int i = 0; i < n; i++) { w[i] = weights[idx[i]]; mapOrig[i] = idx[i]; sum += w[i]; maxw = Math.max(maxw, w[i]); }

        // cotas
        int lower = Math.max(maxw, (sum + m - 1) / m);
        int upper = lpt(weights, m).makespan;

        // búsqueda binaria sobre C (capacidad máxima por camión)
        int lo = lower, hi = upper, best = upper;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            if (canFit(w, m, mid)) { best = mid; hi = mid - 1; } else lo = mid + 1;
        }

        // construir una asignación factible con makespan = best
        List<List<Integer>> asg = new ArrayList<>();
        for (int k = 0; k < m; k++) asg.add(new ArrayList<>());
        int[] loads = new int[m];
        buildAssignment(w, mapOrig, m, best, 0, loads, asg);
        // convertir cargas reales según pesos originales
        int[] cargas = new int[m];
        for (int k = 0; k < m; k++) {
            int s = 0; for (int j : asg.get(k)) s += weights[j]; cargas[k] = s;
        }
        return new Result(asg, cargas);
    }

    /** Feasibility: ¿puedo asignar todos los paquetes sin superar cap? */
    private static boolean canFit(int[] w, int m, int cap) {
        int[] loads = new int[m];
        return dfsFit(0, w, loads, cap);
    }
    private static boolean dfsFit(int i, int[] w, int[] loads, int cap) {
        if (i == w.length) return true;
        HashSet<Integer> tried = new HashSet<>(); // evitar probar mismos loads (simetría)
        for (int k = 0; k < loads.length; k++) {
            if (loads[k] + w[i] > cap) continue;
            if (!tried.add(loads[k])) continue;

            loads[k] += w[i];
            if (dfsFit(i + 1, w, loads, cap)) return true;
            loads[k] -= w[i];

            // poda: si el camión estaba vacío y no sirvió, no probar otros vacíos
            if (loads[k] == 0) break;
        }
        return false;
    }

    /** Construye una asignación concreta que cumpla cap (se asume que existe). */
    private static boolean buildAssignment(int[] w, int[] mapOrig, int m, int cap, int i,
                                           int[] loads, List<List<Integer>> asg) {
        if (i == w.length) return true;
        HashSet<Integer> tried = new HashSet<>();
        for (int k = 0; k < m; k++) {
            if (loads[k] + w[i] > cap) continue;
            if (!tried.add(loads[k])) continue;

            loads[k] += w[i];
            asg.get(k).add(mapOrig[i]); // usamos índices originales
            if (buildAssignment(w, mapOrig, m, cap, i + 1, loads, asg)) return true;
            asg.get(k).remove(asg.get(k).size() - 1);
            loads[k] -= w[i];

            if (loads[k] == 0) break;
        }
        return false;
    }

    /* ===================== Demo ===================== */
    public static void main(String[] args) {
        // ejemplo simple
        int[] paquetes = {8, 7, 6, 5, 4, 3}; // pesos
        int m = 3;                           // camiones idénticos

        System.out.println("Greedy LPT:");
        System.out.println(lpt(paquetes, m));

        System.out.println("Óptimo (binary search + backtracking):");
        System.out.println(optimal(paquetes, m));
    }
}
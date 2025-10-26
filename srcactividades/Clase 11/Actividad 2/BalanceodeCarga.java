//package Clase 11.Actividad 2;
import java.util.*;



/** Asignación de proyectos a empleados idénticos */

public class BalanceodeCarga {
    
    /** Resultado con asignaciones y cargas. */
    public static class Result {
        public final List<List<Integer>> asignacion; // por empleado: índices de trabajos
        public final int[] cargas;                    // horas por empleado
        public final int makespan;                   // max(cargas)

        public Result(List<List<Integer>> asignacion, int[] cargas) {
            this.asignacion = asignacion;
            this.cargas = cargas;
            int mx = 0; for (int v : cargas) mx = Math.max(mx, v);
            this.makespan = mx;
        }

        @Override public String toString() {
            StringBuilder sb = new StringBuilder("Makespan = " + makespan + "\n");
            for (int k = 0; k < asignacion.size(); k++) {
                sb.append("Empleado ").append(k).append(" (")
                  .append(cargas[k]).append(" hs): ").append(asignacion.get(k)).append('\n');
            }
            return sb.toString();
        }
    }

    /* ===================== LPT Greedy ===================== */
    public static Result lpt(int[] horas, int m) {
        int n = horas.length;
        // trabajos ordenados por duración desc, guardamos (duración, índiceOriginal)
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (a,b) -> Integer.compare(horas[b], horas[a]));

        // min-heap de empleados por carga actual
        class Emp { int id, load; }
        PriorityQueue<Emp> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.load));
        for (int k = 0; k < m; k++) { Emp e = new Emp(); e.id = k; e.load = 0; pq.add(e); }

        List<List<Integer>> asign = new ArrayList<>();
        for (int k = 0; k < m; k++) asign.add(new ArrayList<>());
        int[] cargas = new int[m];

        for (int j : idx) {
            Emp e = pq.poll();
            asign.get(e.id).add(j);
            e.load += horas[j];
            cargas[e.id] = e.load;
            pq.add(e);
        }
        return new Result(asign, cargas);
    }

    /* ===================== Branch & Bound (óptimo) ===================== */
    public static Result branchAndBound(int[] horas, int m) {
        int n = horas.length;

        // ordenar desc y recordar mapeo a índices originales
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (a,b) -> Integer.compare(horas[b], horas[a]));
        int[] jobs = new int[n];    // duraciones reordenadas
        int[] mapOrig = new int[n]; // idx->original
        for (int i = 0; i < n; i++) { jobs[i] = horas[idx[i]]; mapOrig[i] = idx[i]; }

        // cota superior inicial: LPT
        Result seed = lpt(horas, m);
        int best = seed.makespan;
        List<List<Integer>> bestAssign = deepCopy(seed.asignacion);

        int[] loads = new int[m];
        List<List<Integer>> currAssign = new ArrayList<>();
        for (int k = 0; k < m; k++) currAssign.add(new ArrayList<>());

        // suma total (cota inferior simple: ceil(sum/m))
        int sum = 0; for (int v: jobs) sum += v;
        int lowerBound = (sum + m - 1) / m;

        best = Math.max(lowerBound, best);

        backtrack(0, jobs, mapOrig, loads, currAssign, m, new int[]{best}, bestAssign);

        return new Result(bestAssign, loadsOf(bestAssign, horas, m));
    }

    private static void backtrack(int i, int[] jobs, int[] mapOrig, int[] loads,
                                  List<List<Integer>> currAssign, int m,
                                  int[] bestRef, List<List<Integer>> bestAssign) {
        int n = jobs.length;
        if (i == n) {
            int mk = 0; for (int v : loads) mk = Math.max(mk, v);
            if (mk < bestRef[0]) {
                bestRef[0] = mk;
                for (int k = 0; k < m; k++) {
                    bestAssign.get(k).clear();
                    bestAssign.get(k).addAll(currAssign.get(k));
                }
            }
            return;
        }

        // Poda por simetría: evitar probar varios empleados con la misma carga actual
        HashSet<Integer> cargasVistas = new HashSet<>();

        for (int k = 0; k < m; k++) {
            if (!cargasVistas.add(loads[k])) continue; // misma carga ya probada en este nivel

            // asignar job i al empleado k
            loads[k] += jobs[i];
            int mk = 0; for (int v : loads) mk = Math.max(mk, v);
            if (mk < bestRef[0]) { // poda por cota superior
                currAssign.get(k).add(mapOrig[i]);
                backtrack(i + 1, jobs, mapOrig, loads, currAssign, m, bestRef, bestAssign);
                currAssign.get(k).remove(currAssign.get(k).size() - 1);
            }
            loads[k] -= jobs[i];

            // Pequeña poda adicional: si el empleado estaba vacío y no lo usamos, no pongamos este trabajo
            // en otros empleados vacíos idénticos (lo cubre la simetría de cargasVistas).
        }
    }

    private static int[] loadsOf(List<List<Integer>> assign, int[] horas, int m) {
        int[] loads = new int[m];
        for (int k = 0; k < m; k++) {
            int s = 0; for (int j : assign.get(k)) s += horas[j];
            loads[k] = s;
        }
        return loads;
    }

    private static List<List<Integer>> deepCopy(List<List<Integer>> src) {
        List<List<Integer>> dst = new ArrayList<>();
        for (List<Integer> l : src) dst.add(new ArrayList<>(l));
        return dst;
    }

    /* ===================== Demo ===================== */
    public static void main(String[] args) {
        // Ejemplo
        int[] horas = {6, 7, 3, 4, 5, 9}; // 6 proyectos
        int m = 3;                        // 3 empleados

        System.out.println("---- LPT (heurístico) ----");
        Result r1 = lpt(horas, m);
        System.out.println(r1);

        System.out.println("---- Branch & Bound (óptimo) ----");
        Result r2 = branchAndBound(horas, m);
        System.out.println(r2);
    }
}

//package Clase 3.Actividad 2;
import java.util.*;

/**
 * Objetivo: minimizar la cantidad de comprobantes usados.
 * Se proveen dos variantes:
 *   A) ilimitados por tipo (unbounded)
 *   B) únicos (0/1, cada comprobante se usa a lo sumo una vez)
 */
public class CambioComprobantes {

    public static class Resultado {
        public final boolean posible;
        public final List<Integer> combinacion; // valores elegidos (repetidos o no según el caso)
        public final int cantidad;

        public Resultado(boolean posible, List<Integer> comb) {
            this.posible = posible;
            this.combinacion = comb;
            this.cantidad = comb.size();
        }

        @Override public String toString() {
            return "posible=" + posible + ", cantidad=" + cantidad + ", comb=" + combinacion;
        }
    }

    /** ===== Caso A: ilimitados por tipo (unbounded coin change, min #comprobantes) ===== */
    public static Resultado minComprobantesIlimitados(int[] valores, int monto) {
        final int INF = 1_000_000_000;
        int[] dp = new int[monto + 1];
        int[] take = new int[monto + 1]; // qué valor tomé por última vez para llegar a a
        Arrays.fill(dp, INF);
        Arrays.fill(take, -1);
        dp[0] = 0;

        // Unbounded: recorrer montos de v..monto
        for (int v : valores) {
            for (int a = v; a <= monto; a++) {
                if (dp[a - v] + 1 < dp[a]) {
                    dp[a] = dp[a - v] + 1;
                    take[a] = v;
                }
            }
        }

        if (dp[monto] >= INF) return new Resultado(false, new ArrayList<>());

        // reconstrucción
        List<Integer> comb = new ArrayList<>();
        int x = monto;
        while (x > 0) {
            int v = take[x];
            comb.add(v);
            x -= v;
        }
        return new Resultado(true, comb);
    }

    /** ===== Caso B: únicos (0/1, cada comprobante a lo sumo una vez) ===== */
    public static Resultado minComprobantesUnicos(int[] valores, int monto) {
        final int INF = 1_000_000_000;
        int[] dp = new int[monto + 1];
        int[] prevIdx = new int[monto + 1]; // índice del comprobante usado para llegar a 'a'
        int[] fromSum = new int[monto + 1]; // suma anterior
        Arrays.fill(dp, INF);
        Arrays.fill(prevIdx, -1);
        Arrays.fill(fromSum, -1);
        dp[0] = 0;

        // 0/1: recorrer montos hacia atrás para no reutilizar el mismo comprobante
        for (int i = 0; i < valores.length; i++) {
            int v = valores[i];
            for (int a = monto; a >= v; a--) {
                if (dp[a - v] + 1 < dp[a]) {
                    dp[a] = dp[a - v] + 1;
                    prevIdx[a] = i;
                    fromSum[a] = a - v;
                }
            }
        }

        if (dp[monto] >= INF) return new Resultado(false, new ArrayList<>());

        // reconstrucción
        List<Integer> comb = new ArrayList<>();
        int x = monto;
        while (x > 0) {
            int i = prevIdx[x];
            comb.add(valores[i]);
            x = fromSum[x];
        }
        return new Resultado(true, comb);
    }

    // Demo 
    public static void main(String[] args) {
        // Caso A (ilimitados): valores arbitrarios (no canónicos)
        int[] tipos = {7, 5, 3}; // cheques/bonos/monedas
        int monto = 17;
        System.out.println("Ilimitados -> " + minComprobantesIlimitados(tipos, monto));
        // ej. 7+5+5 (3 comprobantes)

        // Caso B (únicos): cada comprobante está una sola vez
        int[] comprobantesUnicos = {10, 7, 7, 5, 3}; // documentos distintos
        int monto2 = 14;
        System.out.println("Únicos -> " + minComprobantesUnicos(comprobantesUnicos, monto2));
        // ej. 7+7 (2 comprobantes)
    }
}


/* pseudo codigo para caso A (ilimitados)
func minComprobantesIlimitados(valores[], monto):
    INF := +infinito
    dp[0..monto] := INF
    take[0..monto] := -1
    dp[0] := 0

    para v en valores:
        para a desde v hasta monto:
            si dp[a - v] + 1 < dp[a]:
                dp[a] := dp[a - v] + 1
                take[a] := v

    si dp[monto] == INF: retornar (false, [])
    // reconstrucción
    comb := []
    x := monto
    mientras x > 0:
        comb.agregar(take[x])
        x := x - take[x]
    retornar (true, comb)   // comb puede repetirse por tipo

 * 
 * 
 * pseudo codigo para caso B (únicos), cuando los comprobantes se usan 1 o 0 veces
 * func minComprobantesUnicos(valores[], monto):
    INF := +infinito
    dp[0..monto] := INF
    prev[0..monto] := -1      // índice del comprobante usado
    fromSum[0..monto] := -1   // suma anterior
    dp[0] := 0

    para i en 0..n-1:              // cada comprobante una sola vez
        v := valores[i]
        para a desde monto hasta v: // recorrer hacia atrás (0/1)
            si dp[a - v] + 1 < dp[a]:
                dp[a] := dp[a - v] + 1
                prev[a] := i
                fromSum[a] := a - v

    si dp[monto] == INF: retornar (false, [])
    // reconstrucción (índices únicos)
    comb := []
    x := monto
    mientras x > 0:
        comb.agregar(valores[prev[x]])
        x := fromSum[x]
    retornar (true, comb)

 * 
 * 
 */
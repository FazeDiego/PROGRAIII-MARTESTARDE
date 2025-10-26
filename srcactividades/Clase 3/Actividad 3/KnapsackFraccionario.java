//package Clase 3.Actividad 3;
import java.util.*;

/** Actividad 3: Knapsack fraccionario (Greedy por valor/peso) */
public class KnapsackFraccionario {

    public static class Item {
        public final String nombre;
        public final double peso;
        public final double valor;
        public final double ratio; // valor / peso

        public Item(String nombre, double peso, double valor) {
            if (peso <= 0) throw new IllegalArgumentException("Peso debe ser > 0");
            this.nombre = nombre;
            this.peso = peso;
            this.valor = valor;
            this.ratio = valor / peso;
        }

        @Override public String toString() {
            return nombre + " (peso=" + peso + ", valor=" + valor + ", ratio=" + ratio + ")";
        }
    }

    public static class Seleccion {
        public final Item item;
        public final double fraccion;   // entre 0 y 1
        public final double pesoTomado; // fraccion * item.peso
        public final double valorAportado;

        public Seleccion(Item item, double fraccion) {
            this.item = item;
            this.fraccion = fraccion;
            this.pesoTomado = fraccion * item.peso;
            this.valorAportado = fraccion * item.valor;
        }

        @Override public String toString() {
            String f = (fraccion == 1.0) ? "entero" : String.format(Locale.US, "fracción=%.4f", fraccion);
            return item.nombre + " -> " + f +
                   String.format(Locale.US, " (peso=%.2f, valor=%.2f)", pesoTomado, valorAportado);
        }
    }

    public static class Resultado {
        public final double valorTotal;
        public final double pesoTotal;
        public final List<Seleccion> seleccion;

        public Resultado(double valorTotal, double pesoTotal, List<Seleccion> seleccion) {
            this.valorTotal = valorTotal;
            this.pesoTotal = pesoTotal;
            this.seleccion = seleccion;
        }

        @Override public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(Locale.US, "valorTotal=%.2f, pesoTotal=%.2f\n", valorTotal, pesoTotal));
            for (Seleccion s : seleccion) sb.append("  - ").append(s).append("\n");
            return sb.toString();
        }
    }

    /** Greedy óptimo para fraccionario */
    public static Resultado maximizarValor(double capacidad, List<Item> items) {
        if (capacidad <= 0) return new Resultado(0, 0, List.of());

        // ordenar por valor específico (desc)
        List<Item> ordenados = new ArrayList<>(items);
        ordenados.sort((a, b) -> Double.compare(b.ratio, a.ratio));

        double restante = capacidad;
        double valorTotal = 0;
        double pesoTotal = 0;
        List<Seleccion> res = new ArrayList<>();

        for (Item it : ordenados) {
            if (restante <= 0) break;

            double tomarPeso = Math.min(it.peso, restante);
            double fraccion = tomarPeso / it.peso;

            Seleccion s = new Seleccion(it, fraccion);
            res.add(s);

            restante -= tomarPeso;
            pesoTotal += s.pesoTomado;
            valorTotal += s.valorAportado;
        }

        return new Resultado(valorTotal, pesoTotal, res);
    }

    // Demo
    public static void main(String[] args) {
        List<Item> items = List.of(
            new Item("Cobre",   10, 60),
            new Item("Plata",   20, 100),
            new Item("Oro",     30, 120),
            new Item("Litio",    5,  55)
        );
        double capacidad = 35; // capacidad del camión

        Resultado r = maximizarValor(capacidad, items);
        System.out.println(r);
    }
}

//*  pseudocódigo Knapsack Fraccionario

//func knapsackFraccionario(items, capacidad):
//  para cada item en items:
//        item.ratio = item.valor / item.peso
//    ordenar(items, por ratio desc)

//    totalValor := 0
//    seleccion := []

//    para item en items mientras capacidad > 0:
//        if item.peso <= capacidad:
//            tomarPeso = item.peso
//            fraccion = 1
//        else:
//            tomarPeso = capacidad
//            fraccion = capacidad / item.peso

//        totalValor += item.valor * fraccion
//        capacidad   -= tomarPeso
//        agregar(seleccion, (item, fraccion, tomarPeso))

//    retornar (totalValor, seleccion) 
    
    //*

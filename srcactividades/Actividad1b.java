/*
 * Actividad 1.1 b) .java 6/8/2025 Martinez Diego Lu: 1140019 
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Actividad1b {

    static class Factura {
        int idFactura;
        int idCliente;
        double importe;
        // constructor...
        public Factura(int idFactura, int idCliente, double importe) {
            this.idFactura = idFactura;
            this.idCliente = idCliente;
            this.importe = importe;
        }
    }

    static class Cliente {
        int idCliente;
        String nombre;
        // constructor...
        public Cliente(int idCliente, String nombre) {
            this.idCliente = idCliente;
            this.nombre = nombre;
        }
    }

    static class Resultado {
        int idCliente;
        String nombre;
        double sumaImportes;
        // constructor...
        public Resultado(int idCliente, String nombre, double sumaImportes) {
            this.idCliente = idCliente;
            this.nombre = nombre;
            this.sumaImportes = sumaImportes;
        }
        @Override
        public String toString() {
            return "Resultado{idCliente=" + idCliente + ", nombre='" + nombre + "', sumaImportes=" + sumaImportes + "}";
        }
    }

    public static List<Resultado> sumarFacturasSinMap(List<Factura> facturas, List<Cliente> clientes) {
        List<Resultado> resultado = new ArrayList<>(); // O(1)
        for (Cliente cliente : clientes) { // O(n)
            double suma = 0; // O(1)
            for (Factura factura : facturas) { // O(m)
                if (factura.idCliente == cliente.idCliente) { // O(1)
                    suma += factura.importe; // O(1)
                }
            }
            resultado.add(new Resultado(cliente.idCliente, cliente.nombre, suma)); // O(1)
        }
        return resultado; // O(1)
        // Complejidad temporal total: O(n*m) (n = clientes, m = facturas)
    }
    /*
    La complejidad temporal del metodo SinMap se aproxima a un O(n**2) 
    Este metodo es mas eficiente para casos en donde hay pocos clientes y muchas facturas
    evitando estructuras de datos extras.
     */


    public static List<Resultado> sumarFacturasConMap(List<Factura> facturas, List<Cliente> clientes) {
        Map<Integer, Double> sumaPorCliente = new HashMap<>(); // O(1)
        for (Factura factura : facturas) { // O(m)
            sumaPorCliente.put(factura.idCliente,
                sumaPorCliente.getOrDefault(factura.idCliente, 0.0) + factura.importe); // O(1) promedio
        }
        List<Resultado> resultado = new ArrayList<>(); // O(1)
        for (Cliente cliente : clientes) { // O(n)
            double suma = sumaPorCliente.getOrDefault(cliente.idCliente, 0.0); // O(1) promedio
            resultado.add(new Resultado(cliente.idCliente, cliente.nombre, suma)); // O(1)
        }
        return resultado; // O(1)
        // Complejidad temporal total: O(n + m)
    }

    public static void main(String[] args) {
        // Example usage
        List<Factura> facturas = Arrays.asList(
            new Factura(1, 1, 100.0),
            new Factura(2, 2, 200.0),
            new Factura(3, 1, 150.0)
        );
        List<Cliente> clientes = Arrays.asList(
            new Cliente(1, "Juan"),
            new Cliente(2, "Ana")
        );
        System.out.println("Sin Map: " + sumarFacturasSinMap(facturas, clientes));
        System.out.println("Con Map: " + sumarFacturasConMap(facturas, clientes));
    }
}
/*
La complejidad temporal del metodo ConMap se aproxima a un O(n + m) 
este metodo es eficiente para casos en donde hay muchos clientes y pocas facturas,
osea, un gran volumen de datos con el fin de mejorar el rendimiento.
*/
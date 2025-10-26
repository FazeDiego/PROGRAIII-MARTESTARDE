//Metodo de calcular altura
//Diego Martinez
//Actividad 1

public class CalcularAltura {
    
    /**
     * Calcula la altura de un árbol binario usando el paradigma de Dividir y Vencerás (Divide and Conquer).
     * 
     * Este método implementa una solución recursiva donde:
     * 1. DIVIDIR: Se divide el problema en subproblemas más pequeños (subárboles izquierdo y derecho)
     * 2. VENCER: Se resuelve cada subproblema recursivamente
     * 3. COMBINAR: Se toma el máximo de las alturas y se añade 1 por el nodo actual
     * 
     * No se usa Programación Dinámica aquí porque:
     * - Cada nodo se visita exactamente una vez
     * - No hay superposición de subproblemas
     * - No necesitamos memorización
     * 
     * Complejidad para árbol balanceado: O(log n)
     * - En cada nivel, dividimos el problema en 2 subproblemas más pequeños
     * - La recurrencia es: T(n) = T(n/2) + c
     * - Por el método de sustitución:
     *   T(n) = c log n
     * 
     * Complejidad para árbol desbalanceado: O(n)
     * - En el peor caso (árbol completamente desbalanceado)
     * - La recurrencia es: T(n) = T(n-1) + c
     * - Por el método de sustitución:
     *   T(n) = cn = O(n)
     * 
     * @param raiz Nodo raíz del árbol o subárbol
     * @return Altura del árbol
     */
    public static <T extends Comparable<T>> int getHeight(AVL.Nodo<T> raiz) {
        // Caso base: si el nodo es nulo, la altura es 0
        if (raiz == null) {
            return 0;
        }
        
        // Dividir: calculamos la altura de los subárboles izquierdo y derecho
        int alturaIzquierda = getHeight(raiz.izquierdo);
        int alturaDerecha = getHeight(raiz.derecho);
        
        // Vencer: tomamos la altura máxima entre los subárboles y añadimos 1 por el nodo actual
        return Math.max(alturaIzquierda, alturaDerecha) + 1;
    }
    
    /**
     * Método auxiliar para imprimir la altura de un árbol
     */
    /**
     * Método auxiliar para imprimir la altura de un árbol
     * Utiliza getHeight internamente y formatea la salida
     */
    public static <T extends Comparable<T>> void imprimirAltura(AVL<T> arbol) {
        System.out.println("Altura del árbol: " + getHeight(arbol.getRoot()));
    }
}

/**
 * Programa de prueba para el método getHeight y la clase AVL
 * Demuestra:
 * 1. Cálculo de altura en árbol vacío
 * 2. Árbol AVL balanceado (con rotaciones automáticas)
 * 3. Árbol con inserción secuencial para ver comportamiento balanceado
 */
public class MainCalcularAltura {
    public static void main(String[] args) {
        System.out.println("=== Prueba 1: árbol vacío ===");
        AVL<Integer> arbolVacio = new AVL<>();
        System.out.println("Altura usando getHeight: " + CalcularAltura.getHeight(arbolVacio.getRoot()));

        System.out.println("\n=== Prueba 2: insertar 10,20,30,40,50,25 (balanceado tras rotaciones) ===");
        AVL<Integer> arbol = new AVL<>();
        int[] valores = {10, 20, 30, 40, 50, 25};
        for (int v : valores) {
            arbol.insertar(v);
        }
        System.out.println("Recorrido inOrder (muestra balance):");
        arbol.inOrder();
        System.out.println("Altura usando getHeight: " + CalcularAltura.getHeight(arbol.getRoot()));

        System.out.println("\n=== Prueba 3: árbol con inserción 1..10 ===");
        AVL<Integer> arbol2 = new AVL<>();
        for (int i = 1; i <= 10; i++) {
            arbol2.insertar(i);
        }
        System.out.println("Recorrido inOrder (muestra balance):");
        arbol2.inOrder();
        System.out.println("Altura usando getHeight: " + CalcularAltura.getHeight(arbol2.getRoot()));
    }
}
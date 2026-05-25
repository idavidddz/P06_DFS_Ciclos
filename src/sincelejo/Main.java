package sincelejo;

import sincelejo.grafo.GrafoSincelejo;

public class Main {

    public static void main(String[] args) {
        GrafoSincelejo grafo = new GrafoSincelejo();

        try {
            grafo.cargarDesdeCSV("sincelejo.csv");
            System.out.println("Grafo cargado correctamente.");
            System.out.println("Nodos: 12  |  Aristas: 19  |  Bidireccional\n");
        } catch (Exception e) {
            System.out.println("ERROR al cargar el CSV: " + e.getMessage());
            return;
        }

        grafo.mostrarAdyacencia();
        grafo.ejecutarDFS();
        grafo.hasCycle();
        grafo.connectedComponents();
    }
}
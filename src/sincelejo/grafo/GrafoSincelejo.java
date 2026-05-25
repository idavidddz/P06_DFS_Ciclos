package sincelejo.grafo;

import sincelejo.modelo.Arista;

import java.io.*;
import java.util.*;

public class GrafoSincelejo {

    private Map<String, List<Arista>> adyacencia = new LinkedHashMap<>();
    private Map<String, String> nombres = new LinkedHashMap<>();

    private static final String BLANCO = "BLANCO";
    private static final String GRIS   = "GRIS";
    private static final String NEGRO  = "NEGRO";

    private Map<String, String> color = new HashMap<>();
    private boolean cicloDetectado = false;

    private void cargarNodos() {
        agregarNodo("N00", "UAJS Sede E");
        agregarNodo("N01", "UAJS Sede C");
        agregarNodo("N02", "UNISUCRE");
        agregarNodo("N03", "CECAR");
        agregarNodo("N04", "IPS SEYSA");
        agregarNodo("N05", "Clinica Las Americas");
        agregarNodo("N06", "Terminal de Transporte");
        agregarNodo("N07", "Mercado de Sincelejo");
        agregarNodo("N08", "Av. Alfonso Lopez");
        agregarNodo("N09", "Parque Santander");
        agregarNodo("N10", "Calle 30 / Troncal");
        agregarNodo("N11", "Barrio Puerta Roja");
    }

    private void agregarNodo(String id, String nombre) {
        nombres.put(id, nombre);
        adyacencia.putIfAbsent(id, new ArrayList<>());
    }

    public void cargarDesdeCSV(String rutaArchivo) throws IOException {
        cargarNodos();
        BufferedReader br = new BufferedReader(new FileReader(rutaArchivo));
        String linea;
        br.readLine();

        while ((linea = br.readLine()) != null) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;
            String[] partes = linea.split(",");
            String origen  = partes[0].trim();
            String destino = partes[1].trim();
            int peso       = Integer.parseInt(partes[2].trim());
            int capacidad  = Integer.parseInt(partes[3].trim());

            adyacencia.get(origen).add(new Arista(destino, peso, capacidad));
            adyacencia.get(destino).add(new Arista(origen, peso, capacidad));
        }
        br.close();
    }

    public void mostrarAdyacencia() {
        separador("1. LISTA DE ADYACENCIA");
        System.out.println("  Cada nodo muestra sus vecinos directos y el tiempo de viaje.");
        System.out.println();

        for (String nodo : adyacencia.keySet()) {
            System.out.printf("  [%s] %-25s conecta con:\n", nodo, nombres.get(nodo));
            for (Arista a : adyacencia.get(nodo)) {
                System.out.printf("         -> [%s] %-25s  %d min\n", a.destino, nombres.get(a.destino), a.peso);
            }
            System.out.println();
        }
    }

    private int paso = 0;

    private void dfs(String u) {
        paso++;
        color.put(u, GRIS);
        System.out.printf("  Paso %2d | VISITANDO  [%s] %-25s  Estado: GRIS  (en proceso)\n", paso, u, nombres.get(u));

        for (Arista arista : adyacencia.get(u)) {
            String v = arista.destino;
            if (color.get(v).equals(BLANCO)) {
                dfs(v);
            } else if (color.get(v).equals(GRIS)) {
                cicloDetectado = true;
                System.out.printf("         | CICLO      [%s] -> [%s]  (back edge detectado)\n", u, v);
            }
        }

        color.put(u, NEGRO);
        System.out.printf("         | TERMINADO  [%s] %-25s  Estado: NEGRO (completado)\n", u, nombres.get(u));
    }

    public void ejecutarDFS() {
        for (String nodo : adyacencia.keySet()) color.put(nodo, BLANCO);
        cicloDetectado = false;
        paso = 0;

        separador("2. DFS CON COLOREO  (BLANCO -> GRIS -> NEGRO)");
        System.out.println("  BLANCO = no visitado    GRIS = en proceso    NEGRO = completado");
        System.out.println();

        for (String nodo : adyacencia.keySet()) {
            if (color.get(nodo).equals(BLANCO)) {
                System.out.println("  ---- Inicio DFS desde [" + nodo + "] " + nombres.get(nodo) + " ----");
                dfs(nodo);
                System.out.println();
            }
        }
    }

    public boolean hasCycle() {
        separador("3. DETECCION DE CICLOS  ->  hasCycle()");
        System.out.println("  Un ciclo existe cuando durante el DFS se encuentra un nodo");
        System.out.println("  GRIS (en proceso), indicando un camino de regreso.");
        System.out.println();

        for (String nodo : adyacencia.keySet()) color.put(nodo, BLANCO);
        cicloDetectado = false;
        List<String> ciclosEncontrados = new ArrayList<>();

        for (String nodo : adyacencia.keySet()) {
            if (color.get(nodo).equals(BLANCO)) {
                dfsParaCiclo(nodo, null, ciclosEncontrados);
            }
        }

        if (!ciclosEncontrados.isEmpty()) {
            System.out.println("  Ciclos encontrados:");
            for (String c : ciclosEncontrados) {
                System.out.println("    " + c);
            }
            System.out.println();
            System.out.println("  RESULTADO: El grafo de Sincelejo SI tiene ciclos.");
            System.out.println("             Es bidireccional con multiples caminos entre nodos.");
        } else {
            System.out.println("  RESULTADO: El grafo NO tiene ciclos.");
        }
        return cicloDetectado;
    }

    private void dfsParaCiclo(String u, String padre, List<String> ciclosEncontrados) {
        color.put(u, GRIS);
        for (Arista arista : adyacencia.get(u)) {
            String v = arista.destino;
            if (v.equals(padre)) continue;
            if (color.get(v).equals(GRIS)) {
                cicloDetectado = true;
                ciclosEncontrados.add("[" + u + "] " + nombres.get(u) + "  <-->  [" + v + "] " + nombres.get(v));
                return;
            }
            if (color.get(v).equals(BLANCO)) {
                dfsParaCiclo(v, u, ciclosEncontrados);
            }
        }
        color.put(u, NEGRO);
    }

    public int connectedComponents() {
        separador("4. COMPONENTES CONEXAS  ->  connectedComponents()");
        System.out.println("  Una componente conexa es un grupo de nodos donde todos");
        System.out.println("  estan conectados entre si directa o indirectamente.");
        System.out.println();

        for (String nodo : adyacencia.keySet()) color.put(nodo, BLANCO);
        int contador = 0;

        for (String nodo : adyacencia.keySet()) {
            if (color.get(nodo).equals(BLANCO)) {
                contador++;
                List<String> componente = new ArrayList<>();
                dfsComponente(nodo, componente);
                System.out.printf("  Componente %d (%d nodos):\n", contador, componente.size());
                for (String n : componente) {
                    System.out.println("    - " + n);
                }
                System.out.println();
            }
        }

        System.out.println("  Total de componentes conexas: " + contador);
        if (contador == 1) {
            System.out.println("  RESULTADO: El grafo es CONEXO.");
            System.out.println("             Desde cualquier punto de Sincelejo puedes");
            System.out.println("             llegar a cualquier otro punto.");
        } else {
            System.out.println("  RESULTADO: El grafo NO es completamente conexo.");
        }
        return contador;
    }

    private void dfsComponente(String u, List<String> componente) {
        color.put(u, NEGRO);
        componente.add("[" + u + "] " + nombres.get(u));
        for (Arista arista : adyacencia.get(u)) {
            if (color.get(arista.destino).equals(BLANCO)) {
                dfsComponente(arista.destino, componente);
            }
        }
    }

    private void separador(String titulo) {
        System.out.println();
        System.out.println("======================================================");
        System.out.println("  " + titulo);
        System.out.println("======================================================");
    }
}
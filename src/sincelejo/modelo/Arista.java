package sincelejo.modelo;

public class Arista {
    public String destino;
    public int peso;
    public int capacidad;

    public Arista(String destino, int peso, int capacidad) {
        this.destino = destino;
        this.peso = peso;
        this.capacidad = capacidad;
    }
}
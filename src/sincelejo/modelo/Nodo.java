package sincelejo.modelo;

public class Nodo {
    public String id;
    public String nombre;

    public Nodo(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return id + " (" + nombre + ")";
    }
}
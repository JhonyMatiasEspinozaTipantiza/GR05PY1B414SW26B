package barberia.entidad;

import java.util.ArrayList;

public class Servicio {

    private int id;
    private String nombre;
    private double precio;
    private int duracionMin;
    private ArrayList<Cita> citas = new ArrayList<>();

    public Servicio(int id, String nombre, double precio, int duracionMin) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.duracionMin = duracionMin;
    }

    public void agregarCita(Cita cita) {
        if (!citas.contains(cita)) {
            citas.add(cita);
        }
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getDuracionMin() {
        return duracionMin;
    }

    public ArrayList<Cita> getCitas() {
        return citas;
    }

    @Override
    public String toString() {
        return nombre + "  -  $" + String.format("%.2f", precio) + "  -  " + duracionMin + " min";
    }
}

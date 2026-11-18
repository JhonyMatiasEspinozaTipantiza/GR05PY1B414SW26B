package barberia.entidad;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;

public class Barbero {

    private int id;
    private String nombre;
    private String especialidad;
    private ArrayList<Horario> horarios = new ArrayList<>();
    private ArrayList<Cita> citas = new ArrayList<>();

    public Barbero(int id, String nombre, String especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    public Horario crearHorario(DayOfWeek dia, LocalTime ini, LocalTime fin) {
        Horario nuevo = new Horario(dia, ini, fin, this);
        for (Horario h : horarios) {
            if (h.seSolapaCon(nuevo)) {
                throw new IllegalArgumentException("El horario se cruza con otro horario de " + nombre + ": " + h);
            }
        }
        horarios.add(nuevo);
        return nuevo;
    }

    public void agregarHorario(Horario horario) {
        horarios.add(horario);
    }

    public void agregarCita(Cita cita) {
        if (!citas.contains(cita)) {
            citas.add(cita);
        }
    }

    public void quitarCita(Cita cita) {
        citas.remove(cita);
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public ArrayList<Horario> getHorarios() {
        return horarios;
    }

    public ArrayList<Cita> getCitas() {
        return citas;
    }

    @Override
    public String toString() {
        return nombre + " (" + especialidad + ")";
    }
}

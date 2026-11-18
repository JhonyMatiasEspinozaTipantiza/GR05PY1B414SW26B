package barberia.entidad;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class Horario {

    private static final String[] DIAS = {"lunes", "martes", "miercoles", "jueves", "viernes", "sabado", "domingo"};

    private DayOfWeek dia;
    private LocalTime inicio;
    private LocalTime fin;
    private Barbero barbero;

    public Horario(DayOfWeek dia, LocalTime inicio, LocalTime fin, Barbero barbero) {
        this.dia = dia;
        this.inicio = inicio;
        this.fin = fin;
        this.barbero = barbero;
    }

    // la cita tiene que estar dentro de la franja y ser el mismo dia
    public boolean cubre(LocalDate fecha, LocalTime ini, LocalTime finCita) {
        return fecha.getDayOfWeek() == dia && !ini.isBefore(inicio) && !finCita.isAfter(fin);
    }

    public boolean seSolapaCon(Horario otro) {
        return dia == otro.dia && inicio.isBefore(otro.fin) && otro.inicio.isBefore(fin);
    }

    public DayOfWeek getDia() {
        return dia;
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public LocalTime getFin() {
        return fin;
    }

    public Barbero getBarbero() {
        return barbero;
    }

    public void setInicio(LocalTime inicio) {
        this.inicio = inicio;
    }

    public void setFin(LocalTime fin) {
        this.fin = fin;
    }

    @Override
    public String toString() {
        return DIAS[dia.getValue() - 1] + " " + inicio + " - " + fin;
    }
}

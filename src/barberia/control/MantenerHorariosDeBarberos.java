package barberia.control;

import barberia.entidad.Barbero;
import barberia.entidad.Horario;
import barberia.persistencia.Consultas;
import barberia.persistencia.IAlmacen;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;

public class MantenerHorariosDeBarberos {

    private Consultas consultas;
    private IAlmacen almacen;

    public MantenerHorariosDeBarberos(Consultas consultas, IAlmacen almacen) {
        this.consultas = consultas;
        this.almacen = almacen;
    }

    public ArrayList<Barbero> listarBarberos() {
        return consultas.listarBarberos();
    }

    public ArrayList<Horario> listarHorarios(int idBarbero) {
        return consultas.listarHorarios(barbero(idBarbero));
    }

    public Horario crearHorario(int idBarbero, DayOfWeek dia, LocalTime ini, LocalTime fin) {
        validar(ini, fin);
        Horario h = barbero(idBarbero).crearHorario(dia, ini, fin);
        almacen.guardarHorario(h);
        return h;
    }

    public void editarHorario(int idBarbero, DayOfWeek dia, LocalTime ini, LocalTime ini2, LocalTime fin2) {
        validar(ini2, fin2);
        Barbero b = barbero(idBarbero);
        Horario h = buscarHorario(b, dia, ini);
        Horario prueba = new Horario(dia, ini2, fin2, b);
        for (Horario otro : b.getHorarios()) {
            if (otro != h && otro.seSolapaCon(prueba)) {
                throw new IllegalArgumentException("El horario se cruza con otro horario de " + b.getNombre() + ": " + otro);
            }
        }
        h.setInicio(ini2);
        h.setFin(fin2);
        almacen.guardarHorario(h);
    }

    public void eliminarHorario(int idBarbero, DayOfWeek dia, LocalTime inicio) {
        Barbero b = barbero(idBarbero);
        almacen.eliminarHorario(buscarHorario(b, dia, inicio));
    }

    private void validar(LocalTime ini, LocalTime fin) {
        if (ini == null || fin == null) {
            throw new IllegalArgumentException("Seleccione la hora de inicio y la hora de fin.");
        }
        if (!ini.isBefore(fin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser menor que la hora de fin.");
        }
    }

    private Barbero barbero(int idBarbero) {
        Barbero b = consultas.buscarBarberoPorId(idBarbero);
        if (b == null) {
            throw new IllegalArgumentException("Seleccione un barbero.");
        }
        return b;
    }

    private Horario buscarHorario(Barbero barbero, DayOfWeek dia, LocalTime inicio) {
        for (Horario h : barbero.getHorarios()) {
            if (h.getDia() == dia && h.getInicio().equals(inicio)) {
                return h;
            }
        }
        throw new IllegalArgumentException("No existe ese horario.");
    }
}

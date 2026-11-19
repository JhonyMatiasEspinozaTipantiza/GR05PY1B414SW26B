package barberia.persistencia;

import barberia.entidad.Barbero;
import barberia.entidad.Cita;
import barberia.entidad.Cliente;
import barberia.entidad.EstadoCita;
import barberia.entidad.Horario;
import barberia.entidad.Servicio;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Consultas {

    private IAlmacen almacen;

    public Consultas(IAlmacen almacen) {
        this.almacen = almacen;
    }

    public Cliente buscarCliente(String cedula) {
        for (Cliente c : almacen.getClientes()) {
            if (c.getCedula().equals(cedula)) {
                return c;
            }
        }
        return null;
    }

    public Servicio buscarServicio(int id) {
        for (Servicio s : almacen.getServicios()) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    public Cita buscarCita(int id) {
        for (Cita c : almacen.getCitas()) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public Barbero buscarBarberoPorId(int id) {
        for (Barbero b : almacen.getBarberos()) {
            if (b.getId() == id) {
                return b;
            }
        }
        return null;
    }

    public ArrayList<Barbero> listarBarberos() {
        return new ArrayList<>(almacen.getBarberos());
    }

    public ArrayList<Horario> listarHorarios(Barbero barbero) {
        ArrayList<Horario> lista = new ArrayList<>();
        for (Horario h : almacen.getHorarios()) {
            if (h.getBarbero() == barbero) {
                lista.add(h);
            }
        }
        return lista;
    }

    public ArrayList<Cita> listarPendientes() {
        ArrayList<Cita> lista = new ArrayList<>();
        for (Cita c : almacen.getCitas()) {
            if (c.getEstado() == EstadoCita.PENDIENTE) {
                lista.add(c);
            }
        }
        return lista;
    }

    // true si el barbero ya tiene otra cita propuesta o agendada que choca con ese horario
    public boolean hayCruce(Barbero barbero, LocalDate fecha, LocalTime ini, LocalTime fin, Cita cita) {
        for (Cita otra : almacen.getCitas()) {
            if (otra == cita || otra.getBarbero() != barbero || !otra.getFecha().equals(fecha)) {
                continue;
            }
            if (otra.getEstado() != EstadoCita.PROPUESTA && otra.getEstado() != EstadoCita.AGENDADA) {
                continue;
            }
            if (ini.isBefore(otra.getHoraFin()) && otra.getHora().isBefore(fin)) {
                return true;
            }
        }
        return false;
    }
}

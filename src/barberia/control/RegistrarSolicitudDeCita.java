package barberia.control;

import barberia.entidad.Cita;
import barberia.entidad.Cliente;
import barberia.entidad.Servicio;
import barberia.interfaz.InterfazGestionarSolicitudes;
import barberia.persistencia.Consultas;
import barberia.persistencia.IAlmacen;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class RegistrarSolicitudDeCita {

    private Consultas consultas;
    private IAlmacen almacen;
    private InterfazGestionarSolicitudes interfazGestionar;

    public RegistrarSolicitudDeCita(Consultas consultas, IAlmacen almacen) {
        this.consultas = consultas;
        this.almacen = almacen;
    }

    public ArrayList<Servicio> listarServicios() {
        return new ArrayList<>(almacen.getServicios());
    }

    public Cita registrar(Cliente cliente, int idServicio, LocalDate fecha, LocalTime hora) {
        Servicio servicio = consultas.buscarServicio(idServicio);
        if (servicio == null) {
            throw new IllegalArgumentException("Seleccione un servicio.");
        }
        if (fecha == null || hora == null) {
            throw new IllegalArgumentException("Seleccione la fecha y la hora.");
        }
        if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha y hora ya pasaron, elija una posterior.");
        }
        // la cita solo se puede pedir hasta 31 dias desde hoy
        if (fecha.isAfter(LocalDate.now().plusDays(31))) {
            throw new IllegalArgumentException("La fecha no puede ser mayor a 31 dias desde hoy (maximo "
                    + LocalDate.now().plusDays(31).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ").");
        }
        Cita cita = new Cita(cliente, servicio, fecha, hora);
        almacen.guardarCita(cita);
        if (interfazGestionar != null) {
            interfazGestionar.notificarSolicitud(cita);
        }
        return cita;
    }

    public void setInterfazGestionar(InterfazGestionarSolicitudes interfaz) {
        this.interfazGestionar = interfaz;
    }
}

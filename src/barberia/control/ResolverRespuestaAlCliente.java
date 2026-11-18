package barberia.control;

import barberia.entidad.Cita;
import barberia.entidad.EstadoCita;
import barberia.interfaz.InterfazGestionarSolicitudes;
import barberia.persistencia.Consultas;
import barberia.persistencia.IAlmacen;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ResolverRespuestaAlCliente {

    private Consultas consultas;
    private IAlmacen almacen;
    private RevisarDisponibilidadDeBarberos revisar;
    private ProponerCitaAlCliente proponer;
    private InterfazGestionarSolicitudes interfazGestionar;

    public ResolverRespuestaAlCliente(Consultas consultas, IAlmacen almacen, RevisarDisponibilidadDeBarberos revisar,
            ProponerCitaAlCliente proponer) {
        this.consultas = consultas;
        this.almacen = almacen;
        this.revisar = revisar;
        this.proponer = proponer;
    }

    public Cita aceptarCita(int idCita) {
        Cita cita = buscar(idCita);
        if (cita.getEstado() != EstadoCita.PROPUESTA) {
            throw new IllegalArgumentException("Solo se puede aceptar una cita propuesta.");
        }
        if (!revisar.confirmarCobertura(cita)) {
            throw new IllegalArgumentException("El barbero ya no esta disponible a esa hora.");
        }
        cita.agendar();
        almacen.guardarCita(cita);
        notificar(cita);
        return cita;
    }

    public Cita cancelarCita(int idCita) {
        Cita cita = buscar(idCita);
        if (cita.getEstado() != EstadoCita.PROPUESTA && cita.getEstado() != EstadoCita.SIN_BARBERO) {
            throw new IllegalArgumentException("Solo se puede cancelar una cita propuesta o sin barbero.");
        }
        cita.cancelar();
        almacen.guardarCita(cita);
        notificar(cita);
        return cita;
    }

    public Cita reprogramarCita(int idCita, LocalDate fecha, LocalTime hora) {
        Cita cita = buscar(idCita);
        if (cita.getEstado() != EstadoCita.SIN_BARBERO) {
            throw new IllegalArgumentException("Solo se puede reprogramar una cita sin barbero.");
        }
        if (fecha == null || hora == null) {
            throw new IllegalArgumentException("Elija el nuevo horario.");
        }
        if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha y hora ya pasaron, elija una posterior.");
        }
        cita.reprogramar(fecha, hora);
        almacen.guardarCita(cita);
        proponer.proponerCita(idCita);
        notificar(cita);
        return cita;
    }

    public void setInterfazGestionar(InterfazGestionarSolicitudes interfaz) {
        this.interfazGestionar = interfaz;
    }

    private Cita buscar(int idCita) {
        Cita cita = consultas.buscarCita(idCita);
        if (cita == null) {
            throw new IllegalArgumentException("No existe la cita.");
        }
        return cita;
    }

    private void notificar(Cita cita) {
        if (interfazGestionar != null) {
            interfazGestionar.mostrarResultado(cita);
        }
    }
}

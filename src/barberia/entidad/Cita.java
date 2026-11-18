package barberia.entidad;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {

    private static int contador = 0;

    private int id;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoCita estado;
    private Cliente solicitante;
    private Servicio servicio;
    private Barbero barbero;

    public Cita(Cliente cliente, Servicio servicio, LocalDate fecha, LocalTime hora) {
        contador++;
        this.id = contador;
        this.solicitante = cliente;
        this.servicio = servicio;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = EstadoCita.PENDIENTE;
        cliente.agregarCita(this);
        servicio.agregarCita(this);
    }

    public LocalTime getHoraFin() {
        return hora.plusMinutes(servicio.getDuracionMin());
    }

    public void proponer(Barbero barbero) {
        this.barbero = barbero;
        this.estado = EstadoCita.PROPUESTA;
        barbero.agregarCita(this);
    }

    public void marcarSinBarbero() {
        liberarBarbero();
        estado = EstadoCita.SIN_BARBERO;
    }

    public void agendar() {
        estado = EstadoCita.AGENDADA;
    }

    public void cancelar() {
        liberarBarbero();
        estado = EstadoCita.CANCELADA;
    }

    // vuelve a pendiente para revisar otra vez si hay barbero
    public void reprogramar(LocalDate fecha, LocalTime hora) {
        liberarBarbero();
        this.fecha = fecha;
        this.hora = hora;
        estado = EstadoCita.PENDIENTE;
    }

    private void liberarBarbero() {
        if (barbero != null) {
            barbero.quitarCita(this);
            barbero = null;
        }
    }

    public int getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public Cliente getSolicitante() {
        return solicitante;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public Barbero getBarbero() {
        return barbero;
    }
}

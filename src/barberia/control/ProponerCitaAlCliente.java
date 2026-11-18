package barberia.control;

import barberia.entidad.Barbero;
import barberia.entidad.Cita;
import barberia.entidad.EstadoCita;
import barberia.interfaz.InterfazResponderPropuesta;
import barberia.persistencia.Consultas;
import barberia.persistencia.IAlmacen;
import java.util.ArrayList;

public class ProponerCitaAlCliente {

    private Consultas consultas;
    private IAlmacen almacen;
    private RevisarDisponibilidadDeBarberos revisar;
    private InterfazResponderPropuesta interfazResponder;

    public ProponerCitaAlCliente(Consultas consultas, IAlmacen almacen, RevisarDisponibilidadDeBarberos revisar) {
        this.consultas = consultas;
        this.almacen = almacen;
        this.revisar = revisar;
    }

    public ArrayList<Cita> listarPendientes() {
        return consultas.listarPendientes();
    }

    public Cita proponerCita(int idCita) {
        Cita cita = consultas.buscarCita(idCita);
        if (cita == null) {
            throw new IllegalArgumentException("No existe la cita.");
        }
        if (cita.getEstado() != EstadoCita.PENDIENTE) {
            throw new IllegalArgumentException("Solo se pueden revisar citas pendientes.");
        }
        Barbero barbero = revisar.buscarBarbero(cita);
        ArrayList<String> alternativas = new ArrayList<>();
        if (barbero != null) {
            cita.proponer(barbero);
        } else {
            cita.marcarSinBarbero();
            alternativas = revisar.listarHorariosLibres(cita);
        }
        almacen.guardarCita(cita);
        if (interfazResponder != null) {
            interfazResponder.mostrarPropuesta(cita, alternativas);
        }
        return cita;
    }

    public void setInterfazResponder(InterfazResponderPropuesta interfaz) {
        this.interfazResponder = interfaz;
    }
}

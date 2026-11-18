package barberia.persistencia;

import barberia.entidad.Barbero;
import barberia.entidad.Cita;
import barberia.entidad.Cliente;
import barberia.entidad.Horario;
import barberia.entidad.Servicio;
import java.util.ArrayList;

public interface IAlmacen {

    void guardarCliente(Cliente cliente);

    void guardarCita(Cita cita);

    void guardarHorario(Horario horario);

    void eliminarHorario(Horario horario);

    ArrayList<Cliente> getClientes();

    ArrayList<Servicio> getServicios();

    ArrayList<Barbero> getBarberos();

    ArrayList<Horario> getHorarios();

    ArrayList<Cita> getCitas();
}

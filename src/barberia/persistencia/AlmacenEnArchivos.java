package barberia.persistencia;

import barberia.entidad.Barbero;
import barberia.entidad.Cita;
import barberia.entidad.Cliente;
import barberia.entidad.EstadoCita;
import barberia.entidad.Horario;
import barberia.entidad.Servicio;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class AlmacenEnArchivos implements IAlmacen {

    private Path carpeta;
    private ArrayList<Cliente> clientes = new ArrayList<>();
    private ArrayList<Servicio> servicios = new ArrayList<>();
    private ArrayList<Barbero> barberos = new ArrayList<>();
    private ArrayList<Horario> horarios = new ArrayList<>();
    private ArrayList<Cita> citas = new ArrayList<>();

    public AlmacenEnArchivos(Path carpeta) {
        this.carpeta = carpeta;
        cargar();
    }

    @Override
    public void guardarCliente(Cliente cliente) {
        if (!clientes.contains(cliente)) {
            clientes.add(cliente);
        }
        persistir();
    }

    @Override
    public void guardarCita(Cita cita) {
        if (!citas.contains(cita)) {
            citas.add(cita);
        }
        persistir();
    }

    @Override
    public void guardarHorario(Horario horario) {
        if (!horarios.contains(horario)) {
            horarios.add(horario);
        }
        persistir();
    }

    @Override
    public void eliminarHorario(Horario horario) {
        horarios.remove(horario);
        horario.getBarbero().getHorarios().remove(horario);
        persistir();
    }

    @Override
    public ArrayList<Cliente> getClientes() {
        return clientes;
    }

    @Override
    public ArrayList<Servicio> getServicios() {
        return servicios;
    }

    @Override
    public ArrayList<Barbero> getBarberos() {
        return barberos;
    }

    @Override
    public ArrayList<Horario> getHorarios() {
        return horarios;
    }

    @Override
    public ArrayList<Cita> getCitas() {
        return citas;
    }

    private void cargar() {
        // servicios y barberos fijos de la barberia
        servicios.add(new Servicio(1, "Corte clasico", 8.00, 30));
        servicios.add(new Servicio(2, "Arreglo de barba", 5.00, 20));
        servicios.add(new Servicio(3, "Corte y barba", 12.00, 45));
        servicios.add(new Servicio(4, "Corte infantil", 6.00, 30));
        barberos.add(new Barbero(1, "Luis", "Cortes clasicos"));
        barberos.add(new Barbero(2, "Carlos", "Barba y degradados"));

        try {
            Files.createDirectories(carpeta);

            Path archivoClientes = carpeta.resolve("clientes.txt");
            if (Files.exists(archivoClientes)) {
                for (String linea : Files.readAllLines(archivoClientes, StandardCharsets.UTF_8)) {
                    if (linea.trim().isEmpty()) {
                        continue;
                    }
                    String[] p = linea.split(";", -1);
                    clientes.add(new Cliente(p[0], p[1], p[2], p[3], p[4], p[5]));
                }
            } else {
                clientes.add(new Cliente("1712345678", "Juan", "Perez", "0991234567", "juan.perez@correo.com", "1234"));
            }

            Path archivoHorarios = carpeta.resolve("horarios.txt");
            if (Files.exists(archivoHorarios)) {
                for (String linea : Files.readAllLines(archivoHorarios, StandardCharsets.UTF_8)) {
                    if (linea.trim().isEmpty()) {
                        continue;
                    }
                    String[] p = linea.split(";", -1);
                    Barbero b = buscarBarbero(Integer.parseInt(p[0]));
                    Horario h = new Horario(DayOfWeek.valueOf(p[1]), LocalTime.parse(p[2]), LocalTime.parse(p[3]), b);
                    b.agregarHorario(h);
                    horarios.add(h);
                }
            }

            // las citas se guardan en orden, asi el id se vuelve a generar igual
            Path archivoCitas = carpeta.resolve("citas.txt");
            if (Files.exists(archivoCitas)) {
                for (String linea : Files.readAllLines(archivoCitas, StandardCharsets.UTF_8)) {
                    if (linea.trim().isEmpty()) {
                        continue;
                    }
                    String[] p = linea.split(";", -1);
                    Cliente c = buscarCliente(p[1]);
                    Servicio s = buscarServicio(Integer.parseInt(p[2]));
                    Cita cita = new Cita(c, s, LocalDate.parse(p[3]), LocalTime.parse(p[4]));
                    EstadoCita estado = EstadoCita.valueOf(p[5]);
                    Barbero b = p[6].isEmpty() ? null : buscarBarbero(Integer.parseInt(p[6]));
                    if (b != null) {
                        cita.proponer(b);
                    }
                    if (estado == EstadoCita.AGENDADA) {
                        cita.agendar();
                    } else if (estado == EstadoCita.CANCELADA) {
                        cita.cancelar();
                    } else if (estado == EstadoCita.SIN_BARBERO) {
                        cita.marcarSinBarbero();
                    }
                    citas.add(cita);
                }
            }
            persistir();
        } catch (IOException | RuntimeException e) {
            System.out.println("Error al leer los archivos: " + e.getMessage());
        }
    }

    private void persistir() {
        try {
            Files.createDirectories(carpeta);

            ArrayList<String> lineas = new ArrayList<>();
            for (Cliente c : clientes) {
                lineas.add(c.getCedula() + ";" + c.getNombres() + ";" + c.getApellidos() + ";"
                        + c.getTelefono() + ";" + c.getCorreo() + ";" + c.getClave());
            }
            Files.write(carpeta.resolve("clientes.txt"), lineas, StandardCharsets.UTF_8);

            lineas = new ArrayList<>();
            for (Horario h : horarios) {
                lineas.add(h.getBarbero().getId() + ";" + h.getDia() + ";" + h.getInicio() + ";" + h.getFin());
            }
            Files.write(carpeta.resolve("horarios.txt"), lineas, StandardCharsets.UTF_8);

            lineas = new ArrayList<>();
            for (Cita c : citas) {
                String idBarbero = c.getBarbero() == null ? "" : String.valueOf(c.getBarbero().getId());
                lineas.add(c.getId() + ";" + c.getSolicitante().getCedula() + ";" + c.getServicio().getId() + ";"
                        + c.getFecha() + ";" + c.getHora() + ";" + c.getEstado().name() + ";" + idBarbero);
            }
            Files.write(carpeta.resolve("citas.txt"), lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Error al guardar los archivos: " + e.getMessage());
        }
    }

    private Barbero buscarBarbero(int id) {
        for (Barbero b : barberos) {
            if (b.getId() == id) {
                return b;
            }
        }
        return null;
    }

    private Servicio buscarServicio(int id) {
        for (Servicio s : servicios) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    private Cliente buscarCliente(String cedula) {
        for (Cliente c : clientes) {
            if (c.getCedula().equals(cedula)) {
                return c;
            }
        }
        return null;
    }
}

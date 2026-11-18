package barberia.control;

import barberia.entidad.Barbero;
import barberia.entidad.Cita;
import barberia.entidad.Horario;
import barberia.persistencia.Consultas;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RevisarDisponibilidadDeBarberos {

    private static final String[] DIAS = {"lunes", "martes", "miercoles", "jueves", "viernes", "sabado", "domingo"};

    private Consultas consultas;

    public RevisarDisponibilidadDeBarberos(Consultas consultas) {
        this.consultas = consultas;
    }

    public Barbero buscarBarbero(Cita cita) {
        return buscar(cita, cita.getFecha(), cita.getHora(), cita.getHoraFin());
    }

    public boolean confirmarCobertura(Cita cita) {
        Barbero b = cita.getBarbero();
        if (b == null) {
            return false;
        }
        for (Horario h : consultas.listarHorarios(b)) {
            if (h.cubre(cita.getFecha(), cita.getHora(), cita.getHoraFin())) {
                return !consultas.hayCruce(b, cita.getFecha(), cita.getHora(), cita.getHoraFin(), cita);
            }
        }
        return false;
    }

    // horarios libres de los proximos 14 dias, cada media hora
    public ArrayList<String> listarHorariosLibres(Cita cita) {
        ArrayList<String> libres = new ArrayList<>();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate desde = cita.getFecha().isBefore(LocalDate.now()) ? LocalDate.now() : cita.getFecha();
        for (int d = 0; d < 14; d++) {
            LocalDate fecha = desde.plusDays(d);
            for (LocalTime t = LocalTime.of(8, 0); !t.isAfter(LocalTime.of(20, 0)); t = t.plusMinutes(30)) {
                if (LocalDateTime.of(fecha, t).isBefore(LocalDateTime.now())) {
                    continue;
                }
                if (fecha.equals(cita.getFecha()) && t.equals(cita.getHora())) {
                    continue;
                }
                Barbero b = buscar(cita, fecha, t, t.plusMinutes(cita.getServicio().getDuracionMin()));
                if (b != null) {
                    libres.add(DIAS[fecha.getDayOfWeek().getValue() - 1] + " " + fecha.format(formato) + " " + t + " - " + b.getNombre());
                }
            }
        }
        return libres;
    }

    private Barbero buscar(Cita cita, LocalDate fecha, LocalTime ini, LocalTime fin) {
        for (Barbero b : consultas.listarBarberos()) {
            for (Horario h : consultas.listarHorarios(b)) {
                if (h.cubre(fecha, ini, fin) && !consultas.hayCruce(b, fecha, ini, fin, cita)) {
                    return b;
                }
            }
        }
        return null;
    }
}

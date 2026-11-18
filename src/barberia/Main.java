package barberia;

import barberia.control.MantenerHorariosDeBarberos;
import barberia.control.ProponerCitaAlCliente;
import barberia.control.RegistrarCliente;
import barberia.control.RegistrarSolicitudDeCita;
import barberia.control.ResolverRespuestaAlCliente;
import barberia.control.RevisarDisponibilidadDeBarberos;
import barberia.control.ValidarAcceso;
import barberia.interfaz.InterfazAdministrarHorarios;
import barberia.interfaz.InterfazGestionarSolicitudes;
import barberia.interfaz.InterfazLogin;
import barberia.interfaz.InterfazMenuPrincipal;
import barberia.interfaz.InterfazRegistrarCliente;
import barberia.interfaz.InterfazResponderPropuesta;
import barberia.interfaz.InterfazSolicitarCita;
import barberia.persistencia.AlmacenEnArchivos;
import barberia.persistencia.Consultas;
import barberia.persistencia.IAlmacen;
import java.nio.file.Paths;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> iniciar());
    }

    public static InterfazMenuPrincipal iniciar() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("No se pudo cambiar el aspecto");
        }

        IAlmacen almacen = new AlmacenEnArchivos(Paths.get("datos"));
        Consultas consultas = new Consultas(almacen);

        ValidarAcceso validarAcceso = new ValidarAcceso(consultas);
        RegistrarCliente registrarCliente = new RegistrarCliente(consultas, almacen);
        RegistrarSolicitudDeCita registrarSolicitud = new RegistrarSolicitudDeCita(consultas, almacen);
        RevisarDisponibilidadDeBarberos revisar = new RevisarDisponibilidadDeBarberos(consultas);
        ProponerCitaAlCliente proponer = new ProponerCitaAlCliente(consultas, almacen, revisar);
        ResolverRespuestaAlCliente resolver = new ResolverRespuestaAlCliente(consultas, almacen, revisar, proponer);
        MantenerHorariosDeBarberos mantener = new MantenerHorariosDeBarberos(consultas, almacen);

        InterfazRegistrarCliente interfazRegistrar = new InterfazRegistrarCliente(registrarCliente);
        InterfazLogin login = new InterfazLogin(validarAcceso, interfazRegistrar);
        InterfazSolicitarCita solicitar = new InterfazSolicitarCita(registrarSolicitud);
        InterfazGestionarSolicitudes gestionar = new InterfazGestionarSolicitudes(proponer);
        InterfazResponderPropuesta responder = new InterfazResponderPropuesta(resolver);
        InterfazAdministrarHorarios administrar = new InterfazAdministrarHorarios(mantener);

        registrarSolicitud.setInterfazGestionar(gestionar);
        resolver.setInterfazGestionar(gestionar);
        proponer.setInterfazResponder(responder);

        InterfazMenuPrincipal menu = new InterfazMenuPrincipal(login, solicitar, gestionar, responder, administrar);
        menu.abrir();
        return menu;
    }
}

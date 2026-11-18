package barberia.interfaz;

import barberia.entidad.Cliente;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

public class InterfazMenuPrincipal extends JFrame {

    private InterfazLogin login;
    private InterfazSolicitarCita solicitar;
    private InterfazGestionarSolicitudes gestionar;
    private InterfazResponderPropuesta responder;
    private InterfazAdministrarHorarios administrar;

    private Cliente cliente;
    private CardLayout tarjetas = new CardLayout();
    private JPanel panel = new JPanel(tarjetas);
    private JLabel lblBienvenida = new JLabel(" ", SwingConstants.CENTER);

    public InterfazMenuPrincipal(InterfazLogin login, InterfazSolicitarCita solicitar, InterfazGestionarSolicitudes gestionar,
            InterfazResponderPropuesta responder, InterfazAdministrarHorarios administrar) {
        super("Barberia - Menu Principal");
        this.login = login;
        this.solicitar = solicitar;
        this.gestionar = gestionar;
        this.responder = responder;
        this.administrar = administrar;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(new Color(47, 62, 70));
        cabecera.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        JLabel nombre = new JLabel("Barberia");
        nombre.setForeground(Color.WHITE);
        nombre.setFont(nombre.getFont().deriveFont(Font.BOLD, 26f));
        JLabel lema = new JLabel("Citas y horarios");
        lema.setForeground(new Color(224, 201, 166));
        cabecera.add(nombre, BorderLayout.CENTER);
        cabecera.add(lema, BorderLayout.SOUTH);

        panel.add(crearPanel(null,
                boton("Iniciar sesion (cliente)", "btnIniciarSesion", "login"),
                boton("Ingresar como administrador", "btnAdmin", "admin"),
                boton("Salir", "btnSalir", "salir")), "inicio");
        panel.add(crearPanel(lblBienvenida,
                boton("Solicitar cita", "btnSolicitar", "solicitar"),
                boton("Responder propuesta", "btnResponder", "responder"),
                boton("Cerrar sesion", "btnCerrarCliente", "cerrar")), "cliente");
        panel.add(crearPanel(new JLabel("Administrador", SwingConstants.CENTER),
                boton("Gestionar solicitudes", "btnGestionar", "gestionar"),
                boton("Administrar horarios", "btnHorarios", "administrar"),
                boton("Cerrar sesion", "btnCerrarAdmin", "cerrar")), "admin");

        add(cabecera, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        setSize(420, 380);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public void abrir() {
        tarjetas.show(panel, "inicio");
        setVisible(true);
    }

    public Object abrirModulo(String nombre) {
        switch (nombre) {
            case "login":
                login.setVisible(true);
                if (login.getCliente() != null) {
                    cliente = login.getCliente();
                    lblBienvenida.setText("Bienvenido, " + cliente);
                    tarjetas.show(panel, "cliente");
                }
                return login;
            case "admin":
                JPasswordField clave = new JPasswordField(12);
                int r = JOptionPane.showConfirmDialog(this, new Object[]{"Clave del administrador:", clave}, "Administrador",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (r == JOptionPane.OK_OPTION) {
                    if (new String(clave.getPassword()).equals("admin")) {
                        tarjetas.show(panel, "admin");
                    } else {
                        JOptionPane.showMessageDialog(this, "Clave incorrecta.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                return null;
            case "solicitar":
                solicitar.setCliente(cliente);
                solicitar.mostrarServicios();
                solicitar.setVisible(true);
                return solicitar;
            case "responder":
                responder.setCliente(cliente);
                responder.setVisible(true);
                return responder;
            case "gestionar":
                gestionar.mostrarSolicitudesPendientes();
                gestionar.setVisible(true);
                return gestionar;
            case "administrar":
                administrar.setVisible(true);
                return administrar;
            case "cerrar":
                cliente = null;
                solicitar.setVisible(false);
                responder.setVisible(false);
                gestionar.setVisible(false);
                administrar.setVisible(false);
                tarjetas.show(panel, "inicio");
                return null;
            default:
                System.exit(0);
                return null;
        }
    }

    private JPanel crearPanel(JLabel encabezado, JButton... botones) {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(new Color(246, 243, 238));
        p.setBorder(BorderFactory.createEmptyBorder(18, 50, 22, 50));
        if (encabezado != null) {
            encabezado.setFont(encabezado.getFont().deriveFont(Font.BOLD, 15f));
            p.add(encabezado, BorderLayout.NORTH);
        }
        JPanel lista = new JPanel(new GridLayout(botones.length, 1, 0, 10));
        lista.setOpaque(false);
        for (JButton b : botones) {
            lista.add(b);
        }
        p.add(lista, BorderLayout.CENTER);
        return p;
    }

    private JButton boton(String texto, String nombre, String modulo) {
        JButton b = new JButton(texto);
        b.setName(nombre);
        b.setFont(b.getFont().deriveFont(14f));
        b.setPreferredSize(new Dimension(260, 42));
        b.addActionListener(e -> abrirModulo(modulo));
        return b;
    }
}

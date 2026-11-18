package barberia.interfaz;

import barberia.control.RegistrarSolicitudDeCita;
import barberia.entidad.Cita;
import barberia.entidad.Cliente;
import barberia.entidad.Servicio;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.MaskFormatter;

public class InterfazSolicitarCita extends JDialog {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private RegistrarSolicitudDeCita registrar;
    private Cliente cliente;

    private JComboBox<Servicio> cmbServicio = new JComboBox<>();
    private JFormattedTextField txtFecha;
    private JComboBox<LocalTime> cmbHora = new JComboBox<>();
    private JLabel lblCliente = new JLabel(" ");

    public InterfazSolicitarCita(RegistrarSolicitudDeCita registrar) {
        super((java.awt.Frame) null, "Solicitar Cita", false);
        this.registrar = registrar;

        try {
            MaskFormatter mascara = new MaskFormatter("##/##/####");
            mascara.setPlaceholderCharacter('_');
            txtFecha = new JFormattedTextField(mascara);
        } catch (ParseException e) {
            txtFecha = new JFormattedTextField();
        }
        txtFecha.setColumns(10);
        txtFecha.setFocusLostBehavior(JFormattedTextField.COMMIT);
        for (LocalTime t = LocalTime.of(8, 0); !t.isAfter(LocalTime.of(20, 0)); t = t.plusMinutes(30)) {
            cmbHora.addItem(t);
        }
        txtFecha.setName("txtFecha");
        cmbServicio.setName("cmbServicio");
        cmbHora.setName("cmbHora");

        JLabel titulo = new JLabel("Solicitar cita");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 24, 8, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;
        form.add(titulo, g);
        g.gridy = 1;
        form.add(lblCliente, g);
        g.gridwidth = 1;
        g.gridy = 2;
        form.add(new JLabel("Servicio (precio y duracion):"), g);
        g.gridx = 1;
        form.add(cmbServicio, g);
        g.gridx = 0;
        g.gridy = 3;
        form.add(new JLabel("Fecha (dd/mm/aaaa):"), g);
        g.gridx = 1;
        form.add(txtFecha, g);
        g.gridx = 0;
        g.gridy = 4;
        form.add(new JLabel("Hora:"), g);
        g.gridx = 1;
        form.add(cmbHora, g);

        JButton btnEnviar = new JButton("Enviar solicitud");
        btnEnviar.setName("btnEnviar");
        JButton btnCerrar = new JButton("Cerrar");
        btnEnviar.addActionListener(e -> enviarClick());
        btnCerrar.addActionListener(e -> setVisible(false));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCerrar);
        botones.add(btnEnviar);

        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        mostrarServicios();
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        lblCliente.setText("Cliente: " + cliente);
        txtFecha.setValue(null);
        txtFecha.setText("");
        cmbHora.setSelectedIndex(0);
    }

    public void mostrarServicios() {
        cmbServicio.removeAllItems();
        for (Servicio s : registrar.listarServicios()) {
            cmbServicio.addItem(s);
        }
    }

    public void enviarSolicitud(Cliente cliente, int idServicio, LocalDate fecha, LocalTime hora) {
        Cita cita = registrar.registrar(cliente, idServicio, fecha, hora);
        mostrarMensaje("Solicitud enviada.\nCita #" + cita.getId() + " - " + cita.getServicio().getNombre() + " - "
                + fecha.format(FORMATO) + " " + hora + "\nEstado: " + cita.getEstado() + ". El administrador revisara la disponibilidad.");
    }

    public void mostrarMensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Barberia", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void enviarClick() {
        Servicio servicio = (Servicio) cmbServicio.getSelectedItem();
        String texto = txtFecha.getText();
        if (servicio == null) {
            mostrarError("Seleccione un servicio.");
            return;
        }
        if (texto.replace("_", "").replace("/", "").trim().isEmpty()) {
            mostrarError("Ingrese la fecha de la cita.");
            return;
        }
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(texto.trim(), FORMATO);
        } catch (DateTimeParseException ex) {
            mostrarError("La fecha no es valida. Use el formato dd/mm/aaaa.");
            return;
        }
        try {
            enviarSolicitud(cliente, servicio.getId(), fecha, (LocalTime) cmbHora.getSelectedItem());
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }
}

package barberia.interfaz;

import barberia.control.ResolverRespuestaAlCliente;
import barberia.entidad.Cita;
import barberia.entidad.Cliente;
import barberia.entidad.EstadoCita;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

public class InterfazResponderPropuesta extends JDialog {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private ResolverRespuestaAlCliente resolver;
    private Cliente cliente;
    private HashMap<Integer, ArrayList<String>> alternativas = new HashMap<>();

    private DefaultTableModel modelo = new DefaultTableModel(new String[]{"N", "Servicio", "Fecha", "Hora", "Estado", "Barbero"}, 0) {
        @Override
        public boolean isCellEditable(int f, int c) {
            return false;
        }
    };
    private JTable tblCitas = new JTable(modelo);
    private JLabel lblDetalle = new JLabel(" ");
    private DefaultListModel<String> modeloAlternativas = new DefaultListModel<>();
    private JList<String> lstAlternativas = new JList<>(modeloAlternativas);
    private JFormattedTextField txtFecha;
    private JComboBox<LocalTime> cmbHora = new JComboBox<>();
    private JButton btnAceptar = new JButton("Aceptar");
    private JButton btnCancelar = new JButton("Cancelar cita");
    private JButton btnReprogramar = new JButton("Reprogramar");

    public InterfazResponderPropuesta(ResolverRespuestaAlCliente resolver) {
        super((java.awt.Frame) null, "Responder Propuesta", false);
        this.resolver = resolver;

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

        tblCitas.setName("tblMisCitas");
        lstAlternativas.setName("lstAlternativas");
        btnAceptar.setName("btnAceptar");
        btnCancelar.setName("btnCancelarCita");
        btnReprogramar.setName("btnReprogramar");
        tblCitas.setRowHeight(24);
        tblCitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstAlternativas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCitas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarSeleccionada();
            }
        });
        // al elegir una alternativa se llenan la fecha y la hora
        lstAlternativas.addListSelectionListener(e -> {
            String alt = lstAlternativas.getSelectedValue();
            if (alt != null && !e.getValueIsAdjusting()) {
                String[] p = alt.split(" ");
                txtFecha.setText(p[1]);
                cmbHora.setSelectedItem(LocalTime.parse(p[2]));
            }
        });

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Mis citas"));
        panelTabla.add(new JScrollPane(tblCitas));

        JPanel nuevoHorario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nuevoHorario.add(new JLabel("Nueva fecha:"));
        nuevoHorario.add(txtFecha);
        nuevoHorario.add(new JLabel("Hora:"));
        nuevoHorario.add(cmbHora);

        JPanel panelAlt = new JPanel(new BorderLayout());
        panelAlt.setBorder(BorderFactory.createTitledBorder("Horarios alternativos (elija uno para reprogramar)"));
        panelAlt.add(new JScrollPane(lstAlternativas), BorderLayout.CENTER);
        panelAlt.add(nuevoHorario, BorderLayout.SOUTH);

        JPanel panelDetalle = new JPanel(new BorderLayout(0, 6));
        panelDetalle.setBorder(BorderFactory.createTitledBorder("Propuesta"));
        lblDetalle.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        panelDetalle.add(lblDetalle, BorderLayout.NORTH);
        panelDetalle.add(panelAlt, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelTabla, panelDetalle);
        split.setResizeWeight(0.4);
        split.setBorder(null);

        btnAceptar.addActionListener(e -> responderClick("ACEPTAR"));
        btnCancelar.addActionListener(e -> responderClick("CANCELAR"));
        btnReprogramar.addActionListener(e -> responderClick("REPROGRAMAR"));
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> setVisible(false));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnAceptar);
        botones.add(btnReprogramar);
        botones.add(btnCancelar);
        botones.add(btnCerrar);

        JLabel titulo = new JLabel("Responder propuesta");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        JPanel raiz = new JPanel(new BorderLayout(0, 6));
        raiz.setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 12));
        raiz.add(titulo, BorderLayout.NORTH);
        raiz.add(split, BorderLayout.CENTER);
        raiz.add(botones, BorderLayout.SOUTH);
        setContentPane(raiz);
        setSize(760, 640);
        setLocationRelativeTo(null);
        habilitar(null);
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        setTitle("Responder Propuesta - " + cliente);
        refrescar(null);
    }

    public void mostrarPropuesta(Cita cita, ArrayList<String> alternativas) {
        if (cita != null && cita.getEstado() == EstadoCita.SIN_BARBERO) {
            this.alternativas.put(cita.getId(), alternativas);
        }
        if (cita != null && cita.getSolicitante() == cliente && isVisible()) {
            refrescar(cita);
        }
    }

    public void responder(int idCita, String decision, LocalDate fecha, LocalTime hora) {
        try {
            Cita cita;
            if (decision.equals("ACEPTAR")) {
                cita = resolver.aceptarCita(idCita);
                mostrarMensaje("Cita agendada con " + cita.getBarbero().getNombre() + " el " + cita.getFecha().format(FORMATO)
                        + " a las " + cita.getHora() + ".");
            } else if (decision.equals("CANCELAR")) {
                cita = resolver.cancelarCita(idCita);
                mostrarMensaje("Cita cancelada.");
            } else {
                cita = resolver.reprogramarCita(idCita, fecha, hora);
                String barbero = cita.getBarbero() == null ? "" : " con " + cita.getBarbero().getNombre();
                mostrarMensaje("Cita reprogramada para el " + cita.getFecha().format(FORMATO) + " a las " + cita.getHora()
                        + ".\nSe volvio a revisar la disponibilidad: " + cita.getEstado() + barbero + ".");
            }
            refrescar(cita);
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    public void mostrarMensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Barberia", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void refrescar(Cita seleccionar) {
        if (cliente == null) {
            return;
        }
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        modelo.setRowCount(0);
        int fila = -1;
        ArrayList<Cita> citas = cliente.getCitas();
        for (int i = 0; i < citas.size(); i++) {
            Cita c = citas.get(i);
            modelo.addRow(new Object[]{c.getId(), c.getServicio().getNombre(), c.getFecha().format(f),
                c.getHora() + " - " + c.getHoraFin(), c.getEstado(), c.getBarbero() == null ? "-" : c.getBarbero().getNombre()});
            if (c == seleccionar) {
                fila = i;
            }
            if (seleccionar == null && fila < 0 && (c.getEstado() == EstadoCita.PROPUESTA || c.getEstado() == EstadoCita.SIN_BARBERO)) {
                fila = i;
            }
        }
        if (fila >= 0) {
            tblCitas.setRowSelectionInterval(fila, fila);
        }
        mostrarSeleccionada();
    }

    private Cita seleccionada() {
        int fila = tblCitas.getSelectedRow();
        if (cliente == null || fila < 0 || fila >= cliente.getCitas().size()) {
            return null;
        }
        return cliente.getCitas().get(fila);
    }

    private void mostrarSeleccionada() {
        Cita cita = seleccionada();
        modeloAlternativas.clear();
        txtFecha.setValue(null);
        txtFecha.setText("");
        if (cita == null) {
            lblDetalle.setText("Seleccione una cita.");
            habilitar(null);
            return;
        }
        String texto = "<html><b>Cita #" + cita.getId() + "</b> - " + cita.getServicio().getNombre()
                + "<br>Fecha: " + cita.getFecha().format(FORMATO) + "   Hora: " + cita.getHora() + " - " + cita.getHoraFin();
        if (cita.getEstado() == EstadoCita.PROPUESTA) {
            texto += "<br>Barbero: <b>" + cita.getBarbero().getNombre() + "</b><br>Estado: <b>Propuesta</b> - acepta la cita?";
        } else if (cita.getEstado() == EstadoCita.SIN_BARBERO) {
            texto += "<br>Estado: <b>Sin barbero</b> - no hay barbero disponible a esa hora."
                    + "<br>Elija un horario alternativo (o escriba otra fecha y hora) y pulse Reprogramar, o cancele la cita.";
            ArrayList<String> lista = alternativas.get(cita.getId());
            if (lista != null) {
                for (String a : lista) {
                    modeloAlternativas.addElement(a);
                }
            }
        } else if (cita.getEstado() == EstadoCita.PENDIENTE) {
            texto += "<br>Estado: <b>Pendiente</b> - el administrador aun no revisa la solicitud.";
        } else {
            texto += (cita.getBarbero() != null ? "<br>Barbero: " + cita.getBarbero().getNombre() : "")
                    + "<br>Estado: <b>" + cita.getEstado() + "</b>";
        }
        lblDetalle.setText(texto + "</html>");
        habilitar(cita);
    }

    private void habilitar(Cita cita) {
        EstadoCita e = cita == null ? null : cita.getEstado();
        btnAceptar.setEnabled(e == EstadoCita.PROPUESTA);
        btnCancelar.setEnabled(e == EstadoCita.PROPUESTA || e == EstadoCita.SIN_BARBERO);
        btnReprogramar.setEnabled(e == EstadoCita.SIN_BARBERO);
        lstAlternativas.setEnabled(e == EstadoCita.SIN_BARBERO);
        txtFecha.setEnabled(e == EstadoCita.SIN_BARBERO);
        cmbHora.setEnabled(e == EstadoCita.SIN_BARBERO);
    }

    private void responderClick(String decision) {
        Cita cita = seleccionada();
        if (cita == null) {
            mostrarError("Seleccione una cita.");
            return;
        }
        LocalDate fecha = null;
        LocalTime hora = null;
        if (decision.equals("REPROGRAMAR")) {
            String texto = txtFecha.getText();
            if (texto.replace("_", "").replace("/", "").trim().isEmpty()) {
                mostrarError("Elija un horario alternativo de la lista o escriba la nueva fecha.");
                return;
            }
            try {
                fecha = LocalDate.parse(texto.trim(), FORMATO);
            } catch (DateTimeParseException ex) {
                mostrarError("La fecha no es valida. Use el formato dd/mm/aaaa.");
                return;
            }
            hora = (LocalTime) cmbHora.getSelectedItem();
        } else if (decision.equals("CANCELAR")) {
            int r = JOptionPane.showConfirmDialog(this, "Seguro que desea cancelar la cita #" + cita.getId() + "?",
                    "Cancelar cita", JOptionPane.YES_NO_OPTION);
            if (r != JOptionPane.YES_OPTION) {
                return;
            }
        }
        responder(cita.getId(), decision, fecha, hora);
    }
}

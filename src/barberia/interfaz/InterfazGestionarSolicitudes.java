package barberia.interfaz;

import barberia.control.ProponerCitaAlCliente;
import barberia.entidad.Cita;
import barberia.entidad.EstadoCita;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class InterfazGestionarSolicitudes extends JDialog {

    private static final String[] COLUMNAS = {"N", "Cliente", "Servicio", "Fecha", "Hora", "Estado", "Barbero"};

    private ProponerCitaAlCliente proponer;

    private ArrayList<Cita> pendientes = new ArrayList<>();
    private ArrayList<Cita> resultados = new ArrayList<>();
    private DefaultTableModel modeloPendientes = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int f, int c) {
            return false;
        }
    };
    private DefaultTableModel modeloResultados = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int f, int c) {
            return false;
        }
    };
    private JTable tblPendientes = new JTable(modeloPendientes);
    private JTable tblResultados = new JTable(modeloResultados);
    private JLabel lblAviso = new JLabel(" ");

    public InterfazGestionarSolicitudes(ProponerCitaAlCliente proponer) {
        super((java.awt.Frame) null, "Gestionar Solicitudes", false);
        this.proponer = proponer;

        tblPendientes.setName("tblPendientes");
        tblPendientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPendientes.setRowHeight(24);
        tblResultados.setRowHeight(24);
        tblPendientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    revisarClick();
                }
            }
        });

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.setBorder(BorderFactory.createTitledBorder("Solicitudes pendientes (doble clic para revisar disponibilidad)"));
        arriba.add(new JScrollPane(tblPendientes));
        JPanel abajo = new JPanel(new BorderLayout());
        abajo.setBorder(BorderFactory.createTitledBorder("Resultados"));
        abajo.add(new JScrollPane(tblResultados));
        JPanel centro = new JPanel(new GridLayout(2, 1, 0, 8));
        centro.add(arriba);
        centro.add(abajo);

        JButton btnRevisar = new JButton("Revisar disponibilidad");
        btnRevisar.setName("btnRevisar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnCerrar = new JButton("Cerrar");
        btnRevisar.addActionListener(e -> revisarClick());
        btnActualizar.addActionListener(e -> mostrarSolicitudesPendientes());
        btnCerrar.addActionListener(e -> setVisible(false));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnActualizar);
        botones.add(btnRevisar);
        botones.add(btnCerrar);
        JPanel sur = new JPanel(new BorderLayout());
        sur.add(lblAviso, BorderLayout.CENTER);
        sur.add(botones, BorderLayout.EAST);

        JLabel titulo = new JLabel("Gestionar solicitudes");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        JPanel raiz = new JPanel(new BorderLayout(0, 6));
        raiz.setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 12));
        raiz.add(titulo, BorderLayout.NORTH);
        raiz.add(centro, BorderLayout.CENTER);
        raiz.add(sur, BorderLayout.SOUTH);
        setContentPane(raiz);
        setSize(860, 560);
        setLocationRelativeTo(null);
    }

    public void mostrarSolicitudesPendientes() {
        pendientes = proponer.listarPendientes();
        llenar(modeloPendientes, pendientes);
        llenar(modeloResultados, resultados);
    }

    public void seleccionarSolicitud(int idCita) {
        try {
            Cita cita = proponer.proponerCita(idCita);
            mostrarResultado(cita);
            if (cita.getEstado() == EstadoCita.PROPUESTA) {
                JOptionPane.showMessageDialog(this, "Hay barbero disponible.\nCita #" + cita.getId() + " propuesta con "
                        + cita.getBarbero().getNombre() + ".\nEl cliente vera la propuesta.", "Propuesta enviada",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No hay barbero disponible para la cita #" + cita.getId()
                        + ".\nSe enviaron horarios alternativos al cliente.\nEl cliente debe entrar a Responder propuesta para reprogramar o cancelar.",
                        "Sin barbero", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    public void notificarSolicitud(Cita cita) {
        lblAviso.setText("Nueva solicitud: cita #" + cita.getId() + " de " + cita.getSolicitante());
        mostrarSolicitudesPendientes();
    }

    public void mostrarResultado(Cita cita) {
        if (!resultados.contains(cita)) {
            resultados.add(cita);
        }
        String barbero = cita.getBarbero() == null ? "" : " con " + cita.getBarbero().getNombre();
        lblAviso.setText("Cita #" + cita.getId() + ": " + cita.getEstado() + barbero);
        mostrarSolicitudesPendientes();
    }

    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void revisarClick() {
        int fila = tblPendientes.getSelectedRow();
        if (fila < 0) {
            mostrarError("Seleccione una solicitud pendiente.");
            return;
        }
        seleccionarSolicitud(pendientes.get(fila).getId());
    }

    private void llenar(DefaultTableModel modelo, ArrayList<Cita> citas) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        modelo.setRowCount(0);
        for (Cita c : citas) {
            modelo.addRow(new Object[]{c.getId(), c.getSolicitante(), c.getServicio().getNombre(), c.getFecha().format(f),
                c.getHora() + " - " + c.getHoraFin(), c.getEstado(), c.getBarbero() == null ? "-" : c.getBarbero().getNombre()});
        }
    }
}

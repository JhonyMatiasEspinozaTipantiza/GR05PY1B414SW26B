package barberia.interfaz;

import barberia.control.MantenerHorariosDeBarberos;
import barberia.entidad.Barbero;
import barberia.entidad.Horario;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.DayOfWeek;
import java.time.LocalTime;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class InterfazAdministrarHorarios extends JDialog {

    private static final String[] DIAS = {"lunes", "martes", "miercoles", "jueves", "viernes", "sabado", "domingo"};

    private MantenerHorariosDeBarberos mantener;

    private JComboBox<Barbero> cmbBarbero = new JComboBox<>();
    private JComboBox<String> cmbDia = new JComboBox<>(DIAS);
    private JComboBox<LocalTime> cmbInicio = new JComboBox<>();
    private JComboBox<LocalTime> cmbFin = new JComboBox<>();
    private DefaultTableModel modelo = new DefaultTableModel(new String[]{"Dia", "Inicio", "Fin"}, 0) {
        @Override
        public boolean isCellEditable(int f, int c) {
            return false;
        }
    };
    private JTable tblHorarios = new JTable(modelo);

    public InterfazAdministrarHorarios(MantenerHorariosDeBarberos mantener) {
        super((java.awt.Frame) null, "Administrar Horarios", false);
        this.mantener = mantener;

        for (LocalTime t = LocalTime.of(6, 0); !t.isAfter(LocalTime.of(22, 0)); t = t.plusMinutes(30)) {
            cmbInicio.addItem(t);
            cmbFin.addItem(t);
        }
        cmbInicio.setSelectedItem(LocalTime.of(9, 0));
        cmbFin.setSelectedItem(LocalTime.of(13, 0));
        cmbBarbero.setName("cmbBarbero");
        cmbDia.setName("cmbDia");
        cmbInicio.setName("cmbInicio");
        cmbFin.setName("cmbFin");
        tblHorarios.setRowHeight(24);
        tblHorarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblHorarios.getSelectionModel().addListSelectionListener(e -> {
            int f = tblHorarios.getSelectedRow();
            if (!e.getValueIsAdjusting() && f >= 0) {
                cmbDia.setSelectedItem(modelo.getValueAt(f, 0));
                cmbInicio.setSelectedItem(modelo.getValueAt(f, 1));
                cmbFin.setSelectedItem(modelo.getValueAt(f, 2));
            }
        });
        cmbBarbero.addActionListener(e -> {
            if (cmbBarbero.getSelectedItem() != null) {
                mostrarHorarios(((Barbero) cmbBarbero.getSelectedItem()).getId());
            }
        });

        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selector.add(new JLabel("Barbero:"));
        selector.add(cmbBarbero);

        JPanel tabla = new JPanel(new BorderLayout());
        tabla.setBorder(BorderFactory.createTitledBorder("Horarios actuales del barbero"));
        tabla.add(new JScrollPane(tblHorarios));

        JPanel edicion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        edicion.setBorder(BorderFactory.createTitledBorder("Dia, hora de inicio y hora de fin"));
        edicion.add(new JLabel("Dia:"));
        edicion.add(cmbDia);
        edicion.add(new JLabel("Inicio:"));
        edicion.add(cmbInicio);
        edicion.add(new JLabel("Fin:"));
        edicion.add(cmbFin);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setName("btnGuardarHorario");
        JButton btnEditar = new JButton("Editar seleccionado");
        JButton btnEliminar = new JButton("Eliminar seleccionado");
        JButton btnCerrar = new JButton("Cerrar");
        btnGuardar.addActionListener(e -> guardarClick());
        btnEditar.addActionListener(e -> editarClick());
        btnEliminar.addActionListener(e -> eliminarClick());
        btnCerrar.addActionListener(e -> setVisible(false));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnEliminar);
        botones.add(btnEditar);
        botones.add(btnGuardar);
        botones.add(btnCerrar);

        JPanel sur = new JPanel(new BorderLayout());
        sur.add(edicion, BorderLayout.CENTER);
        sur.add(botones, BorderLayout.SOUTH);

        JLabel titulo = new JLabel("Administrar horarios");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        JPanel norte = new JPanel(new BorderLayout());
        norte.add(titulo, BorderLayout.NORTH);
        norte.add(selector, BorderLayout.CENTER);

        JPanel raiz = new JPanel(new BorderLayout(0, 6));
        raiz.setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 12));
        raiz.add(norte, BorderLayout.NORTH);
        raiz.add(tabla, BorderLayout.CENTER);
        raiz.add(sur, BorderLayout.SOUTH);
        setContentPane(raiz);
        setSize(640, 520);
        setLocationRelativeTo(null);
    }

    // al abrir la ventana se cargan los barberos y los horarios del primero
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            cmbBarbero.removeAllItems();
            for (Barbero b : mantener.listarBarberos()) {
                cmbBarbero.addItem(b);
            }
            if (cmbBarbero.getItemCount() > 0) {
                cmbBarbero.setSelectedIndex(0);
                mostrarHorarios(idBarbero());
            }
        }
        super.setVisible(visible);
    }

    public void mostrarHorarios(int idBarbero) {
        modelo.setRowCount(0);
        for (Horario h : mantener.listarHorarios(idBarbero)) {
            modelo.addRow(new Object[]{DIAS[h.getDia().getValue() - 1], h.getInicio(), h.getFin()});
        }
    }

    public void guardarHorario(int idBarbero, DayOfWeek dia, LocalTime inicio, LocalTime fin) {
        try {
            mantener.crearHorario(idBarbero, dia, inicio, fin);
            mostrarHorarios(idBarbero);
            mostrarMensaje("Horario guardado.");
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    public void editarHorario(int idBarbero, DayOfWeek dia, LocalTime ini, LocalTime ini2, LocalTime fin2) {
        try {
            mantener.editarHorario(idBarbero, dia, ini, ini2, fin2);
            mostrarHorarios(idBarbero);
            mostrarMensaje("Horario actualizado.");
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    public void eliminarHorario(int idBarbero, DayOfWeek dia, LocalTime inicio) {
        try {
            mantener.eliminarHorario(idBarbero, dia, inicio);
            mostrarHorarios(idBarbero);
            mostrarMensaje("Horario eliminado.");
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

    private int idBarbero() {
        return ((Barbero) cmbBarbero.getSelectedItem()).getId();
    }

    private DayOfWeek dia() {
        return DayOfWeek.of(cmbDia.getSelectedIndex() + 1);
    }

    private void guardarClick() {
        if (cmbBarbero.getSelectedItem() == null) {
            mostrarError("Seleccione un barbero.");
            return;
        }
        guardarHorario(idBarbero(), dia(), (LocalTime) cmbInicio.getSelectedItem(), (LocalTime) cmbFin.getSelectedItem());
    }

    private void editarClick() {
        int f = tblHorarios.getSelectedRow();
        if (f < 0) {
            mostrarError("Seleccione el horario que desea editar.");
            return;
        }
        DayOfWeek diaFila = DayOfWeek.of(java.util.Arrays.asList(DIAS).indexOf(modelo.getValueAt(f, 0)) + 1);
        if (dia() != diaFila) {
            mostrarError("Para cambiar el dia elimine el horario y cree uno nuevo.");
            return;
        }
        editarHorario(idBarbero(), diaFila, (LocalTime) modelo.getValueAt(f, 1), (LocalTime) cmbInicio.getSelectedItem(),
                (LocalTime) cmbFin.getSelectedItem());
    }

    private void eliminarClick() {
        int f = tblHorarios.getSelectedRow();
        if (f < 0) {
            mostrarError("Seleccione el horario que desea eliminar.");
            return;
        }
        DayOfWeek diaFila = DayOfWeek.of(java.util.Arrays.asList(DIAS).indexOf(modelo.getValueAt(f, 0)) + 1);
        LocalTime inicio = (LocalTime) modelo.getValueAt(f, 1);
        String texto = modelo.getValueAt(f, 0) + " " + inicio + " - " + modelo.getValueAt(f, 2);
        int r = JOptionPane.showConfirmDialog(this, "Eliminar el horario " + texto + "?", "Eliminar horario", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            eliminarHorario(idBarbero(), diaFila, inicio);
        }
    }
}

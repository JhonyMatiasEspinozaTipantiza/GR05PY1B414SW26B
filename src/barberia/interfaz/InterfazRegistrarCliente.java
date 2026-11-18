package barberia.interfaz;

import barberia.control.RegistrarCliente;
import barberia.entidad.Cliente;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

public class InterfazRegistrarCliente extends JDialog {

    private RegistrarCliente registrar;
    private Cliente cliente;

    private JTextField txtCedula = new JTextField(18);
    private JTextField txtNombres = new JTextField(18);
    private JTextField txtApellidos = new JTextField(18);
    private JTextField txtTelefono = new JTextField(18);
    private JTextField txtCorreo = new JTextField(18);
    private JPasswordField txtClave = new JPasswordField(18);
    private JPasswordField txtConfirmar = new JPasswordField(18);

    public InterfazRegistrarCliente(RegistrarCliente registrar) {
        super((java.awt.Frame) null, "Registrar Cliente", true);
        this.registrar = registrar;

        filtro(txtCedula, "\\d*", 10);
        filtro(txtNombres, "[\\p{L} ]*", 40);
        filtro(txtApellidos, "[\\p{L} ]*", 40);
        filtro(txtTelefono, "\\d*", 10);
        filtro(txtCorreo, "[^\\s;]*", 60);
        filtro(txtClave, "[^\\s;]*", 20);
        filtro(txtConfirmar, "[^\\s;]*", 20);
        txtCedula.setName("regCedula");
        txtNombres.setName("regNombres");
        txtApellidos.setName("regApellidos");
        txtTelefono.setName("regTelefono");
        txtCorreo.setName("regCorreo");
        txtClave.setName("regClave");
        txtConfirmar.setName("regConfirmar");

        JLabel titulo = new JLabel("Registro de cliente");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 24, 8, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;
        form.add(titulo, g);
        g.gridwidth = 1;
        String[] etiquetas = {"Cedula:", "Nombres:", "Apellidos:", "Telefono:", "Correo:", "Clave:", "Confirmar clave:"};
        JTextField[] campos = {txtCedula, txtNombres, txtApellidos, txtTelefono, txtCorreo, txtClave, txtConfirmar};
        for (int i = 0; i < campos.length; i++) {
            g.gridx = 0;
            g.gridy = i + 1;
            form.add(new JLabel(etiquetas[i]), g);
            g.gridx = 1;
            form.add(campos[i], g);
        }
        g.gridx = 0;
        g.gridy = campos.length + 1;
        g.gridwidth = 2;
        form.add(new JLabel("<html><small>Cedula: 10 numeros - Telefono: 09XXXXXXXX - Clave: minimo 4 caracteres</small></html>"), g);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setName("btnRegistrar");
        JButton btnCancelar = new JButton("Cancelar");
        btnRegistrar.addActionListener(e -> registrarClick());
        btnCancelar.addActionListener(e -> setVisible(false));
        getRootPane().setDefaultButton(btnRegistrar);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCancelar);
        botones.add(btnRegistrar);

        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public void solicitarDatos() {
        cliente = null;
        for (JTextField t : new JTextField[]{txtCedula, txtNombres, txtApellidos, txtTelefono, txtCorreo, txtClave, txtConfirmar}) {
            t.setText("");
        }
        setVisible(true);
    }

    public Cliente registrarDatos(String cedula, String nombres, String apellidos, String telefono, String correo, String clave) {
        return registrar.registrar(cedula, nombres, apellidos, telefono, correo, clave);
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void mostrarMensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Barberia", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void registrarClick() {
        String clave = new String(txtClave.getPassword());
        if (!clave.equals(new String(txtConfirmar.getPassword()))) {
            mostrarError("Las claves no coinciden.");
            return;
        }
        try {
            cliente = registrarDatos(txtCedula.getText(), txtNombres.getText(), txtApellidos.getText(),
                    txtTelefono.getText(), txtCorreo.getText(), clave);
            mostrarMensaje("Registro exitoso. Bienvenido " + cliente + ".");
            setVisible(false);
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private static void filtro(JTextComponent campo, String patron, int maximo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attr) throws BadLocationException {
                replace(fb, offset, 0, texto, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int largo, String texto, AttributeSet attrs) throws BadLocationException {
                if (texto == null) {
                    texto = "";
                }
                if (texto.matches(patron) && fb.getDocument().getLength() - largo + texto.length() <= maximo) {
                    super.replace(fb, offset, largo, texto, attrs);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
    }
}

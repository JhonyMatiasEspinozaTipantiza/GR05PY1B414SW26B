package barberia.interfaz;

import barberia.control.ValidarAcceso;
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

public class InterfazLogin extends JDialog {

    private ValidarAcceso validar;
    private InterfazRegistrarCliente interfazRegistrar;
    private Cliente cliente;

    private JTextField txtCedula = new JTextField(14);
    private JPasswordField txtClave = new JPasswordField(14);

    public InterfazLogin(ValidarAcceso validar, InterfazRegistrarCliente interfazRegistrar) {
        super((java.awt.Frame) null, "Login", true);
        this.validar = validar;
        this.interfazRegistrar = interfazRegistrar;

        txtCedula.setName("txtCedula");
        txtClave.setName("txtClave");
        filtro(txtCedula, "\\d*", 10);
        filtro(txtClave, "[^\\s;]*", 20);

        JLabel titulo = new JLabel("Iniciar sesion");
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
        g.gridwidth = 1;
        g.gridy = 1;
        form.add(new JLabel("Cedula:"), g);
        g.gridx = 1;
        form.add(txtCedula, g);
        g.gridx = 0;
        g.gridy = 2;
        form.add(new JLabel("Clave:"), g);
        g.gridx = 1;
        form.add(txtClave, g);

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.setName("btnIngresar");
        JButton btnRegistrar = new JButton("Registrarme");
        JButton btnCancelar = new JButton("Cancelar");
        btnIngresar.addActionListener(e -> ingresar());
        btnRegistrar.addActionListener(e -> registrarse());
        btnCancelar.addActionListener(e -> setVisible(false));
        getRootPane().setDefaultButton(btnIngresar);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnRegistrar);
        botones.add(btnCancelar);
        botones.add(btnIngresar);

        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            cliente = null;
            txtCedula.setText("");
            txtClave.setText("");
        }
        super.setVisible(visible);
    }

    public Cliente iniciarSesion(String cedula, String clave) {
        return validar.iniciarSesion(cedula, clave);
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

    private void ingresar() {
        String cedula = txtCedula.getText().trim();
        String clave = new String(txtClave.getPassword());
        if (cedula.isEmpty() || clave.isEmpty()) {
            mostrarError("Ingrese la cedula y la clave.");
            return;
        }
        if (cedula.length() != 10) {
            mostrarError("La cedula debe tener 10 numeros.");
            return;
        }
        try {
            Cliente c = iniciarSesion(cedula, clave);
            if (c == null) {
                mostrarMensaje("La cedula " + cedula + " no esta registrada. Complete sus datos para registrarse.");
                registrarse();
            } else {
                cliente = c;
                super.setVisible(false);
            }
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void registrarse() {
        interfazRegistrar.solicitarDatos();
        if (interfazRegistrar.getCliente() != null) {
            cliente = interfazRegistrar.getCliente();
            super.setVisible(false);
        }
    }

    // no deja escribir lo que no cumpla el patron
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

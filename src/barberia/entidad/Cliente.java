package barberia.entidad;

import java.util.ArrayList;

public class Cliente {

    private String cedula;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private String clave;
    private ArrayList<Cita> citas = new ArrayList<>();

    public Cliente(String cedula, String nombres, String apellidos, String telefono, String correo, String clave) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correo = correo;
        this.clave = clave;
    }

    public boolean validarClave(String clave) {
        return this.clave.equals(clave);
    }

    public void agregarCita(Cita cita) {
        if (!citas.contains(cita)) {
            citas.add(cita);
        }
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getClave() {
        return clave;
    }

    public ArrayList<Cita> getCitas() {
        return citas;
    }

    @Override
    public String toString() {
        return nombres + " " + apellidos;
    }
}

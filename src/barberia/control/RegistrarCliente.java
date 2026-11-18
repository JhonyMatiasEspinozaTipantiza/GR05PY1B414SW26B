package barberia.control;

import barberia.entidad.Cliente;
import barberia.persistencia.Consultas;
import barberia.persistencia.IAlmacen;

public class RegistrarCliente {

    private Consultas consultas;
    private IAlmacen almacen;

    public RegistrarCliente(Consultas consultas, IAlmacen almacen) {
        this.consultas = consultas;
        this.almacen = almacen;
    }

    public Cliente registrar(String cedula, String nombres, String apellidos, String telefono, String correo, String clave) {
        cedula = cedula.trim();
        nombres = nombres.trim();
        apellidos = apellidos.trim();
        telefono = telefono.trim();
        correo = correo.trim();

        if (cedula.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || telefono.isEmpty()
                || correo.isEmpty() || clave.isEmpty()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }
        if (!cedula.matches("\\d{10}")) {
            throw new IllegalArgumentException("La cedula debe tener 10 numeros.");
        }
        if (!nombres.matches("[\\p{L} ]+") || !apellidos.matches("[\\p{L} ]+")) {
            throw new IllegalArgumentException("Los nombres y apellidos solo pueden tener letras.");
        }
        if (!telefono.matches("09\\d{8}")) {
            throw new IllegalArgumentException("El telefono debe tener 10 numeros y empezar con 09.");
        }
        if (!correo.matches("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+")) {
            throw new IllegalArgumentException("El correo no es valido (ejemplo: nombre@correo.com).");
        }
        if (clave.length() < 4) {
            throw new IllegalArgumentException("La clave debe tener al menos 4 caracteres.");
        }
        if (consultas.buscarCliente(cedula) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con esa cedula.");
        }
        Cliente cliente = new Cliente(cedula, nombres, apellidos, telefono, correo, clave);
        almacen.guardarCliente(cliente);
        return cliente;
    }
}

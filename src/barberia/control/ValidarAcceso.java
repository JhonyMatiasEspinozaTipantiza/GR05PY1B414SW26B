package barberia.control;

import barberia.entidad.Cliente;
import barberia.persistencia.Consultas;

public class ValidarAcceso {

    private Consultas consultas;

    public ValidarAcceso(Consultas consultas) {
        this.consultas = consultas;
    }

    // devuelve null si la cedula no esta registrada
    public Cliente iniciarSesion(String cedula, String clave) {
        Cliente cliente = consultas.buscarCliente(cedula);
        if (cliente == null) {
            return null;
        }
        if (!cliente.validarClave(clave)) {
            throw new IllegalArgumentException("La clave es incorrecta.");
        }
        return cliente;
    }
}

package barberia.entidad;

public enum EstadoCita {
    PENDIENTE("Pendiente"),
    PROPUESTA("Propuesta"),
    AGENDADA("Agendada"),
    CANCELADA("Cancelada"),
    SIN_BARBERO("Sin barbero");

    private final String texto;

    EstadoCita(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}

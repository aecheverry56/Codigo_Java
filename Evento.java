import java.time.Instant;
import java.util.Objects;

/** Evento inmutable. Menor prioridad numérica significa mayor urgencia. */
public class Evento {
    private final int id;
    private final Instant fechaCreacion;
    private final String descripcion;
    private final int prioridad;

    public Evento(int id, Instant fechaCreacion,
                  String descripcion, int prioridad) {
        if (id < 0 || prioridad < 0 || prioridad > 9)
            throw new IllegalArgumentException("id >= 0, prioridad 0..9");
        this.id = id;
        this.fechaCreacion = Objects.requireNonNull(fechaCreacion);
        this.descripcion = Objects.requireNonNull(descripcion);
        this.prioridad = prioridad;
    }
    public int getId() { return id; }
    public Instant getFechaCreacion() { return fechaCreacion; }
    public String getDescripcion() { return descripcion; }
    public int getPrioridad() { return prioridad; }
    @Override public String toString() {
        return id + ":p" + prioridad;
    }
}

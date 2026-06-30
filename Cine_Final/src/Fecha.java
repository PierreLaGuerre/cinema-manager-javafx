import java.io.Serializable;

public class Fecha implements Serializable, Comparable<Fecha> {
    private static final long serialVersionUID = 1L;
    private int hora;
    private int minutos;

    public Fecha(int hora, int minutos) {
        this.hora = hora;
        this.minutos = minutos;
    }

    @Override
    public int compareTo(Fecha otra) {
        if (this.hora != otra.hora) {
            return Integer.compare(this.hora, otra.hora);
        } else {
            return Integer.compare(this.minutos, otra.minutos);
        }
    }

    @Override
    public String toString() {
        return hora + ":" + minutos;
    }
}
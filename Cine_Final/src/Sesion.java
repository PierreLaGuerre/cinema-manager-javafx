import java.io.Serializable;

public class Sesion implements Serializable {
    private static final long serialVersionUID = 4L;

    private String PeliculaId;
    private int salaId;
    private Fecha hora;
    private int entradasVendidas;

    public Sesion(String peliculaId, int salaId, Fecha hora, int entradasVendidas) {
        this.PeliculaId = peliculaId;
        this.salaId = salaId;
        this.hora = hora;
        this.entradasVendidas = entradasVendidas;
    }

    public String getPeliculaID() {
        return PeliculaId;
    }

    public int getSalaId() {
        return salaId;
    }

    public Fecha getHora() {
        return hora;
    }

    public int getEntradasVendidas() {
        return entradasVendidas;
    }

    public void setEntradasVendidas(int entradasVendidas) {
        this.entradasVendidas = entradasVendidas;
    }

    @Override
    public String toString() {
        return "Película: " + PeliculaId +
                ", Sala: " + salaId +
                ", Hora: " + hora +
                ", Entradas vendidas: " + entradasVendidas;
    }
}

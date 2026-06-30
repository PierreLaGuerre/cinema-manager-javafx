import java.io.Serializable;

public class Sala implements Serializable{
    private static final long serialVersionUID = 3L;
    private int numeroSala;
    private int capacidad;

    public Sala(int numeroSala, int capacidad){
        this.numeroSala = numeroSala;
        this.capacidad = capacidad;
    }

    public int getNumeroSala() {
        return numeroSala;
    }

    @Override
    public String toString() {
        return this.numeroSala+ " sala";
    }

    public int getCapacidad() {
        return capacidad;
    }
}

import java.io.Serializable;

public class Pelicula implements Serializable {
    private static final long serialVersionUID = 2L;
    private String nombre;
    private String director;
    private int duracion;

    public Pelicula(String nombre, String director, int duracion){
        this.nombre = nombre;
        this.director = director;
        this.duracion = duracion;
    }


    public String getNombre() {
        return nombre;
    }

    public String getDirector() {
        return director;
    }

    public int getDuracion() {
        return duracion;
    }



    
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Pelicula)) return false;
    Pelicula otra = (Pelicula) obj;
    return this.nombre.equals(otra.nombre);
}

@Override
public int hashCode() {
    return nombre.hashCode();
}

@Override
public String toString() {
    return nombre;
}

}

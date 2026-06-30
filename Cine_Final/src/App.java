import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class App {

    private static HashMap<Pelicula, ArrayList<Sesion>> LISTADO = new HashMap<>();

    private static ArrayList<Sesion> SESIONES = new ArrayList<>();
    private static ArrayList<Sala> SALAS = new ArrayList<>();
    private static ArrayList<Pelicula> PELICULAS = new ArrayList<>();

    private static final Scanner SC = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

        // leerFicheroBinario();
        // cargarMapa();
        
        // exportarArrays();
        leerFicheroDeTexto();
        cargarMapa();
        menuAdmin();
    }

    private static void menuAdmin() {
        boolean seguir = true;

        while (seguir) {
            System.out.println("1 - Ver listado películas");
            System.out.println("2 - Añadir sesión");
            System.out.println("3 - Eliminar sesión");
            System.out.println("4 - Salir");

            int op = pedirNumero();

            switch (op) {
                case 1:
                    imprimirPeliculas();
                    break;

                case 2:
                    añadirSesion();
                    break;

                case 3:
                    eliminarSesion();

                case 4:
                    exportarSesionesOrdenadasEnBinario();
                    seguir = false;
                    break;
            }
        }
    }

    public static void imprimirPeliculas() {
        for (Pelicula p : LISTADO.keySet()) {
            System.out.println(p);
        }
    }

    public static void añadirSesion() {
        System.out.println("Introduce el nombre de la película:");
        SC.nextLine(); // limpia buffer
        String peliculaId = SC.nextLine();

        Pelicula peli = buscarPeliculaPorNombre(peliculaId);

        if (peli == null) {
            System.out.println("Película no encontrada.");
            return;
        }

        System.out.println("Introduce el ID de la sala:");
        int salaId = pedirNumero();

        Sala sala = buscarSalaPorId(salaId);
        if (sala == null) {
            System.out.println("Sala no encontrada.");
            return;
        }

        System.out.println("Introduce la hora de la sesión:");
        int hora = pedirNumero();
        System.out.println("Introduce los minutos:");
        int minutos = pedirNumero();

        Fecha f = new Fecha(hora, minutos);

        System.out.println("Introduce el número de entradas vendidas:");
        int entradas = pedirNumero();

        Sesion nueva = new Sesion(peliculaId, salaId, f, entradas);
        SESIONES.add(nueva);

        // Añadir también al mapa
        if (!LISTADO.containsKey(peli)) {
            LISTADO.put(peli, new ArrayList<Sesion>());
        }
        LISTADO.get(peli).add(nueva);

        System.out.println("Sesión añadida correctamente.");
    }

    private static void eliminarSesion() {
        boolean seguir2 = true;
        while (seguir2) {
            System.out.println("Introduce el nombre de la película que quieres ver\nEscribe 'exit' para salir.");
            String st = SC.nextLine();

            if (st.equalsIgnoreCase("exit")) {
                seguir2 = false;
            } else {
                Pelicula p = buscarPeliculaPorNombre(st);

                if (p == null) {
                    System.out.println(" La película no existe");
                } else {
                    seguir2 = false;

                    if (LISTADO.get(p).size() > 0) {
                        System.out.println(" Película encontrada, mostrando sesiones...");
                        ArrayList<Sesion> valorAsociado = LISTADO.get(p);

                        Iterator<Sesion> it = valorAsociado.iterator();

                        while (it.hasNext()) {
                            Sesion aux = it.next();
                            System.out.println("¿Quieres borrar la sesión?: " + aux + "\n(s/n)");

                            String respuesta = SC.nextLine();
                            if (respuesta.equalsIgnoreCase("s")) {
                                SESIONES.remove(aux);
                                it.remove();
                            }
                        }

                    } else {
                        System.out.println("Ahora mismo no hay sesiones disponibles para esa película.");
                    }
                }
            }
        }
    }

    public static void exportarSesionesOrdenadas() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("sesiones_ordenadas.txt"));

            // Copiar las claves del mapa a una lista
            ArrayList<Pelicula> peliculasOrdenadas = new ArrayList<>(LISTADO.keySet());

            // Ordenar alfabéticamente por nombre
            Collections.sort(peliculasOrdenadas, new Comparator<Pelicula>() {
                public int compare(Pelicula p1, Pelicula p2) {
                    return p1.getNombre().compareTo(p2.getNombre());
                }
            });

            // Recorrer las películas ya ordenadas
            for (Pelicula p : peliculasOrdenadas) {
                bw.write("Película: " + p.getNombre());
                bw.newLine();

                ArrayList<Sesion> sesiones = LISTADO.get(p);

                // Ordenar sesiones por hora
                Collections.sort(sesiones, new Comparator<Sesion>() {
                    public int compare(Sesion s1, Sesion s2) {
                        return s1.getHora().compareTo(s2.getHora());
                    }
                });

                // Escribir sesiones al fichero
                for (Sesion s : sesiones) {
                    bw.write("  Sala: " + s.getSalaId() +
                            " - Hora: " + s.getHora() +
                            " - Entradas vendidas: " + s.getEntradasVendidas());
                    bw.newLine();
                }

                bw.newLine();
            }

            bw.close();
            System.out.println("Sesiones exportadas correctamente.");

        } catch (Exception e) {
            System.out.println("Error al exportar sesiones: " + e.getMessage());
        }
    }

    public static void exportarArrays() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("arraysenfichero.txt"));

            for (Sesion s : SESIONES) {
                bw.write("SESION-"
                        + s.getPeliculaID() + "-"
                        + s.getSalaId() + "-" +
                        s.getHora() + "-" +
                        s.getEntradasVendidas());
                bw.newLine();
            }

            for (Pelicula p : PELICULAS) {
                bw.write(
                        "PELICULA-"
                                + p.getNombre() + "-" +
                                p.getDirector() + "-" +
                                p.getDuracion());
                bw.newLine();
            }

            for (Sala sala : SALAS) {
                bw.write("SALA-" +
                        sala.getNumeroSala() + "-"
                        + sala.getCapacidad());
                bw.newLine();
            }

            bw.close();

            System.out.println("Arrays exportados correctamente.");

        } catch (Exception e) {
            System.out.println("Error al exportar sesiones: " + e.getMessage());
        }
    }

    public static void leerFicheroDeTexto() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("arraysenfichero.txt"));
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("-");

                switch (partes[0]) {

                    case "SESION":
                        String peliculaId = partes[1];
                        int salaId = Integer.parseInt(partes[2]);

                        String[] horaMin = partes[3].split(":");
                        int hora = Integer.parseInt(horaMin[0]);
                        int minutos = Integer.parseInt(horaMin[1]);

                        int entradas = Integer.parseInt(partes[4]);

                        Fecha f = new Fecha(hora, minutos);
                        Sesion s = new Sesion(peliculaId, salaId, f, entradas);
                        SESIONES.add(s);
                        break;

                    case "PELICULA":
                        String nombre = partes[1];
                        String director = partes[2];
                        int duracion = Integer.parseInt(partes[3]);

                        Pelicula p = new Pelicula(nombre, director, duracion);
                        PELICULAS.add(p);
                        break;

                    case "SALA":
                        int numeroSala = Integer.parseInt(partes[1]);
                        int capacidad = Integer.parseInt(partes[2]);

                        Sala sala = new Sala(numeroSala, capacidad);
                        SALAS.add(sala);
                        break;
                }
            }

            br.close();
            System.out.println(" Datos cargados correctamente desde el fichero de texto.");

        } catch (Exception e) {
            System.out.println("Error al leer el fichero: " + e.getMessage());
        }
    }

    public static void exportarSesionesOrdenadasEnBinario() {
        // Asegurarse de que el fichero existe
        File file = new File("sesiones_ordenadas.dat");
        if (file.exists() == false) {
            try {
                file.createNewFile(); // Crear el fichero si no existe
            } catch (Exception e) {
                System.out.println("Error al crear el fichero: " + e.getMessage());
            }
        }

        try {

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));

            // Copiar las claves del mapa a una lista
            ArrayList<Pelicula> peliculasOrdenadas = new ArrayList<>(LISTADO.keySet());

            // Ordenar alfabéticamente por nombre
            Collections.sort(peliculasOrdenadas, new Comparator<Pelicula>() {
                public int compare(Pelicula p1, Pelicula p2) {
                    return p1.getNombre().compareTo(p2.getNombre());
                }
            });

            // Recorrer las películas ya ordenadas
            for (Pelicula p : peliculasOrdenadas) {
                oos.writeObject(p);

                ArrayList<Sesion> sesiones = LISTADO.get(p);

                // Ordenar sesiones por hora
                Collections.sort(sesiones, new Comparator<Sesion>() {
                    public int compare(Sesion s1, Sesion s2) {
                        return s1.getHora().compareTo(s2.getHora());
                    }
                });

                // Escribir sesiones al fichero
                for (Sesion s : sesiones) {
                    oos.writeObject(s);
                }
                oos.writeObject("\n");

                // Escrbir Salas al fichero
                for (Sala sala : SALAS) {
                    oos.writeObject(sala);
                }
            }

            oos.close();
            System.out.println("Sesiones exportadas correctamente.");

        } catch (Exception e) {
            System.out.println("Error al exportar sesiones: " + e.getMessage());
        }
    }

    private static void menuGuest() {
        boolean seguir = true;

        while (seguir) {
            System.out.println("1 - Ver listado películas");
            System.out.println("2 - Comprar entradas");
            System.out.println("3 - Salir");

            int op = pedirNumero();

            switch (op) {
                case 1:
                    verListadoPeliculaYSesiones();
                    break;

                case 2:
                    comprarEntradas();
                    break;

                case 3:
                    seguir = false;
                    break;
            }
        }
    }

    public static void verListadoPeliculaYSesiones() {
        if (LISTADO.isEmpty()) {
            System.out.println("No hay películas disponibles.");
        } else {
            for (Map.Entry<Pelicula, ArrayList<Sesion>> entry : LISTADO.entrySet()) {
                Pelicula peli = entry.getKey();
                ArrayList<Sesion> sesiones = entry.getValue();

                System.out.println("\nPelícula: " + peli.getNombre());
                System.out.println("Sesiones disponibles:");
                for (Sesion s : sesiones) {
                    System.out.println("  - Sala " + s.getSalaId() +
                            ", Hora: " + s.getHora() +
                            ", Entradas vendidas: " + s.getEntradasVendidas());
                }
            }
        }
    }

    public static void comprarEntradas() {
        boolean seguir2 = true;
        while (seguir2) {
            System.out.println("Introduce el nombre de la película que quieres ver o escribe 'exit' para salir.");
            String st = SC.nextLine();

            if (st.equalsIgnoreCase("exit")) {
                seguir2 = false;
            } else {
                Pelicula p = buscarPeliculaPorNombre(st);
                if (p == null) {
                    System.out.println("La película no está en taquilla, introduce otra.");
                } else {
                    ArrayList<Sesion> sesiones = LISTADO.get(p);

                    if (sesiones == null || sesiones.isEmpty()) {
                        System.out.println("No hay sesiones disponibles para esta película.");
                        return;
                    }

                    System.out.println("Película encontrada, elige una sesión:");
                    for (int i = 0; i < sesiones.size(); i++) {
                        System.out.println((i + 1) + " - " + sesiones.get(i));
                    }

                    System.out.println("Selecciona el número de sesión:");
                    int seleccion = pedirNumero();

                    if (seleccion > 0 && seleccion <= sesiones.size()) {
                        Sesion sesionElegida = sesiones.get(seleccion - 1);
                        // Aumento las entradas vendidas
                        sesionElegida.setEntradasVendidas(sesionElegida.getEntradasVendidas() + 1);
                        System.out.println("Entrada comprada para la sesión: " + sesionElegida);
                    } else {
                        System.out.println("Opción inválida.");
                    }

                    seguir2 = false;
                }
            }
        }
    }

    public static void leerFicheroBinario() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("sesiones_ordenadas.dat"));

            while (true) {
                try {
                    Object obj = ois.readObject();

                    if (obj instanceof Sala) {
                        SALAS.add((Sala) obj);
                    } else if (obj instanceof Pelicula) {
                        PELICULAS.add((Pelicula) obj);
                    } else if (obj instanceof Sesion) {
                        SESIONES.add((Sesion) obj);
                    }

                } catch (EOFException e) {
                    System.out.println("Fichero bbdd.dat leído correctamente.");
                    break;
                }
            }

            ois.close();

        } catch (Exception e) {
            System.out.println("Error al leer fichero binario: " + e.getMessage());
        }
    }

    public static Pelicula buscarPeliculaPorNombre(String nombre) {
        for (Pelicula p : PELICULAS) {
            if (p.getNombre().equals(nombre)) {
                return p;
            }
        }
        return null;
    }

    private static void cargarMapa() {
        for (Pelicula p : PELICULAS) {
            ArrayList<Sesion> sesiones_p = new ArrayList<>();

            for (Sesion s : SESIONES) {
                if (p.getNombre().equals(s.getPeliculaID())) {
                    sesiones_p.add(s);
                }
            }

            LISTADO.put(p, sesiones_p);
        }
    }

    public static void mostrarListadoPeliculasConSesiones() {
        if (LISTADO.isEmpty()) {
            System.out.println("No hay películas ni sesiones cargadas.");
            return;
        }

        for (Map.Entry<Pelicula, ArrayList<Sesion>> entry : LISTADO.entrySet()) {
            Pelicula peli = entry.getKey();
            ArrayList<Sesion> sesiones = entry.getValue();

            System.out.println("\nPelícula: " + peli.getNombre());
            System.out.println("Sesiones:");

            for (Sesion s : sesiones) {
                System.out.println("  - Sala: " + s.getSalaId() +
                        ", Hora: " + s.getHora() +
                        ", Entradas vendidas: " + s.getEntradasVendidas());
            }
        }
    }

    private static int pedirNumero() {
        int aux = 0;
        boolean ok = false;

        while (ok == false) {
            try {
                aux = SC.nextInt();
                ok = true;
                SC.nextLine(); // limpia buffer
            } catch (Exception e) {
                System.out.println("Por favor introduce un número válido");
                SC.nextLine(); // limpiar buffer
            }
        }
        return aux;
    }

    public static Sala buscarSalaPorId(int id) {
        for (Sala s : SALAS) {
            if (s.getNumeroSala() == id) {
                return s;
            }
        }
        return null;
    }

}

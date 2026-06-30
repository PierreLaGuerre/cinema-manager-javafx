package dev.pierrelaguerre.cinema.repository;

import dev.pierrelaguerre.cinema.model.Auditorium;
import dev.pierrelaguerre.cinema.model.CinemaData;
import dev.pierrelaguerre.cinema.model.Movie;
import dev.pierrelaguerre.cinema.model.Screening;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class FileCinemaRepository implements CinemaRepository {
    private static final String DATA_VERSION = "# cinema-manager-data-v2";
    private final Path dataFile;
    private final String seedResource;

    public FileCinemaRepository(Path dataFile, String seedResource) {
        this.dataFile = dataFile;
        this.seedResource = seedResource;
    }

    public static FileCinemaRepository defaultRepository() {
        Path directory = Path.of(System.getProperty("user.home"), ".cinema-manager");
        return new FileCinemaRepository(directory.resolve("cinema-data.txt"), "/data/cinema-seed.txt");
    }

    @Override
    public CinemaData load() throws IOException {
        if (Files.notExists(dataFile)) copySeed();
        String content = Files.readString(dataFile, StandardCharsets.UTF_8);
        try (BufferedReader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8)) {
            CinemaData current = parse(reader);
            if (!content.startsWith(DATA_VERSION)) {
                CinemaData migrated = migrateToCurrentSeed(current);
                save(migrated);
                return migrated;
            }
            return current;
        }
    }

    @Override
    public void save(CinemaData data) throws IOException {
        Path parent = dataFile.getParent();
        if (parent != null) Files.createDirectories(parent);
        List<String> lines = new ArrayList<>();
        lines.add(DATA_VERSION);
        data.movies().forEach(movie -> lines.add(String.join("|", "MOVIE", movie.id(), movie.titleEs(), movie.titleEn(),
                movie.director(), String.valueOf(movie.durationMinutes()), movie.theme())));
        data.auditoriums().forEach(room -> lines.add("AUDITORIUM|" + room.number() + "|" + room.capacity()));
        data.screenings().forEach(screening -> lines.add(String.join("|", "SCREENING", screening.id(), screening.movieId(),
                String.valueOf(screening.auditoriumNumber()), screening.time().toString(), String.valueOf(screening.soldTickets()))));
        Files.write(dataFile, lines, StandardCharsets.UTF_8);
    }

    private void copySeed() throws IOException {
        Path parent = dataFile.getParent();
        if (parent != null) Files.createDirectories(parent);
        try (InputStream input = getClass().getResourceAsStream(seedResource)) {
            if (input == null) throw new IOException("Seed data not found: " + seedResource);
            Files.copy(input, dataFile);
        }
    }

    private CinemaData migrateToCurrentSeed(CinemaData current) throws IOException {
        CinemaData seed;
        try (InputStream input = getClass().getResourceAsStream(seedResource)) {
            if (input == null) throw new IOException("Seed data not found: " + seedResource);
            seed = parse(new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8)));
        }
        var soldById = current.screenings().stream().collect(java.util.stream.Collectors.toMap(Screening::id, Screening::soldTickets));
        seed.screenings().forEach(screening -> {
            Integer previousSold = soldById.get(screening.id());
            if (previousSold != null && previousSold > screening.soldTickets()) screening.sell(previousSold - screening.soldTickets());
        });
        return seed;
    }

    static CinemaData parse(BufferedReader reader) throws IOException {
        List<Movie> movies = new ArrayList<>();
        List<Auditorium> auditoriums = new ArrayList<>();
        List<Screening> screenings = new ArrayList<>();
        String line;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (line.isBlank() || line.startsWith("#")) continue;
            String[] parts = line.split("\\|", -1);
            try {
                switch (parts[0]) {
                    case "MOVIE" -> movies.add(new Movie(parts[1], parts[2], parts[3], parts[4], Integer.parseInt(parts[5]), parts[6]));
                    case "AUDITORIUM" -> auditoriums.add(new Auditorium(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                    case "SCREENING" -> screenings.add(new Screening(parts[1], parts[2], Integer.parseInt(parts[3]),
                            LocalTime.parse(parts[4]), Integer.parseInt(parts[5])));
                    default -> throw new IllegalArgumentException("Unknown record: " + parts[0]);
                }
            } catch (RuntimeException error) {
                throw new IOException("Invalid cinema data at line " + lineNumber, error);
            }
        }
        return new CinemaData(movies, auditoriums, screenings);
    }
}

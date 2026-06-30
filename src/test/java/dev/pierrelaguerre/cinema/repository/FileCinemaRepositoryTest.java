package dev.pierrelaguerre.cinema.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileCinemaRepositoryTest {
    @TempDir Path directory;

    @Test void copiesSeedLoadsAndPersistsUtf8Data() throws Exception {
        Path file = directory.resolve("data.txt");
        FileCinemaRepository repository = new FileCinemaRepository(file, "/data/cinema-seed.txt");
        var loaded = repository.load();
        assertEquals(9, loaded.movies().size());
        assertEquals("La última luz", loaded.movies().getFirst().titleEs());
        loaded.screenings().getFirst().sell(1);
        repository.save(loaded);
        assertEquals(loaded.screenings().getFirst().soldTickets(), repository.load().screenings().getFirst().soldTickets());
    }
}

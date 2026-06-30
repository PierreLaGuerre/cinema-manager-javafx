package dev.pierrelaguerre.cinema.service;

import dev.pierrelaguerre.cinema.model.Auditorium;
import dev.pierrelaguerre.cinema.model.CinemaData;
import dev.pierrelaguerre.cinema.model.Movie;
import dev.pierrelaguerre.cinema.model.Screening;
import dev.pierrelaguerre.cinema.repository.CinemaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class CinemaServiceTest {
    private MemoryRepository repository;
    private CinemaService service;

    @BeforeEach
    void setUp() throws IOException {
        repository = new MemoryRepository(sampleData());
        service = new CinemaService(repository);
        service.load();
    }

    @Test void loadsAndSortsMoviesByLocalizedTitle() {
        assertEquals(List.of("Aurora", "Órbita"), service.searchMovies("", Locale.forLanguageTag("es")).stream().map(Movie::titleEs).toList());
    }

    @Test void searchesIgnoringCaseAndAccents() {
        assertEquals("orbit", service.searchMovies("ORBITA", Locale.forLanguageTag("es")).getFirst().id());
    }

    @Test void sortsScreeningsByTime() {
        assertEquals(List.of(LocalTime.of(17, 0), LocalTime.of(21, 0)), service.screeningsFor("orbit").stream().map(Screening::time).toList());
    }

    @Test void calculatesAvailabilityAndPersistsPurchase() throws IOException {
        Screening screening = service.screeningsFor("aurora").getFirst();
        assertEquals(8, service.availableSeats(screening));
        service.purchase(screening.id(), 3);
        assertEquals(5, service.availableSeats(screening));
        assertEquals(1, repository.saveCount);
    }

    @Test void rejectsPurchaseAboveCapacity() {
        Screening screening = service.screeningsFor("aurora").getFirst();
        assertThrows(IllegalStateException.class, () -> service.purchase(screening.id(), 9));
    }

    @Test void allowsExactlyTheRemainingCapacityButNeverOneMore() throws IOException {
        Screening screening = service.screeningsFor("aurora").getFirst();
        service.purchase(screening.id(), 8);
        assertEquals(0, service.availableSeats(screening));
        assertThrows(IllegalStateException.class, () -> service.purchase(screening.id(), 1));
    }

    @Test void rejectsNonPositiveQuantity() {
        assertThrows(IllegalArgumentException.class, () -> service.purchase("a-1800", 0));
    }

    private static CinemaData sampleData() {
        Movie orbit = new Movie("orbit", "Órbita", "Orbit", "Ana", 100, "blue");
        Movie aurora = new Movie("aurora", "Aurora", "Aurora", "Zoé", 95, "gold");
        return new CinemaData(List.of(orbit, aurora), List.of(new Auditorium(1, 10)), List.of(
                new Screening("o-2100", "orbit", 1, LocalTime.of(21, 0), 1),
                new Screening("o-1700", "orbit", 1, LocalTime.of(17, 0), 2),
                new Screening("a-1800", "aurora", 1, LocalTime.of(18, 0), 2)));
    }

    private static final class MemoryRepository implements CinemaRepository {
        private final CinemaData data;
        private int saveCount;
        private MemoryRepository(CinemaData data) { this.data = data; }
        @Override public CinemaData load() { return data; }
        @Override public void save(CinemaData data) { saveCount++; }
    }
}

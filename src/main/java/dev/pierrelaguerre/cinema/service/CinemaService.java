package dev.pierrelaguerre.cinema.service;

import dev.pierrelaguerre.cinema.model.Auditorium;
import dev.pierrelaguerre.cinema.model.CinemaData;
import dev.pierrelaguerre.cinema.model.Movie;
import dev.pierrelaguerre.cinema.model.Screening;
import dev.pierrelaguerre.cinema.repository.CinemaRepository;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

public final class CinemaService {
    private final CinemaRepository repository;
    private CinemaData data;

    public CinemaService(CinemaRepository repository) {
        this.repository = repository;
    }

    public void load() throws IOException { data = repository.load(); }

    public List<Movie> searchMovies(String query, Locale locale) {
        ensureLoaded();
        String needle = normalize(query == null ? "" : query);
        return data.movies().stream()
                .filter(movie -> normalize(movie.title(locale)).contains(needle) || normalize(movie.director()).contains(needle))
                .sorted(Comparator.comparing(movie -> movie.title(locale)))
                .toList();
    }

    public List<Screening> screeningsFor(String movieId) {
        ensureLoaded();
        return data.screenings().stream().filter(screening -> screening.movieId().equals(movieId))
                .sorted(Comparator.comparing(Screening::time)).toList();
    }

    public int availableSeats(Screening screening) {
        return capacityFor(screening) - screening.soldTickets();
    }

    public int capacityFor(Screening screening) {
        return data.auditoriums().stream().filter(a -> a.number() == screening.auditoriumNumber()).findFirst()
                .map(Auditorium::capacity).orElseThrow(() -> new NoSuchElementException("Auditorium not found"));
    }

    public synchronized void purchase(String screeningId, int quantity) throws IOException {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        Screening screening = data.screenings().stream().filter(item -> item.id().equals(screeningId)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Screening not found"));
        if (quantity > availableSeats(screening)) throw new IllegalStateException("Not enough seats");
        screening.sell(quantity);
        repository.save(data);
    }

    public CinemaData snapshot() { ensureLoaded(); return data; }

    private void ensureLoaded() {
        if (data == null) throw new IllegalStateException("Cinema data has not been loaded");
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).trim();
    }
}

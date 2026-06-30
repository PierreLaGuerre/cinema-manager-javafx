package dev.pierrelaguerre.cinema.model;

import java.util.List;

public record CinemaData(List<Movie> movies, List<Auditorium> auditoriums, List<Screening> screenings) {
    public CinemaData {
        movies = List.copyOf(movies);
        auditoriums = List.copyOf(auditoriums);
        screenings = List.copyOf(screenings);
    }
}

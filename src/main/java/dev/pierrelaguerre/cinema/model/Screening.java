package dev.pierrelaguerre.cinema.model;

import java.time.LocalTime;

public final class Screening {
    private final String id;
    private final String movieId;
    private final int auditoriumNumber;
    private final LocalTime time;
    private int soldTickets;

    public Screening(String id, String movieId, int auditoriumNumber, LocalTime time, int soldTickets) {
        if (id == null || id.isBlank() || movieId == null || time == null || auditoriumNumber <= 0 || soldTickets < 0) {
            throw new IllegalArgumentException("Invalid screening data");
        }
        this.id = id;
        this.movieId = movieId;
        this.auditoriumNumber = auditoriumNumber;
        this.time = time;
        this.soldTickets = soldTickets;
    }

    public String id() { return id; }
    public String movieId() { return movieId; }
    public int auditoriumNumber() { return auditoriumNumber; }
    public LocalTime time() { return time; }
    public int soldTickets() { return soldTickets; }
    public void sell(int quantity) { soldTickets += quantity; }
}

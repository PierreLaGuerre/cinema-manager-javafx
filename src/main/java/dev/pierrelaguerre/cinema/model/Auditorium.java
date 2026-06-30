package dev.pierrelaguerre.cinema.model;

public record Auditorium(int number, int capacity) {
    public Auditorium {
        if (number <= 0 || capacity <= 0) throw new IllegalArgumentException("Invalid auditorium data");
    }
}

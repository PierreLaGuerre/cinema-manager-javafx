package dev.pierrelaguerre.cinema.repository;

import dev.pierrelaguerre.cinema.model.CinemaData;

import java.io.IOException;

public interface CinemaRepository {
    CinemaData load() throws IOException;
    void save(CinemaData data) throws IOException;
}

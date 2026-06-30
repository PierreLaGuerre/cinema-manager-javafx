package dev.pierrelaguerre.cinema.model;

import java.util.Locale;

public record Movie(String id, String titleEs, String titleEn, String director, int durationMinutes, String theme) {
    public Movie {
        if (id == null || id.isBlank() || titleEs == null || titleEn == null || durationMinutes <= 0) {
            throw new IllegalArgumentException("Invalid movie data");
        }
    }

    public String title(Locale locale) {
        return locale.getLanguage().equals("en") ? titleEn : titleEs;
    }
}

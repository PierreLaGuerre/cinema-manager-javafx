# Cinema Manager

[Español](README.es.md) · [Live portfolio page](https://pierrelaguerre.github.io/cinema-manager-javafx/) · [Download](../../releases/latest)

![Cinema Manager interface](docs/assets/preview.svg)

A bilingual JavaFX cinema programme rebuilt from my first-year Java final project. The original console application demonstrated object-oriented programming, collections and file I/O; this portfolio edition turns that foundation into a polished desktop experience.

## Highlights

- Modern JavaFX interface built with FXML and CSS
- Spanish and English UI with `ResourceBundle`
- Search by title or director, ignoring case and accents
- Screenings sorted by time with live seat availability
- Ticket booking with capacity validation and local persistence
- Layered model, repository, service and UI architecture
- JUnit 5 tests and automated GitHub Actions builds

## Run locally

Requirements: JDK 21 or newer. Maven does not need to be installed.

```powershell
.\mvnw.cmd clean javafx:run
```

On macOS or Linux:

```bash
./mvnw clean javafx:run
```

Bookings are stored at `~/.cinema-manager/cinema-data.txt`. Delete that file to restore the demo data.

## Architecture

```text
JavaFX / FXML → CinemaService → CinemaRepository → UTF-8 local file
```

The movies and artwork are fictional and were created for this demonstration. No real payment is processed.

## From classroom exercise to portfolio project

The untouched exam submission is preserved in the first Git commit and under `Cine_Final/`. The current version replaces the monolithic console flow with testable business rules, Java's `LocalTime`, internationalisation, accessible keyboard navigation and a self-contained desktop distribution.

## License

[MIT](LICENSE)

package dev.pierrelaguerre.cinema.ui;

import dev.pierrelaguerre.cinema.CinemaApplication;
import dev.pierrelaguerre.cinema.model.Movie;
import dev.pierrelaguerre.cinema.model.Screening;
import dev.pierrelaguerre.cinema.service.CinemaService;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public final class CinemaController {
    @FXML private TextField searchField;
    @FXML private FlowPane movieGrid;
    @FXML private VBox detailPanel;
    @FXML private Label detailTitle;
    @FXML private Label detailMeta;
    @FXML private VBox screeningList;
    @FXML private Label movieCount;
    @FXML private Label emptyMessage;
    @FXML private Button languageButton;
    @FXML private StackPane purchaseOverlay;
    @FXML private Label purchaseMovieTitle;
    @FXML private Label purchaseSessionDetail;
    @FXML private Label purchaseAvailability;
    @FXML private Spinner<Integer> ticketQuantity;
    @FXML private Label toastMessage;
    @FXML private StackPane confirmationOverlay;
    @FXML private Label confirmationMovie;
    @FXML private Label confirmationSchedule;
    @FXML private Label confirmationRoom;
    @FXML private Label confirmationTickets;

    private CinemaApplication application;
    private CinemaService service;
    private ResourceBundle text;
    private Locale locale;
    private Screening pendingScreening;
    private Movie selectedMovie;

    public void configure(CinemaApplication application, CinemaService service, ResourceBundle text, Locale locale) {
        this.application = application;
        this.service = service;
        this.text = text;
        this.locale = locale;
        languageButton.setText(locale.getLanguage().equals("es") ? "EN" : "ES");
        try {
            service.load();
            renderMovies();
        } catch (IOException error) {
            showInternalMessage(text.getString("error.load") + ": " + error.getMessage());
        }
        searchField.textProperty().addListener((observable, oldValue, newValue) -> renderMovies());
    }

    @FXML private void switchLanguage() throws IOException { application.switchLanguage(); }

    private void renderMovies() {
        var movies = service.searchMovies(searchField.getText(), locale);
        movieGrid.getChildren().clear();
        movieCount.setText(format("movies.count", movies.size()));
        emptyMessage.setVisible(movies.isEmpty());
        emptyMessage.setManaged(movies.isEmpty());
        movies.forEach(movie -> movieGrid.getChildren().add(createMovieCard(movie)));
        if (!movies.isEmpty()) showMovie(movies.getFirst()); else detailPanel.setVisible(false);
    }

    private VBox createMovieCard(Movie movie) {
        Label monogram = new Label(movie.title(locale).substring(0, 1).toUpperCase(locale));
        monogram.getStyleClass().addAll("poster-letter", "theme-" + movie.theme());
        Label title = new Label(movie.title(locale));
        title.getStyleClass().add("movie-title");
        title.setWrapText(true);
        Label director = new Label(movie.director());
        director.getStyleClass().add("muted");
        VBox card = new VBox(12, monogram, title, director);
        card.getStyleClass().add("movie-card");
        card.setOnMouseClicked(event -> showMovie(movie));
        card.setOnKeyPressed(event -> { if (event.getCode().isWhitespaceKey() || event.getCode().toString().equals("ENTER")) showMovie(movie); });
        card.setFocusTraversable(true);
        return card;
    }

    private void showMovie(Movie movie) {
        selectedMovie = movie;
        detailPanel.setVisible(true);
        detailTitle.setText(movie.title(locale));
        detailMeta.setText(format("movie.meta", movie.director(), movie.durationMinutes()));
        screeningList.getChildren().clear();
        var screenings = service.screeningsFor(movie.id());
        if (screenings.isEmpty()) screeningList.getChildren().add(new Label(text.getString("screenings.empty")));
        screenings.forEach(screening -> screeningList.getChildren().add(createScreeningRow(screening)));
        FadeTransition fade = new FadeTransition(Duration.millis(180), detailPanel);
        fade.setFromValue(0.55); fade.setToValue(1); fade.play();
    }

    private HBox createScreeningRow(Screening screening) {
        int available = service.availableSeats(screening);
        int capacity = service.capacityFor(screening);
        Label time = new Label(screening.time().format(DateTimeFormatter.ofPattern("HH:mm")));
        time.getStyleClass().add("session-time");
        Label room = new Label(format("screening.room", screening.auditoriumNumber()));
        Label seats = new Label(format("screening.capacity", available, capacity));
        seats.getStyleClass().add(available == 0 ? "sold-out" : "availability");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button buy = new Button(available == 0 ? text.getString("screening.soldOut") : text.getString("screening.buy"));
        buy.getStyleClass().add("buy-button"); buy.setDisable(available == 0);
        buy.setOnAction(event -> openPurchase(screening));
        HBox row = new HBox(12, time, room, seats, spacer, buy);
        row.setAlignment(Pos.CENTER_LEFT); row.getStyleClass().add("session-row");
        return row;
    }

    private void openPurchase(Screening screening) {
        int available = service.availableSeats(screening);
        pendingScreening = screening;
        purchaseMovieTitle.setText(selectedMovie.title(locale));
        purchaseSessionDetail.setText(format("purchase.header", screening.time()));
        purchaseAvailability.setText(format("screening.capacity", available, service.capacityFor(screening)));
        ticketQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Math.min(available, 8), 1));
        purchaseOverlay.setManaged(true);
        purchaseOverlay.setVisible(true);
        FadeTransition fade = new FadeTransition(Duration.millis(160), purchaseOverlay);
        fade.setFromValue(0); fade.setToValue(1); fade.play();
        ticketQuantity.requestFocus();
    }

    @FXML private void closePurchase() {
        purchaseOverlay.setVisible(false);
        purchaseOverlay.setManaged(false);
        pendingScreening = null;
    }

    @FXML private void confirmPurchase() {
        if (pendingScreening == null) return;
        int quantity = ticketQuantity.getValue();
        Screening bookedScreening = pendingScreening;
        String bookedMovie = selectedMovie.title(locale);
        try {
            service.purchase(bookedScreening.id(), quantity);
            closePurchase();
            renderMovies();
            showConfirmation(bookedMovie, bookedScreening, quantity);
        } catch (IOException | IllegalStateException error) {
            showInternalMessage(text.getString("purchase.error") + ": " + error.getMessage());
        }
    }

    private void showConfirmation(String movie, Screening screening, int quantity) {
        confirmationMovie.setText(movie);
        confirmationSchedule.setText(format("confirmation.schedule", screening.time().format(DateTimeFormatter.ofPattern("HH:mm"))));
        confirmationRoom.setText(format("confirmation.room", screening.auditoriumNumber()));
        confirmationTickets.setText(format("confirmation.tickets", quantity));
        confirmationOverlay.setManaged(true);
        confirmationOverlay.setVisible(true);
        FadeTransition fade = new FadeTransition(Duration.millis(180), confirmationOverlay);
        fade.setFromValue(0); fade.setToValue(1); fade.play();
    }

    @FXML private void closeConfirmation() {
        confirmationOverlay.setVisible(false);
        confirmationOverlay.setManaged(false);
    }

    private String format(String key, Object... values) {
        return new MessageFormat(text.getString(key), locale).format(values);
    }

    private void showInternalMessage(String message) {
        toastMessage.setText(message);
        toastMessage.setManaged(true);
        toastMessage.setVisible(true);
        FadeTransition fade = new FadeTransition(Duration.seconds(3.2), toastMessage);
        fade.setFromValue(1); fade.setToValue(0); fade.setDelay(Duration.seconds(2));
        fade.setOnFinished(event -> { toastMessage.setVisible(false); toastMessage.setManaged(false); toastMessage.setOpacity(1); });
        fade.playFromStart();
    }
}

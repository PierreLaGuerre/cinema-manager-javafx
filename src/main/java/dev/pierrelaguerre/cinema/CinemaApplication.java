package dev.pierrelaguerre.cinema;

import dev.pierrelaguerre.cinema.repository.FileCinemaRepository;
import dev.pierrelaguerre.cinema.service.CinemaService;
import dev.pierrelaguerre.cinema.ui.CinemaController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public final class CinemaApplication extends Application {
    private Stage stage;
    private Locale locale = Locale.forLanguageTag("es");
    private final CinemaService service = new CinemaService(FileCinemaRepository.defaultRepository());

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.setTitle("Cinema Manager");
        showCinema();
        stage.show();
    }

    public void switchLanguage() throws IOException {
        locale = locale.getLanguage().equals("es") ? Locale.ENGLISH : Locale.forLanguageTag("es");
        showCinema();
    }

    private void showCinema() throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cinema-view.fxml"), bundle);
        Scene scene = new Scene(loader.load(), 1180, 760);
        scene.getStylesheets().add(getClass().getResource("/css/cinema.css").toExternalForm());
        CinemaController controller = loader.getController();
        controller.configure(this, service, bundle, locale);
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

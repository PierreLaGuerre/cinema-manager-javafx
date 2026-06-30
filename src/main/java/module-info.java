module dev.pierrelaguerre.cinema {
    requires javafx.controls;
    requires javafx.fxml;

    exports dev.pierrelaguerre.cinema;
    exports dev.pierrelaguerre.cinema.model;
    opens dev.pierrelaguerre.cinema.ui to javafx.fxml;
}

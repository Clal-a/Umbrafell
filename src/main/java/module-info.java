module com.ced.umbrafell {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.ced.umbrafell to javafx.fxml;
    exports com.ced.umbrafell;
}

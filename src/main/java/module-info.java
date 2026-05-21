module com.ced.umbrafell {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens com.ced.umbrafell to javafx.fxml;
    opens com.ced.umbrafell.controller to javafx.fxml;
     
    exports com.ced.umbrafell;
}

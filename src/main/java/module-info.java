module com.example.gameapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires javafx.media;

    opens com.example.gameapp to javafx.fxml;
    exports com.example.gameapp;
    exports com.example.gameapp.Controller;
    opens com.example.gameapp.Controller to javafx.fxml;
}
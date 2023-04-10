module com.example.maze {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;


    opens com.example.maze to javafx.fxml;
    exports com.example.maze;
}
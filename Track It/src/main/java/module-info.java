module org.example.trackit {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.trackit.model to javafx.base;

    exports org.example.trackit;
}

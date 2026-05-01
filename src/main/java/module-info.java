module com.openlib {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.openlib to javafx.graphics;
    opens com.openlib.model to javafx.base;
    opens com.openlib.view to javafx.graphics;
    opens com.openlib.controller to javafx.graphics;
}

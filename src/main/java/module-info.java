module com.ise.officeescape {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.ise.officeescape to javafx.fxml;
    exports com.ise.officeescape;
}

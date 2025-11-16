module com.ise.officeescape {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.ise.officeescape to javafx.fxml;
    exports com.ise.officeescape;
}
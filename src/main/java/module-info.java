module com.ise.officeescape {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.ise.officeescape to javafx.fxml;
    opens com.ise.officeescape.view to javafx.fxml;
    opens com.ise.officeescape.eventSystem to javafx.fxml;
    
    exports com.ise.officeescape;
    exports com.ise.officeescape.view;
    exports com.ise.officeescape.view.puzzles;
    exports com.ise.officeescape.controller;
    exports com.ise.officeescape.model;
    exports com.ise.officeescape.model.puzzles;
    exports com.ise.officeescape.eventSystem;
    exports com.ise.officeescape.service;
}

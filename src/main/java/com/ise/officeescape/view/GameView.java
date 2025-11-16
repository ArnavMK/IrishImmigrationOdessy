package com.ise.officeescape.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class GameView extends VBox {
    public TextArea output;
    public TextField input;
    public Button send;

    public GameView() {
        setSpacing(10);
        setPadding(new Insets(10));

        output = new TextArea();
        output.setEditable(false);
        output.setWrapText(true);

        input = new TextField();
        send = new Button("Send");

        getChildren().addAll(output, input, send);
    }

    public void showMessage(String msg) {
        output.appendText(msg + "\n");
    }
}

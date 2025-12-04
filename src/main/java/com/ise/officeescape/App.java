package com.ise.officeescape;

import com.ise.officeescape.controller.GameController;
import com.ise.officeescape.view.GameView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        GameView view = new GameView();
        GameController controller = new GameController(view);

        Scene scene = new Scene(view, 1366, 768);
        stage.setScene(scene);
        stage.setTitle("Irish Immigration Adventure");
        stage.show();
        
        // Request focus so keyboard events work
        view.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

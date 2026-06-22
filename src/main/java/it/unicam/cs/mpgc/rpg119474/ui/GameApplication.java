package it.unicam.cs.mpgc.rpg119474.ui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/** Applicazione JavaFX. Per ora mostra solo una finestra iniziale (verifica dell'impalcatura). */
public class GameApplication extends Application {

    @Override
    public void start(Stage stage) {
        Label titolo = new Label("Wasteland — duelli a turni");
        StackPane root = new StackPane(titolo);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Wasteland");
        stage.setScene(scene);
        stage.show();
    }
}
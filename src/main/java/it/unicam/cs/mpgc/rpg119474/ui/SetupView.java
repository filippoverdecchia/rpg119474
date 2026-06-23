package it.unicam.cs.mpgc.rpg119474.ui;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.engine.factory.EnemyFactory;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;

/** Schermata iniziale: crea un nuovo sopravvissuto o caricane uno salvato. */
public class SetupView {

    @FunctionalInterface
    public interface StartHandler {
        void onStart(String name, SurvivorClass survivorClass, Enemy enemy);
    }

    @FunctionalInterface
    public interface LoadHandler {
        void onLoad(String savedId, Enemy enemy);
    }

    private final VBox root = new VBox(12);
    private final TextField nameField = new TextField("Sopravvissuto");
    private final ComboBox<SurvivorClass> classBox = new ComboBox<>();
    private final ComboBox<String> enemyBox = new ComboBox<>();
    private final ComboBox<String> savedBox = new ComboBox<>();

    public SetupView(StartHandler onStart, List<String> savedIds, LoadHandler onLoad) {
        classBox.getItems().setAll(SurvivorClass.values());
        classBox.setValue(SurvivorClass.BRUTO);
        classBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(SurvivorClass survivorClass) {
                return survivorClass == null ? "" : survivorClass.displayName();
            }
            @Override
            public SurvivorClass fromString(String value) {
                return null;
            }
        });

        enemyBox.getItems().setAll("Predone", "Cane mutante", "Ghoul inferocito", "Capo dei predoni");
        enemyBox.setValue("Predone");

        Button startButton = new Button("Inizia duello");
        startButton.setOnAction(event -> {
            String name = (nameField.getText() == null || nameField.getText().isBlank())
                    ? "Sopravvissuto" : nameField.getText().trim();
            onStart.onStart(name, classBox.getValue(), createEnemy(enemyBox.getValue()));
        });

        savedBox.getItems().setAll(savedIds);
        Button loadButton = new Button("Carica e combatti");
        boolean hasSaves = !savedIds.isEmpty();
        savedBox.setDisable(!hasSaves);
        loadButton.setDisable(!hasSaves);
        if (hasSaves) {
            savedBox.setValue(savedIds.get(0));
        }
        loadButton.setOnAction(event -> {
            if (savedBox.getValue() != null) {
                onLoad.onLoad(savedBox.getValue(), createEnemy(enemyBox.getValue()));
            }
        });

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                new Label("Wasteland — crea il tuo sopravvissuto"),
                new Label("Nome:"), nameField,
                new Label("Classe:"), classBox,
                new Label("Nemico:"), enemyBox,
                startButton,
                new Label("— oppure carica un sopravvissuto salvato —"),
                new HBox(8, savedBox, loadButton));
    }

    public Parent getRoot() {
        return root;
    }

    private Enemy createEnemy(String name) {
        return switch (name) {
            case "Cane mutante" -> EnemyFactory.mutantDog();
            case "Ghoul inferocito" -> EnemyFactory.ghoul();
            case "Capo dei predoni" -> EnemyFactory.raiderBoss();
            default -> EnemyFactory.raider();
        };
    }
}
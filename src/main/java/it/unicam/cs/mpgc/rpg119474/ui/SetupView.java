package it.unicam.cs.mpgc.rpg119474.ui;

import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;

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
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Schermata iniziale: si crea un nuovo sopravvissuto per affrontare la campagna,
 * oppure si riprende una partita lasciata a meta'.
 */
public class SetupView {

    private static final String DEFAULT_NAME = "Sopravvissuto";

    private final VBox root = new VBox(12);
    private final TextField nameField = new TextField(DEFAULT_NAME);
    private final ComboBox<SurvivorClass> classBox = new ComboBox<>();
    private final ComboBox<String> savedBox = new ComboBox<>();

    /**
     * @param campaignTitle nome della campagna, mostrato come titolo
     * @param onStart       riceve nome e classe del nuovo sopravvissuto
     * @param savedRuns     identificativi delle partite salvate
     * @param onResume      riceve l'identificativo della partita da riprendere
     */
    public SetupView(String campaignTitle, BiConsumer<String, SurvivorClass> onStart,
                     List<String> savedRuns, Consumer<String> onResume) {
        Objects.requireNonNull(onStart, "onStart");
        Objects.requireNonNull(savedRuns, "savedRuns");
        Objects.requireNonNull(onResume, "onResume");

        classBox.getItems().setAll(SurvivorClass.values());
        classBox.setValue(SurvivorClass.BRUTO);
        classBox.setConverter(new StringConverter<SurvivorClass>() {
            @Override
            public String toString(SurvivorClass survivorClass) {
                return survivorClass == null ? "" : survivorClass.displayName();
            }

            @Override
            public SurvivorClass fromString(String value) {
                return null;
            }
        });

        Button startButton = new Button("Parti per la campagna");
        startButton.setOnAction(event -> onStart.accept(chosenName(), classBox.getValue()));

        savedBox.getItems().setAll(savedRuns);
        Button resumeButton = new Button("Riprendi");
        boolean hasSaves = !savedRuns.isEmpty();
        savedBox.setDisable(!hasSaves);
        resumeButton.setDisable(!hasSaves);
        if (hasSaves) {
            savedBox.setValue(savedRuns.get(0));
        }
        resumeButton.setOnAction(event -> {
            if (savedBox.getValue() != null) {
                onResume.accept(savedBox.getValue());
            }
        });

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                new Label("Wasteland - " + campaignTitle),
                new Label("Nome del sopravvissuto:"), nameField,
                new Label("Classe:"), classBox,
                startButton,
                new Label("- oppure riprendi una partita salvata -"),
                new HBox(8, savedBox, resumeButton));
    }

    public Parent getRoot() {
        return root;
    }

    private String chosenName() {
        String typed = nameField.getText();
        return typed == null || typed.isBlank() ? DEFAULT_NAME : typed.trim();
    }
}
package it.unicam.cs.mpgc.rpg119474.ui;

import javafx.application.Application;

/**
 * Avvio dell'applicazione. E' una classe separata che NON estende Application:
 * cosi' l'avvio con i due comandi richiesti parte senza problemi di module-path.
 */
public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        Application.launch(GameApplication.class, args);
    }
}
package it.unicam.cs.mpgc.rpg119474.ui;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.engine.DefaultGameService;
import it.unicam.cs.mpgc.rpg119474.engine.GameService;
import it.unicam.cs.mpgc.rpg119474.engine.ai.EnemyStrategies;
import it.unicam.cs.mpgc.rpg119474.engine.factory.CharacterFactory;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Applicazione JavaFX: schermata iniziale -> duello. */
public class GameApplication extends Application {

    private Stage stage;
    private final RandomSource rng = RandomSource.systemDefault();

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Wasteland");
        showSetup();
        stage.show();
    }

    private void showSetup() {
        SetupView setup = new SetupView(this::startDuel);
        stage.setScene(new Scene(setup.getRoot(), 900, 600));
    }

    private void startDuel(String name, SurvivorClass survivorClass, Enemy enemy) {
        PlayerCharacter player = CharacterFactory.createSurvivor(name, survivorClass);
        GameService game = new DefaultGameService(rng);
        CombatView view = new CombatView(game, player, enemy);
        game.startDuel(player, enemy, EnemyStrategies.aggressive(), view::onEvent);
        view.refresh();
        stage.setScene(new Scene(view.getRoot(), 900, 600));
    }
}
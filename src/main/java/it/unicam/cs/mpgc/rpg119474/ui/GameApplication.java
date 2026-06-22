package it.unicam.cs.mpgc.rpg119474.ui;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.engine.DefaultGameService;
import it.unicam.cs.mpgc.rpg119474.engine.GameService;
import it.unicam.cs.mpgc.rpg119474.engine.ai.EnemyStrategies;
import it.unicam.cs.mpgc.rpg119474.engine.factory.CharacterFactory;
import it.unicam.cs.mpgc.rpg119474.engine.factory.EnemyFactory;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Applicazione JavaFX: avvia un duello e mostra la schermata di combattimento. */
public class GameApplication extends Application {

    @Override
    public void start(Stage stage) {
        RandomSource rng = RandomSource.systemDefault();
        PlayerCharacter player = CharacterFactory.createSurvivor("Sopravvissuto", SurvivorClass.BRUTO);
        Enemy enemy = EnemyFactory.raider();

        GameService game = new DefaultGameService(rng);
        CombatView view = new CombatView(game, player, enemy);
        game.startDuel(player, enemy, EnemyStrategies.aggressive(), view::onEvent);
        view.refresh();

        Scene scene = new Scene(view.getRoot(), 900, 600);
        stage.setTitle("Wasteland — Duello");
        stage.setScene(scene);
        stage.show();
    }
}
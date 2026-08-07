package it.unicam.cs.mpgc.rpg119474.ui;

import it.unicam.cs.mpgc.rpg119474.core.character.CharacterSnapshot;
import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.engine.DefaultGameService;
import it.unicam.cs.mpgc.rpg119474.engine.GameService;
import it.unicam.cs.mpgc.rpg119474.engine.ai.EnemyStrategies;
import it.unicam.cs.mpgc.rpg119474.engine.factory.CharacterFactory;
import it.unicam.cs.mpgc.rpg119474.engine.factory.EnemyFactory;
import it.unicam.cs.mpgc.rpg119474.persistence.JsonRepository;
import it.unicam.cs.mpgc.rpg119474.persistence.Repository;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;

/**
 * Applicazione JavaFX: schermata iniziale, duelli a catena, salvataggio e
 * caricamento del sopravvissuto.
 * <p>
 * Non contiene regole di gioco: chiama il {@link GameService} e mostra cio' che
 * il servizio restituisce. Dipende inoltre dall'astrazione {@link Repository},
 * non da un formato di salvataggio preciso: cambiare implementazione qui sotto
 * e' l'unica modifica necessaria per cambiare il modo in cui i dati vengono
 * conservati.
 */
public class GameApplication extends Application {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;
    private static final Path SAVE_DIRECTORY = Path.of("saves");

    private final RandomSource rng = RandomSource.systemDefault();
    private final Repository<CharacterSnapshot> repository =
            new JsonRepository<>(SAVE_DIRECTORY, CharacterSnapshot.class);
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Wasteland");
        showSetup();
        stage.show();
    }

    private void showSetup() {
        SetupView setup = new SetupView(this::startNew, repository.listIds(), this::loadAndStart);
        stage.setScene(new Scene(setup.getRoot(), WIDTH, HEIGHT));
    }

    private void startNew(String name, SurvivorClass survivorClass, Enemy enemy) {
        openCombat(CharacterFactory.createSurvivor(name, survivorClass), enemy);
    }

    private void loadAndStart(String savedId, Enemy enemy) {
        repository.load(savedId)
                .map(PlayerCharacter::fromSnapshot)
                .ifPresent(player -> openCombat(player, enemy));
    }

    private void openCombat(PlayerCharacter player, Enemy enemy) {
        GameService game = new DefaultGameService(rng);
        CombatView view = new CombatView(game, player, enemy,
                () -> repository.save(saveId(player), player.toSnapshot()),
                this::showSetup,
                () -> game.resolveVictory().map(CombatEventFormatter::describe).orElse(""),
                () -> openCombat(player, EnemyFactory.random(rng)));
        game.startDuel(player, enemy, EnemyStrategies.aggressive(), view::onEvent);
        view.refresh();
        stage.setScene(new Scene(view.getRoot(), WIDTH, HEIGHT));
    }

    private static String saveId(PlayerCharacter player) {
        return player.name().trim().replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
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
import it.unicam.cs.mpgc.rpg119474.persistence.FileRepository;
import it.unicam.cs.mpgc.rpg119474.persistence.Repository;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;

/** Applicazione JavaFX: schermata iniziale, duelli a catena, salvataggio/caricamento, progressione. */
public class GameApplication extends Application {

    private final RandomSource rng = RandomSource.systemDefault();
    private final Repository<CharacterSnapshot> repository = new FileRepository<>(Path.of("saves"));
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
        stage.setScene(new Scene(setup.getRoot(), 900, 600));
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
                () -> awardVictory(player, enemy),
                () -> openCombat(player, randomEnemy()));
        game.startDuel(player, enemy, EnemyStrategies.aggressive(), view::onEvent);
        view.refresh();
        stage.setScene(new Scene(view.getRoot(), 900, 600));
    }

    /** Assegna l'esperienza del nemico, applica eventuali salite di livello e recupera i PV. */
    private String awardVictory(PlayerCharacter player, Enemy enemy) {
        int levelBefore = player.level();
        player.gainExperience(enemy.experienceReward());
        player.heal(player.maxHealth());
        int levelAfter = player.level();
        String message = "Hai guadagnato " + enemy.experienceReward() + " punti esperienza.";
        if (levelAfter > levelBefore) {
            message += " Sei salito al livello " + levelAfter + "!";
        }
        return message;
    }

    private Enemy randomEnemy() {
        return switch (rng.nextInt(4)) {
            case 0 -> EnemyFactory.mutantDog();
            case 1 -> EnemyFactory.ghoul();
            case 2 -> EnemyFactory.raiderBoss();
            default -> EnemyFactory.raider();
        };
    }

    private static String saveId(PlayerCharacter player) {
        return player.name().trim().replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
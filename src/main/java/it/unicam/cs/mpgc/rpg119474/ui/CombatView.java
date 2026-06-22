package it.unicam.cs.mpgc.rpg119474.ui;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;
import it.unicam.cs.mpgc.rpg119474.engine.GameService;
import it.unicam.cs.mpgc.rpg119474.engine.combat.CombatEvent;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;

/** Schermata di combattimento: mostra i combattenti, il log degli eventi e i comandi del giocatore. */
public class CombatView {

    private final GameService game;
    private final GameCharacter player;
    private final GameCharacter enemy;

    private final BorderPane root = new BorderPane();
    private final TextArea log = new TextArea();
    private final Label turnLabel = new Label();
    private final Label apLabel = new Label();
    private final Label resultLabel = new Label();

    private final ProgressBar playerHp = new ProgressBar(1);
    private final Label playerHpLabel = new Label();
    private final ProgressBar enemyHp = new ProgressBar(1);
    private final Label enemyHpLabel = new Label();

    private final Map<Button, Ability> abilityButtons = new LinkedHashMap<>();
    private final Button endTurnButton = new Button("Termina turno");

    public CombatView(GameService game, GameCharacter player, GameCharacter enemy) {
        this.game = game;
        this.player = player;
        this.enemy = enemy;
        buildLayout();
        wireActions();
    }

    public Parent getRoot() {
        return root;
    }

    private void buildLayout() {
        log.setEditable(false);
        log.setWrapText(true);

        VBox playerPanel = new VBox(6,
                new Label(player.name() + " (giocatore)"), playerHpLabel, playerHp, apLabel);
        playerPanel.setPadding(new Insets(10));

        VBox enemyPanel = new VBox(6,
                new Label(enemy.name() + " (nemico)"), enemyHpLabel, enemyHp);
        enemyPanel.setPadding(new Insets(10));
        enemyPanel.setAlignment(Pos.TOP_RIGHT);

        HBox top = new HBox(40, playerPanel, enemyPanel);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(10));

        HBox controls = new HBox(8);
        controls.setPadding(new Insets(10));
        controls.setAlignment(Pos.CENTER_LEFT);
        for (Ability ability : player.abilities()) {
            Button button = new Button(ability.name() + " (PA " + ability.actionPointCost() + ")");
            abilityButtons.put(button, ability);
            controls.getChildren().add(button);
        }
        controls.getChildren().add(endTurnButton);

        VBox bottom = new VBox(6, turnLabel, controls, resultLabel);
        bottom.setPadding(new Insets(10));

        root.setTop(top);
        root.setCenter(log);
        root.setBottom(bottom);
    }

    private void wireActions() {
        for (Map.Entry<Button, Ability> entry : abilityButtons.entrySet()) {
            Ability ability = entry.getValue();
            entry.getKey().setOnAction(event -> {
                game.playerUseAbility(ability, enemy);
                refresh();
            });
        }
        endTurnButton.setOnAction(event -> {
            game.playerEndTurn();
            refresh();
        });
    }

    /** Osservatore degli eventi: aggiunge una riga al log. */
    public void onEvent(CombatEvent event) {
        log.appendText(describe(event) + "\n");
    }

    /** Aggiorna barre dei PV, Punti Azione, etichette e stato dei pulsanti. */
    public void refresh() {
        playerHp.setProgress(healthFraction(player));
        playerHpLabel.setText("PV " + player.currentHealth() + "/" + player.maxHealth());
        enemyHp.setProgress(healthFraction(enemy));
        enemyHpLabel.setText("PV " + enemy.currentHealth() + "/" + enemy.maxHealth());
        apLabel.setText("Punti Azione: " + game.currentActionPoints());

        boolean playerTurn = !game.isOver() && game.currentActor() == player;
        turnLabel.setText(game.isOver() ? "Combattimento terminato"
                : "Turno di " + game.currentActor().name());
        for (Map.Entry<Button, Ability> entry : abilityButtons.entrySet()) {
            boolean affordable = entry.getValue().actionPointCost() <= game.currentActionPoints();
            entry.getKey().setDisable(!playerTurn || !affordable);
        }
        endTurnButton.setDisable(!playerTurn);

        if (game.isOver()) {
            String winner = game.winner().map(GameCharacter::name).orElse("nessuno");
            resultLabel.setText("Vince " + winner + "!");
        }
    }

    private static double healthFraction(GameCharacter character) {
        return character.maxHealth() == 0 ? 0
                : (double) character.currentHealth() / character.maxHealth();
    }

    private static String describe(CombatEvent event) {
        return switch (event) {
            case CombatEvent.CombatStarted s ->
                    "Inizia lo scontro: " + s.player().name() + " contro " + s.enemy().name();
            case CombatEvent.TurnStarted t ->
                    "-- Turno di " + t.actor().name() + " (PA " + t.actionPoints() + ")";
            case CombatEvent.AbilityUsed a -> "   " + a.result().message();
            case CombatEvent.CharacterDefeated d ->
                    "   " + d.character().name() + " e' stato sconfitto!";
            case CombatEvent.CombatEnded c -> "Vince " + c.winner().name() + "!";
        };
    }
}
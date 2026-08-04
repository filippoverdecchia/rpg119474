package it.unicam.cs.mpgc.rpg119474.ui;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.engine.GameService;
import it.unicam.cs.mpgc.rpg119474.engine.combat.CombatEvent;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Schermata di combattimento: combattenti, log, comandi, zaino, salvataggio e progressione. */
public class CombatView {

    /** Invocato una volta quando il giocatore vince; restituisce un messaggio da mostrare nel log. */
    @FunctionalInterface
    public interface VictoryHandler {
        String onPlayerVictory();
    }

    private final GameService game;
    private final PlayerCharacter player;
    private final GameCharacter enemy;
    private final Runnable onSave;
    private final Runnable onBackToMenu;
    private final VictoryHandler onVictory;
    private final Runnable onNextEnemy;

    private boolean victoryHandled = false;

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
    private final Button saveButton = new Button("Salva sopravvissuto");
    private final Button menuButton = new Button("Torna al menu");
    private final Button nextEnemyButton = new Button("Affronta un nuovo nemico");
    private final ComboBox<Item> inventoryBox = new ComboBox<>();
    private final Button equipButton = new Button("Equipaggia");

    public CombatView(GameService game, PlayerCharacter player, GameCharacter enemy,
                      Runnable onSave, Runnable onBackToMenu, VictoryHandler onVictory, Runnable onNextEnemy) {
        this.game = game;
        this.player = player;
        this.enemy = enemy;
        this.onSave = onSave;
        this.onBackToMenu = onBackToMenu;
        this.onVictory = onVictory;
        this.onNextEnemy = onNextEnemy;
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

        nextEnemyButton.setDisable(true);
        equipButton.setDisable(true);
        inventoryBox.setDisable(true);
        inventoryBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Item item) {
                return item == null ? "" : item.name();
            }

            @Override
            public Item fromString(String value) {
                return null;
            }
        });

        HBox actions = new HBox(8, saveButton, menuButton, nextEnemyButton,
                new Label("Zaino:"), inventoryBox, equipButton);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(0, 10, 0, 10));

        VBox bottom = new VBox(6, turnLabel, controls, actions, resultLabel);
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
        saveButton.setOnAction(event -> {
            onSave.run();
            log.appendText("Sopravvissuto salvato.\n");
        });
        menuButton.setOnAction(event -> onBackToMenu.run());
        nextEnemyButton.setOnAction(event -> onNextEnemy.run());
        equipButton.setOnAction(event -> {
            Item chosen = inventoryBox.getValue();
            if (chosen != null && player.equipFromInventory(chosen)) {
                log.appendText("Hai equipaggiato: " + chosen.name() + ".\n");
                refresh();
            }
        });
    }

    /** Osservatore degli eventi: aggiunge una riga al log e gestisce la vittoria del giocatore. */
    public void onEvent(CombatEvent event) {
        log.appendText(CombatEventFormatter.describe(event) + "\n");
        if (event instanceof CombatEvent.CombatEnded ended
                && ended.winner() == player && !victoryHandled) {
            victoryHandled = true;
            String message = onVictory.onPlayerVictory();
            if (message != null && !message.isBlank()) {
                log.appendText(message + "\n");
            }
            refresh();
        }
    }

    /** Aggiorna barre dei PV, Punti Azione, zaino, etichette e stato dei pulsanti. */
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

        boolean playerWon = game.isOver() && game.winner().map(winner -> winner == player).orElse(false);
        nextEnemyButton.setDisable(!playerWon);
        refreshInventory(playerWon);

        if (game.isOver()) {
            String winner = game.winner().map(GameCharacter::name).orElse("nessuno");
            resultLabel.setText("Vince " + winner + "!");
        }
    }

    /** Mostra nello zaino i soli oggetti equipaggiabili, utilizzabili tra un duello e l'altro. */
    private void refreshInventory(boolean playerWon) {
        List<Item> equippable = player.inventory().stream().filter(Item::equippable).toList();
        Item previous = inventoryBox.getValue();
        inventoryBox.getItems().setAll(equippable);
        boolean canEquip = playerWon && !equippable.isEmpty();
        inventoryBox.setDisable(!canEquip);
        equipButton.setDisable(!canEquip);
        if (canEquip) {
            inventoryBox.setValue(equippable.contains(previous) ? previous : equippable.get(0));
        } else {
            inventoryBox.setValue(null);
        }
    }

    private static double healthFraction(GameCharacter character) {
        return character.maxHealth() == 0 ? 0
                : (double) character.currentHealth() / character.maxHealth();
    }
}
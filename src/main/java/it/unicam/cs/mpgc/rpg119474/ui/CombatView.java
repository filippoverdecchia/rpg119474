package it.unicam.cs.mpgc.rpg119474.ui;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.item.Armor;
import it.unicam.cs.mpgc.rpg119474.core.item.Consumable;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.core.item.Weapon;
import it.unicam.cs.mpgc.rpg119474.engine.GameService;
import it.unicam.cs.mpgc.rpg119474.engine.combat.CombatEngine;
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

/**
 * Schermata di combattimento: combattenti, log degli eventi, azioni di turno
 * (abilita' e consumabili), zaino, salvataggio e proseguimento.
 */
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
    private final Label equipmentLabel = new Label();
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

    private final ComboBox<Consumable> consumableBox = new ComboBox<>();
    private final Button useItemButton =
            new Button("Usa (PA " + CombatEngine.CONSUMABLE_ACTION_POINT_COST + ")");
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

        VBox playerPanel = new VBox(6, new Label(player.name() + " (giocatore)"),
                playerHpLabel, playerHp, apLabel, equipmentLabel);
        playerPanel.setPadding(new Insets(10));

        VBox enemyPanel = new VBox(6,
                new Label(enemy.name() + " (nemico)"), enemyHpLabel, enemyHp);
        enemyPanel.setPadding(new Insets(10));
        enemyPanel.setAlignment(Pos.TOP_RIGHT);

        HBox top = new HBox(40, playerPanel, enemyPanel);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(10));

        // Azioni disponibili durante il proprio turno.
        HBox turnActions = new HBox(8);
        turnActions.setAlignment(Pos.CENTER_LEFT);
        turnActions.setPadding(new Insets(10, 10, 0, 10));
        for (Ability ability : player.abilities()) {
            Button button = new Button(ability.name() + " (PA " + ability.actionPointCost() + ")");
            abilityButtons.put(button, ability);
            turnActions.getChildren().add(button);
        }
        configureConverter(consumableBox);
        turnActions.getChildren().addAll(consumableBox, useItemButton, endTurnButton);

        // Azioni fuori dal turno: gestione del personaggio e proseguimento.
        configureConverter(inventoryBox);
        HBox metaActions = new HBox(8, saveButton, menuButton, nextEnemyButton,
                new Label("Zaino:"), inventoryBox, equipButton);
        metaActions.setAlignment(Pos.CENTER_LEFT);
        metaActions.setPadding(new Insets(0, 10, 0, 10));

        VBox bottom = new VBox(6, turnLabel, turnActions, metaActions, resultLabel);
        bottom.setPadding(new Insets(10));

        root.setTop(top);
        root.setCenter(log);
        root.setBottom(bottom);
    }

    /** Nelle tendine si mostra il nome dell'oggetto, non la sua rappresentazione tecnica. */
    private static <T extends Item> void configureConverter(ComboBox<T> box) {
        box.setConverter(new StringConverter<>() {
            @Override
            public String toString(T item) {
                return item == null ? "" : item.name();
            }

            @Override
            public T fromString(String value) {
                return null;
            }
        });
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
        useItemButton.setOnAction(event -> {
            Consumable chosen = consumableBox.getValue();
            if (chosen != null) {
                game.playerUseConsumable(chosen);
                refresh();
            }
        });
        equipButton.setOnAction(event -> {
            Item chosen = inventoryBox.getValue();
            if (chosen != null && player.equipFromInventory(chosen)) {
                log.appendText("Hai equipaggiato: " + chosen.name() + ".\n");
                refresh();
            }
        });
        saveButton.setOnAction(event -> {
            onSave.run();
            log.appendText("Sopravvissuto salvato.\n");
        });
        menuButton.setOnAction(event -> onBackToMenu.run());
        nextEnemyButton.setOnAction(event -> onNextEnemy.run());
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

    /** Aggiorna barre dei PV, Punti Azione, equipaggiamento, zaino e stato dei comandi. */
    public void refresh() {
        playerHp.setProgress(healthFraction(player));
        playerHpLabel.setText("PV " + player.currentHealth() + "/" + player.maxHealth());
        enemyHp.setProgress(healthFraction(enemy));
        enemyHpLabel.setText("PV " + enemy.currentHealth() + "/" + enemy.maxHealth());
        apLabel.setText("Punti Azione: " + game.currentActionPoints());
        equipmentLabel.setText("Arma: " + player.weapon().map(Weapon::name).orElse("nessuna")
                + " | Armatura: " + player.armor().map(Armor::name).orElse("nessuna"));

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

        refreshConsumables(playerTurn);
        refreshEquippable(playerWon);

        if (game.isOver()) {
            String winner = game.winner().map(GameCharacter::name).orElse("nessuno");
            resultLabel.setText("Vince " + winner + "!");
        }
    }

    /** I consumabili si usano durante il proprio turno, se restano abbastanza Punti Azione. */
    private void refreshConsumables(boolean playerTurn) {
        List<Consumable> consumables = player.inventory().stream()
                .filter(Consumable.class::isInstance)
                .map(Consumable.class::cast)
                .toList();
        Consumable previous = consumableBox.getValue();
        consumableBox.getItems().setAll(consumables);
        boolean canUse = playerTurn && !consumables.isEmpty()
                && game.currentActionPoints() >= CombatEngine.CONSUMABLE_ACTION_POINT_COST;
        consumableBox.setDisable(!canUse);
        useItemButton.setDisable(!canUse);
        consumableBox.setValue(consumables.isEmpty() ? null
                : (consumables.contains(previous) ? previous : consumables.get(0)));
    }

    /** L'equipaggiamento si cambia tra un duello e l'altro, a scontro concluso. */
    private void refreshEquippable(boolean playerWon) {
        List<Item> equippable = player.inventory().stream().filter(Item::equippable).toList();
        Item previous = inventoryBox.getValue();
        inventoryBox.getItems().setAll(equippable);
        boolean canEquip = playerWon && !equippable.isEmpty();
        inventoryBox.setDisable(!canEquip);
        equipButton.setDisable(!canEquip);
        inventoryBox.setValue(equippable.isEmpty() ? null
                : (equippable.contains(previous) ? previous : equippable.get(0)));
    }

    private static double healthFraction(GameCharacter character) {
        return character.maxHealth() == 0 ? 0
                : (double) character.currentHealth() / character.maxHealth();
    }
}
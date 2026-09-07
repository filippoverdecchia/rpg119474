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
import java.util.Objects;

/**
 * Schermata di una tappa della campagna: i due contendenti, il diario dello
 * scontro e i comandi del giocatore.
 * <p>
 * Non contiene regole di gioco: interroga il {@link GameService} per sapere cosa
 * mostrare e gli comunica le scelte del giocatore. Cosa accada al termine della
 * tappa non la riguarda: lo delega alle {@link CombatActions} ricevute.
 */
public class CombatView {

    private final GameService game;
    private final PlayerCharacter player;
    private final GameCharacter enemy;
    private final CombatActions actions;

    private boolean stageEnded = false;

    private final BorderPane root = new BorderPane();
    private final TextArea log = new TextArea();
    private final Label stageLabel = new Label();
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
    private final Button saveButton = new Button("Salva partita");
    private final Button menuButton = new Button("Torna al menu");
    private final Button continueButton = new Button("Continua");

    private final ComboBox<ItemStack<Consumable>> suppliesBox = new ComboBox<>();
    private final Button useItemButton =
            new Button("Usa (PA " + CombatEngine.CONSUMABLE_ACTION_POINT_COST + ")");
    private final ComboBox<ItemStack<Item>> backpackBox = new ComboBox<>();
    private final Button equipButton = new Button("Equipaggia");

    public CombatView(GameService game, PlayerCharacter player, GameCharacter enemy,
                      String stageHeader, CombatActions actions) {
        this.game = Objects.requireNonNull(game, "game");
        this.player = Objects.requireNonNull(player, "player");
        this.enemy = Objects.requireNonNull(enemy, "enemy");
        this.actions = Objects.requireNonNull(actions, "actions");
        this.stageLabel.setText(Objects.requireNonNull(stageHeader, "stageHeader"));
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

        HBox fighters = new HBox(40, playerPanel, enemyPanel);
        fighters.setAlignment(Pos.CENTER);

        VBox top = new VBox(4, stageLabel, fighters);
        top.setPadding(new Insets(10));
        top.setAlignment(Pos.CENTER);

        HBox turnActions = new HBox(8);
        turnActions.setAlignment(Pos.CENTER_LEFT);
        turnActions.setPadding(new Insets(10, 10, 0, 10));
        for (Ability ability : player.abilities()) {
            Button button = new Button(ability.name() + " (PA " + ability.actionPointCost() + ")");
            abilityButtons.put(button, ability);
            turnActions.getChildren().add(button);
        }
        configureLabels(suppliesBox);
        turnActions.getChildren().addAll(suppliesBox, useItemButton, endTurnButton);

        configureLabels(backpackBox);
        HBox metaActions = new HBox(8, new Label("Zaino:"), backpackBox, equipButton,
                saveButton, menuButton, continueButton);
        metaActions.setAlignment(Pos.CENTER_LEFT);
        metaActions.setPadding(new Insets(0, 10, 0, 10));

        VBox bottom = new VBox(6, turnLabel, turnActions, metaActions, resultLabel);
        bottom.setPadding(new Insets(10));

        root.setTop(top);
        root.setCenter(log);
        root.setBottom(bottom);
    }

    /** Nelle tendine si mostra il nome dell'oggetto con il numero di esemplari posseduti. */
    private static <T extends Item> void configureLabels(ComboBox<ItemStack<T>> box) {
        box.setConverter(new StringConverter<ItemStack<T>>() {
            @Override
            public String toString(ItemStack<T> stack) {
                return stack == null ? "" : stack.label();
            }

            @Override
            public ItemStack<T> fromString(String value) {
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
            ItemStack<Consumable> chosen = suppliesBox.getValue();
            if (chosen != null) {
                game.playerUseConsumable(chosen.item());
                refresh();
            }
        });
        equipButton.setOnAction(event -> {
            ItemStack<Item> chosen = backpackBox.getValue();
            if (chosen != null && player.equipFromInventory(chosen.item())) {
                log.appendText("Hai equipaggiato: " + chosen.item().name() + ".\n");
                refresh();
            }
        });
        saveButton.setOnAction(event -> {
            actions.onSave().run();
            log.appendText("Partita salvata.\n");
        });
        menuButton.setOnAction(event -> actions.onBackToMenu().run());
        continueButton.setOnAction(event -> actions.onContinue().run());
    }

    /**
     * Osservatore degli eventi: li annota nel diario e, quando lo scontro si
     * chiude, fa concludere la tappa a chi ospita la schermata.
     */
    public void onEvent(CombatEvent event) {
        log.appendText(CombatEventFormatter.describe(event) + "\n");
        if (event instanceof CombatEvent.CombatEnded && !stageEnded) {
            stageEnded = true;
            String report = actions.onStageEnded().get();
            if (report != null && !report.isBlank()) {
                log.appendText(report + "\n");
            }
            refresh();
        }
    }

    /** Aggiorna barre, Punti Azione, equipaggiamento, scorte e stato dei comandi. */
    public void refresh() {
        playerHp.setProgress(healthFraction(player));
        playerHpLabel.setText("PV " + player.currentHealth() + "/" + player.maxHealth());
        enemyHp.setProgress(healthFraction(enemy));
        enemyHpLabel.setText("PV " + enemy.currentHealth() + "/" + enemy.maxHealth());
        apLabel.setText("Punti Azione: " + game.currentActionPoints());
        equipmentLabel.setText("Arma: " + player.weapon().map(Weapon::name).orElse("nessuna")
                + " | Armatura: " + player.armor().map(Armor::name).orElse("nessuna"));

        boolean playerTurn = !game.isOver() && game.currentActor() == player;
        turnLabel.setText(game.isOver() ? "Scontro terminato"
                : "Turno di " + game.currentActor().name());
        for (Map.Entry<Button, Ability> entry : abilityButtons.entrySet()) {
            boolean affordable = entry.getValue().actionPointCost() <= game.currentActionPoints();
            entry.getKey().setDisable(!playerTurn || !affordable);
        }
        endTurnButton.setDisable(!playerTurn);
        continueButton.setDisable(!game.isOver());

        refreshSupplies(playerTurn);
        refreshBackpack(game.isOver());

        if (game.isOver()) {
            String winner = game.winner().map(GameCharacter::name).orElse("nessuno");
            resultLabel.setText("Vince " + winner + "!");
        }
    }

        /** Le scorte si usano nel proprio turno, se restano abbastanza Punti Azione. */
    private void refreshSupplies(boolean playerTurn) {
        List<ItemStack<Consumable>> supplies = ItemStack.group(player.inventory().stream()
                .filter(Consumable.class::isInstance)
                .map(Consumable.class::cast));
        boolean usable = playerTurn && !supplies.isEmpty()
                && game.currentActionPoints() >= CombatEngine.CONSUMABLE_ACTION_POINT_COST;
        updateBox(suppliesBox, supplies, usable);
        useItemButton.setDisable(!usable);

        // Con un solo tipo di scorta la tendina non offre alcuna scelta: si mostra
        // solo quando c'e' davvero qualcosa da scegliere, e il pulsante dice cosa
        // verra' usato.
        boolean choiceNeeded = supplies.size() > 1;
        suppliesBox.setVisible(choiceNeeded);
        suppliesBox.setManaged(choiceNeeded);
        ItemStack<Consumable> selected = suppliesBox.getValue();
        useItemButton.setText(selected == null
                ? "Usa (PA " + CombatEngine.CONSUMABLE_ACTION_POINT_COST + ")"
                : "Usa " + selected.label() + " (PA " + CombatEngine.CONSUMABLE_ACTION_POINT_COST + ")");
    }

    /** L'equipaggiamento si cambia a scontro concluso, prima di ripartire. */
    private void refreshBackpack(boolean combatOver) {
        List<ItemStack<Item>> equippable =
                ItemStack.group(player.inventory().stream().filter(Item::equippable));
        boolean canEquip = combatOver && !equippable.isEmpty();
        updateBox(backpackBox, equippable, canEquip);
        equipButton.setDisable(!canEquip);
    }

    /** Ricarica una tendina conservando, dove possibile, la voce gia' scelta. */
    private static <T extends Item> void updateBox(ComboBox<ItemStack<T>> box,
                                                   List<ItemStack<T>> content, boolean enabled) {
        ItemStack<T> previous = box.getValue();
        box.getItems().setAll(content);
        box.setDisable(!enabled);
        if (content.isEmpty()) {
            box.setValue(null);
            return;
        }
        box.setValue(content.stream()
                .filter(stack -> previous != null && stack.item().name().equals(previous.item().name()))
                .findFirst()
                .orElse(content.get(0)));
    }

    private static double healthFraction(GameCharacter character) {
        return character.maxHealth() == 0 ? 0
                : (double) character.currentHealth() / character.maxHealth();
    }
}
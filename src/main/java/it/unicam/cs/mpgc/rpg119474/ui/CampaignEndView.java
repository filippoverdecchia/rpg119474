package it.unicam.cs.mpgc.rpg119474.ui;

import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.item.Armor;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.core.item.Weapon;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

/**
 * Schermata conclusiva: la campagna e' stata portata a termine. Riepiloga il
 * sopravvissuto e cio' che ha riportato indietro dalla Zona.
 */
public class CampaignEndView {

    private final VBox root = new VBox(12);

    public CampaignEndView(String campaignTitle, PlayerCharacter survivor, Runnable onBackToMenu) {
        Objects.requireNonNull(campaignTitle, "campaignTitle");
        Objects.requireNonNull(survivor, "survivor");
        Objects.requireNonNull(onBackToMenu, "onBackToMenu");

        Button menuButton = new Button("Torna al menu");
        menuButton.setOnAction(event -> onBackToMenu.run());

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                new Label("Campagna completata: " + campaignTitle),
                new Label(survivor.name() + " ha attraversato tutta la Zona."),
                new Label("Classe: " + survivor.survivorClass().displayName()
                        + " | Livello " + survivor.level()
                        + " | Esperienza " + survivor.experience()),
                new Label("Punti vita: " + survivor.currentHealth() + "/" + survivor.maxHealth()),
                new Label("Equipaggiamento: "
                        + survivor.weapon().map(Weapon::name).orElse("nessuna arma")
                        + " e " + survivor.armor().map(Armor::name).orElse("nessuna armatura")),
                new Label("Riportato indietro: " + describeBackpack(survivor)),
                menuButton);
    }

    public Parent getRoot() {
        return root;
    }

    /** Contenuto dello zaino a fine campagna, raggruppato per non ripetere le voci. */
    private static String describeBackpack(PlayerCharacter survivor) {
        List<ItemStack<Item>> backpack = ItemStack.group(survivor.inventory().stream());
        return backpack.isEmpty() ? "niente, la Zona si e' presa tutto"
                : String.join(", ", backpack.stream().map(ItemStack::label).toList());
    }
}
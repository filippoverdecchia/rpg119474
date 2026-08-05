package it.unicam.cs.mpgc.rpg119474.core.character;

import it.unicam.cs.mpgc.rpg119474.core.item.Armor;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.core.item.Weapon;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Stato salvabile di un personaggio giocante: solo dati (niente comportamento),
 * cosi' e' serializzabile senza problemi. Le abilita' non si salvano: vengono
 * ricostruite dalla classe al caricamento. {@code weapon} e {@code armor}
 * possono essere {@code null} se non equipaggiati.
 */
public record CharacterSnapshot(String name, SurvivorClass survivorClass, int experience,
                                int currentHealth, Weapon weapon, Armor armor,
                                List<Item> inventory)
        implements Serializable {

    public CharacterSnapshot {
        Objects.requireNonNull(inventory, "inventory");
        inventory = List.copyOf(inventory);
    }
}
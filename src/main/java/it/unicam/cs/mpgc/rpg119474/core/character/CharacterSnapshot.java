package it.unicam.cs.mpgc.rpg119474.core.character;

import it.unicam.cs.mpgc.rpg119474.core.item.Armor;
import it.unicam.cs.mpgc.rpg119474.core.item.Weapon;

import java.io.Serializable;

/**
 * Stato salvabile di un personaggio giocante: solo dati (niente comportamento),
 * cosi' e' serializzabile senza problemi. Le abilita' non si salvano: vengono
 * ricostruite dalla classe al caricamento. {@code weapon} e {@code armor}
 * possono essere {@code null} se non equipaggiati.
 */
public record CharacterSnapshot(String name, SurvivorClass survivorClass, int experience,
                                int currentHealth, Weapon weapon, Armor armor)
        implements Serializable {
}
package it.unicam.cs.mpgc.rpg119474.core.item;

import it.unicam.cs.mpgc.rpg119474.core.attribute.AttributeModifier;

import java.io.Serializable;
import java.util.Objects;

/**
 * Armatura equipaggiabile: fornisce un bonus agli attributi (tipicamente
 * Resistenza). Serializzabile per la persistenza.
 */
public record Armor(String name, Rarity rarity, String description,
                    AttributeModifier attributeBonus)
        implements Item, Serializable {

    public Armor {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(rarity, "rarity");
        Objects.requireNonNull(attributeBonus, "attributeBonus");
        if (description == null) {
            description = "";
        }
    }
}
package it.unicam.cs.mpgc.rpg119474.core.item;

import it.unicam.cs.mpgc.rpg119474.core.attribute.AttributeModifier;

import java.util.Objects;

/**
 * Armatura equipaggiabile: fornisce un bonus agli attributi (tipicamente
 * Resistenza, che alza punti vita e difesa, ma puo' incidere anche su altri).
 */
public record Armor(String name, Rarity rarity, String description,
                    AttributeModifier attributeBonus) implements Item {

    public Armor {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(rarity, "rarity");
        Objects.requireNonNull(attributeBonus, "attributeBonus");
        if (description == null) {
            description = "";
        }
    }
}

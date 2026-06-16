package it.unicam.cs.mpgc.rpg119474.core.item;

import it.unicam.cs.mpgc.rpg119474.core.attribute.AttributeModifier;
import it.unicam.cs.mpgc.rpg119474.core.stats.DamageType;

import java.util.Objects;

/**
 * Arma equipaggiabile: aggiunge danno base agli attacchi, ha un tipo di danno e
 * puo' fornire un bonus agli attributi (es. un mirino aumenta la Percezione).
 */
public record Weapon(String name, Rarity rarity, String description,
                     DamageType damageType, int baseDamage, AttributeModifier attributeBonus) implements Item {

    public Weapon {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(rarity, "rarity");
        Objects.requireNonNull(damageType, "damageType");
        Objects.requireNonNull(attributeBonus, "attributeBonus");
        if (description == null) {
            description = "";
        }
        if (baseDamage < 0) {
            throw new IllegalArgumentException("baseDamage deve essere >= 0");
        }
    }
}

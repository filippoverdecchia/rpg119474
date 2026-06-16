package it.unicam.cs.mpgc.rpg119474.core.item;

import java.util.Objects;

/** Oggetto consumabile, ad esempio un medikit che ripristina punti vita. */
public record Consumable(String name, Rarity rarity, String description,
                         int healthRestored) implements Item {

    public Consumable {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(rarity, "rarity");
        if (description == null) {
            description = "";
        }
        if (healthRestored < 0) {
            throw new IllegalArgumentException("healthRestored deve essere >= 0");
        }
    }
}

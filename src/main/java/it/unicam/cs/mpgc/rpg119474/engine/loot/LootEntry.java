package it.unicam.cs.mpgc.rpg119474.engine.loot;

import it.unicam.cs.mpgc.rpg119474.core.item.Item;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Una riga della tabella del bottino: quale oggetto puo' cadere, con quale
 * probabilita' e da quali nemici.
 *
 * @param item          costruttore dell'oggetto (un nuovo esemplare a ogni caduta)
 * @param percent       probabilita' di caduta, da 0 a 100
 * @param minimumReward esperienza minima del nemico perche' l'oggetto possa cadere:
 *                      il bottino migliore arriva solo dagli avversari piu' duri
 */
public record LootEntry(Supplier<Item> item, int percent, int minimumReward) {

    public LootEntry {
        Objects.requireNonNull(item, "item");
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent deve essere tra 0 e 100");
        }
        if (minimumReward < 0) {
            throw new IllegalArgumentException("minimumReward deve essere >= 0");
        }
    }
}
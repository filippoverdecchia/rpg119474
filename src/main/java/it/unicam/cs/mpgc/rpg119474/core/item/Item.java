package it.unicam.cs.mpgc.rpg119474.core.item;

/**
 * Oggetto del gioco.
 * <p>
 * Gerarchia <em>sealed</em>: i soli tipi ammessi sono {@link Weapon},
 * {@link Armor} e {@link Consumable}. Cosi' il pattern matching sugli oggetti
 * resta esaustivo e l'aggiunta di un nuovo tipo e' una scelta esplicita e
 * controllata (Open/Closed Principle).
 */
public sealed interface Item permits Weapon, Armor, Consumable {

    String name();

    Rarity rarity();

    String description();
}

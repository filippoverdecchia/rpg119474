package it.unicam.cs.mpgc.rpg119474.engine.loot;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;

import java.util.List;

/**
 * Decide quali oggetti lascia un nemico sconfitto.
 * <p>
 * E' un'astrazione separata dalla progressione perche' "quanta esperienza do"
 * e "cosa lascio cadere" sono due regole indipendenti: si possono cambiare una
 * senza toccare l'altra.
 */
@FunctionalInterface
public interface LootService {

    /** Bottino lasciato dal nemico sconfitto; lista vuota se non cade nulla. */
    List<Item> rollLoot(Enemy defeated);
}
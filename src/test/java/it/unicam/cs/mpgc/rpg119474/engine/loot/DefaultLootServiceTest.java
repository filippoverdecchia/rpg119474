package it.unicam.cs.mpgc.rpg119474.engine.loot;

import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.engine.factory.EnemyFactory;
import it.unicam.cs.mpgc.rpg119474.engine.factory.ItemFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultLootServiceTest {

    /** Sorgente che restituisce sempre 0: ogni tiro percentuale riesce. */
    private static final RandomSource ALWAYS_HITS = bound -> 0;

    /** Sorgente che restituisce sempre il massimo: ogni tiro percentuale fallisce. */
    private static final RandomSource ALWAYS_MISSES = bound -> bound - 1;

    @Test
    void dropsNothingWhenEveryRollFails() {
        LootService loot = new DefaultLootService(ALWAYS_MISSES);
        assertTrue(loot.rollLoot(EnemyFactory.raiderBoss()).isEmpty());
    }

    @Test
    void weakEnemiesCannotDropTheBestItems() {
        LootService loot = new DefaultLootService(ALWAYS_HITS);
        List<Item> dropped = loot.rollLoot(EnemyFactory.mutantDog()); // 50 XP

        assertTrue(dropped.stream().anyMatch(item -> item.name().equals("Medikit")));
        assertTrue(dropped.stream().noneMatch(item -> item.name().equals("Armatura da combattimento")),
                "l'armatura da combattimento richiede un nemico da almeno 90 XP");
    }

    @Test
    void toughEnemiesCanDropEverything() {
        LootService loot = new DefaultLootService(ALWAYS_HITS);
        assertEquals(4, loot.rollLoot(EnemyFactory.raiderBoss()).size()); // 150 XP: tutta la tabella
    }

    @Test
    void usesTheProvidedTable() {
        LootService loot = new DefaultLootService(ALWAYS_HITS,
                List.of(new LootEntry(ItemFactory::machete, 100, 0)));
        assertEquals(List.of("Machete"), loot.rollLoot(EnemyFactory.raider())
                .stream().map(Item::name).toList());
    }
}
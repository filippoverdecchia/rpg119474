package it.unicam.cs.mpgc.rpg119474.engine.loot;

import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.engine.campaign.Campaign;
import it.unicam.cs.mpgc.rpg119474.engine.campaign.Campaigns;
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
        assertTrue(loot.rollLoot(EnemyFactory.wasteWarlord()).isEmpty());
    }

    @Test
    void weakEnemiesCannotDropTheBestItems() {
        LootService loot = new DefaultLootService(ALWAYS_HITS);
        List<Item> dropped = loot.rollLoot(EnemyFactory.mutantDog()); // 50 XP

        assertTrue(dropped.stream().anyMatch(item -> item.name().equals("Medikit")));
        assertTrue(dropped.stream().noneMatch(item -> item.name().equals("Esoscheletro")),
                "l'esoscheletro si trova solo sugli avversari piu' temibili");
    }

    /**
     * Verifica la regola, non un numero preciso di oggetti: la tabella del bottino
     * puo' crescere con i contenuti del gioco, ma un nemico piu' duro deve sempre
     * poter lasciare almeno quanto uno piu' debole.
     */
    @Test
    void theTougherTheEnemyTheRicherTheLoot() {
        LootService loot = new DefaultLootService(ALWAYS_HITS);

        int weak = loot.rollLoot(EnemyFactory.mutantDog()).size();         // 50 XP
        int average = loot.rollLoot(EnemyFactory.ghoul()).size();          // 90 XP
        int tough = loot.rollLoot(EnemyFactory.raiderBoss()).size();       // 150 XP
        int finalBoss = loot.rollLoot(EnemyFactory.wasteWarlord()).size(); // 250 XP

        assertTrue(weak < average, "il ghoul deve lasciare piu' del cane mutante");
        assertTrue(average < tough, "il capo dei predoni deve lasciare piu' del ghoul");
        assertTrue(tough <= finalBoss, "l'avversario finale non puo' lasciare di meno");
    }

    /**
     * Nessun oggetto deve essere ottenibile soltanto dall'ultimo nemico della
     * campagna: con quella vittoria il gioco si chiude, quindi un bottino
     * esclusivo dello scontro finale non potrebbe mai essere usato.
     */
    @Test
    void nothingIsExclusiveToTheFinalEnemy() {
        LootService loot = new DefaultLootService(ALWAYS_HITS);
        Campaign campaign = Campaigns.wasteland();

        int beforeTheEnd = loot.rollLoot(campaign.stage(campaign.length() - 1).spawnEnemy()).size();
        int atTheEnd = loot.rollLoot(campaign.stage(campaign.length()).spawnEnemy()).size();

        assertEquals(atTheEnd, beforeTheEnd,
                "tutto il bottino dev'essere raggiungibile prima dello scontro finale");
    }

    @Test
    void usesTheProvidedTable() {
        LootService loot = new DefaultLootService(ALWAYS_HITS,
                List.of(new LootEntry(ItemFactory::machete, 100, 0)));
        assertEquals(List.of("Machete"), loot.rollLoot(EnemyFactory.raider())
                .stream().map(Item::name).toList());
    }
}
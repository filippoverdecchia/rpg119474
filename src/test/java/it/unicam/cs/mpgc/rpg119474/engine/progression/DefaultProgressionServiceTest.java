package it.unicam.cs.mpgc.rpg119474.engine.progression;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.engine.factory.EnemyFactory;
import it.unicam.cs.mpgc.rpg119474.engine.factory.ItemFactory;
import it.unicam.cs.mpgc.rpg119474.engine.loot.LootService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultProgressionServiceTest {

    /** Bottino disattivato: qui interessa solo la progressione. */
    private static final LootService NO_LOOT = defeated -> List.of();

    private final ProgressionService progression = new DefaultProgressionService(NO_LOOT);

    @Test
    void awardsTheExperienceOfTheDefeatedEnemy() {
        PlayerCharacter player = new PlayerCharacter("Eroe", SurvivorClass.BRUTO);
        Enemy enemy = EnemyFactory.mutantDog(); // 50 XP

        VictoryOutcome outcome = progression.awardVictory(player, enemy);

        assertEquals(enemy.experienceReward(), outcome.experienceGained());
        assertEquals(enemy.experienceReward(), player.experience());
    }

    @Test
    void reportsTheLevelUpWhenTheThresholdIsReached() {
        PlayerCharacter player = new PlayerCharacter("Eroe", SurvivorClass.BRUTO);

        VictoryOutcome first = progression.awardVictory(player, EnemyFactory.ghoul()); // 90 XP
        assertFalse(first.leveledUp(), "90 XP non bastano per il livello 2 (soglia 100)");

        VictoryOutcome second = progression.awardVictory(player, EnemyFactory.mutantDog()); // 140 XP
        assertTrue(second.leveledUp());
        assertEquals(1, second.levelBefore());
        assertEquals(2, second.levelAfter());
        assertEquals(2, player.level());
    }

    @Test
    void healsThePlayerToFullHealth() {
        PlayerCharacter player = new PlayerCharacter("Eroe", SurvivorClass.MEDICO);
        player.takeDamage(30);

        progression.awardVictory(player, EnemyFactory.raider());

        assertEquals(player.maxHealth(), player.currentHealth());
    }

    @Test
    void collectsTheLootIntoTheInventory() {
        Item medikit = ItemFactory.medikit();
        LootService always = defeated -> List.of(medikit);
        PlayerCharacter player = new PlayerCharacter("Eroe", SurvivorClass.BRUTO);

        VictoryOutcome outcome = new DefaultProgressionService(always)
                .awardVictory(player, EnemyFactory.raider());

        assertTrue(outcome.hasLoot());
        assertEquals(1, player.inventory().size());
        assertEquals(medikit, player.inventory().asList().get(0));
    }
}
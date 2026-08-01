package it.unicam.cs.mpgc.rpg119474.engine.progression;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.engine.factory.EnemyFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultProgressionServiceTest {

    private final ProgressionService progression = new DefaultProgressionService();

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
}
package it.unicam.cs.mpgc.rpg119474.engine.campaign;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.engine.factory.EnemyFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CampaignTest {

    private final Campaign campaign = Campaigns.wasteland();

    @Test
    void theStagesAreNumberedInOrder() {
        for (int number = 1; number <= campaign.length(); number++) {
            assertEquals(number, campaign.stage(number).number());
        }
    }

    @Test
    void theDifficultyKeepsGrowing() {
        int previousReward = 0;
        for (CampaignStage stage : campaign.stages()) {
            Enemy enemy = stage.spawnEnemy();
            assertTrue(enemy.experienceReward() > previousReward,
                    "la tappa " + stage.number() + " deve valere piu' della precedente");
            previousReward = enemy.experienceReward();
        }
    }

    @Test
    void onlyTheLastStageIsTheFinalOne() {
        assertTrue(campaign.isFinalStage(campaign.length()));
        assertFalse(campaign.isFinalStage(campaign.length() - 1));
    }

    @Test
    void stagesOutsideTheCampaignAreRejected() {
        assertFalse(campaign.contains(0));
        assertFalse(campaign.contains(campaign.length() + 1));
        assertThrows(IllegalArgumentException.class, () -> campaign.stage(0));
        assertThrows(IllegalArgumentException.class, () -> campaign.stage(campaign.length() + 1));
    }

    @Test
    void everyAttemptSpawnsAFreshEnemy() {
        CampaignStage stage = campaign.stage(1);
        Enemy first = stage.spawnEnemy();
        first.takeDamage(first.maxHealth());

        Enemy second = stage.spawnEnemy();

        assertFalse(first.isAlive());
        assertEquals(second.maxHealth(), second.currentHealth(),
                "ripetere una tappa non deve riproporre il nemico gia' ferito");
    }

    @Test
    void anEmptyCampaignIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Campaign("Vuota", List.of()));
    }

    @Test
    void aStageMustBeNumberedFromOne() {
        assertThrows(IllegalArgumentException.class,
                () -> new CampaignStage(0, "Nessun luogo", EnemyFactory::scavenger));
    }
}
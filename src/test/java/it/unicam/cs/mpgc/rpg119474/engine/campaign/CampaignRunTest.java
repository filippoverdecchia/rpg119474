package it.unicam.cs.mpgc.rpg119474.engine.campaign;

import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.engine.factory.CharacterFactory;
import it.unicam.cs.mpgc.rpg119474.engine.factory.ItemFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CampaignRunTest {

    private final Campaign campaign = Campaigns.wasteland();

    /** Penalita' neutra: qui interessa l'avanzamento, non il prezzo della sconfitta. */
    private static final DefeatPenalty NO_PENALTY = player -> List.of();

    private CampaignRun runFor(PlayerCharacter player) {
        return new CampaignRun(campaign, NO_PENALTY, player);
    }

    private static PlayerCharacter survivor() {
        return CharacterFactory.createSurvivor("Eroe", SurvivorClass.BRUTO);
    }

    @Test
    void aNewRunStartsFromTheFirstStage() {
        CampaignRun run = runFor(survivor());

        assertEquals(1, run.currentStageNumber());
        assertEquals(campaign.stage(1), run.currentStage());
        assertFalse(run.isComplete());
    }

    @Test
    void winningMovesToTheNextStage() {
        CampaignRun run = runFor(survivor());

        StageOutcome outcome = run.recordVictory();

        assertTrue(outcome.cleared());
        assertFalse(outcome.hasLosses(), "vincere non costa nulla");
        assertFalse(outcome.campaignComplete());
        assertEquals(2, run.currentStageNumber());
    }

    @Test
    void losingKeepsThePlayerOnTheSameStage() {
        CampaignRun run = runFor(survivor());
        run.recordVictory(); // siamo alla tappa 2

        StageOutcome outcome = run.recordDefeat();

        assertFalse(outcome.cleared());
        assertEquals(2, outcome.stageNumber());
        assertEquals(2, run.currentStageNumber(), "la tappa persa va ripetuta");
    }

    @Test
    void theDefeatPenaltyIsApplied() {
        PlayerCharacter player = survivor();
        player.inventory().add(ItemFactory.medikit());
        CampaignRun run = new CampaignRun(campaign, new SuppliesLostPenalty(), player);

        StageOutcome outcome = run.recordDefeat();

        assertTrue(outcome.hasLosses());
        assertTrue(player.inventory().isEmpty());
    }

    @Test
    void clearingTheLastStageCompletesTheCampaign() {
        CampaignRun run = new CampaignRun(campaign, NO_PENALTY, survivor(), campaign.length());

        StageOutcome outcome = run.recordVictory();

        assertTrue(outcome.campaignComplete());
        assertTrue(run.isComplete());
    }

    @Test
    void thereAreNoStagesLeftOnceTheCampaignIsOver() {
        CampaignRun run = new CampaignRun(campaign, NO_PENALTY, survivor(), campaign.length());
        run.recordVictory();

        assertThrows(IllegalStateException.class, run::currentStage);
        assertThrows(IllegalStateException.class, run::recordVictory);
    }

    @Test
    void aRunOutsideTheCampaignIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new CampaignRun(campaign, NO_PENALTY, survivor(), 0));
        assertThrows(IllegalArgumentException.class,
                () -> new CampaignRun(campaign, NO_PENALTY, survivor(), campaign.length() + 2));
    }

    @Test
    void aSavedRunIsResumedWhereItWasLeft() {
        PlayerCharacter player = CharacterFactory.createSurvivor("Vagabondo", SurvivorClass.CECCHINO);
        player.gainExperience(150);
        player.inventory().add(ItemFactory.medikit());
        CampaignRun run = runFor(player);
        run.recordVictory();
        run.recordVictory();
        run.recordVictory();

        CampaignRun resumed = CampaignRun.resume(campaign, NO_PENALTY, run.toSave());

        assertEquals(4, resumed.currentStageNumber());
        assertEquals(player.level(), resumed.player().level());
        assertEquals(player.experience(), resumed.player().experience());
        assertEquals(1, resumed.player().inventory().size());
    }
}

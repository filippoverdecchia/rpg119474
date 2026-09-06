package it.unicam.cs.mpgc.rpg119474.engine.campaign;

import it.unicam.cs.mpgc.rpg119474.engine.factory.ItemFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StageOutcomeTest {

    @Test
    void reportsWhetherSomethingWasLost() {
        assertFalse(new StageOutcome(1, "Tappa", true, List.of(), false).hasLosses());
        assertTrue(new StageOutcome(1, "Tappa", false, List.of(ItemFactory.medikit()), false).hasLosses());
    }

    @Test
    void aClearedStageCannotCostAnything() {
        assertThrows(IllegalArgumentException.class,
                () -> new StageOutcome(1, "Tappa", true, List.of(ItemFactory.medikit()), false));
    }

    @Test
    void theCampaignCannotEndWithADefeat() {
        assertThrows(IllegalArgumentException.class,
                () -> new StageOutcome(8, "Tappa finale", false, List.of(), true));
    }
}
package it.unicam.cs.mpgc.rpg119474.engine.progression;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VictoryOutcomeTest {

    @Test
    void detectsTheLevelUp() {
        assertTrue(new VictoryOutcome(100, 1, 2).leveledUp());
        assertFalse(new VictoryOutcome(100, 3, 3).leveledUp());
    }

    @Test
    void rejectsALevelThatGoesDown() {
        assertThrows(IllegalArgumentException.class, () -> new VictoryOutcome(10, 3, 2));
    }

    @Test
    void rejectsNegativeExperience() {
        assertThrows(IllegalArgumentException.class, () -> new VictoryOutcome(-1, 1, 1));
    }
}
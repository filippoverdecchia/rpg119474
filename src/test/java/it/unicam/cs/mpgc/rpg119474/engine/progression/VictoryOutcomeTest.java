package it.unicam.cs.mpgc.rpg119474.engine.progression;

import it.unicam.cs.mpgc.rpg119474.engine.factory.ItemFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VictoryOutcomeTest {

    @Test
    void detectsTheLevelUp() {
        assertTrue(new VictoryOutcome(100, 1, 2, List.of()).leveledUp());
        assertFalse(new VictoryOutcome(100, 3, 3, List.of()).leveledUp());
    }

    @Test
    void detectsTheLoot() {
        assertFalse(new VictoryOutcome(10, 1, 1, List.of()).hasLoot());
        assertTrue(new VictoryOutcome(10, 1, 1, List.of(ItemFactory.medikit())).hasLoot());
    }

    @Test
    void rejectsALevelThatGoesDown() {
        assertThrows(IllegalArgumentException.class, () -> new VictoryOutcome(10, 3, 2, List.of()));
    }

    @Test
    void rejectsNegativeExperience() {
        assertThrows(IllegalArgumentException.class, () -> new VictoryOutcome(-1, 1, 1, List.of()));
    }
}
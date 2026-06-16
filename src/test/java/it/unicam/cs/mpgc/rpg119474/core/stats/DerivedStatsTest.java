package it.unicam.cs.mpgc.rpg119474.core.stats;

import it.unicam.cs.mpgc.rpg119474.core.attribute.Attributes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DerivedStatsTest {

    @Test
    void computesValuesFromAttributes() {
        DerivedStats d = DerivedStats.from(new Attributes(5, 5, 4, 6, 5));
        assertEquals(80, d.maxHealth());        // 40 + Resistenza(4) * 10
        assertEquals(8, d.defense());           // Resistenza(4) * 2
        assertEquals(6, d.initiative());        // Agilita'(6)
        assertEquals(9, d.maxActionPoints());   // 6 + Agilita'(6) / 2
        assertEquals(25, d.critChance());       // 5 + Fortuna(5) * 4
    }

    @Test
    void critChanceIsCappedAt60() {
        assertEquals(60, DerivedStats.from(new Attributes(1, 1, 1, 1, 20)).critChance());
    }
}

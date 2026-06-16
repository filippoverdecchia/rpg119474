package it.unicam.cs.mpgc.rpg119474.core.ability;

import it.unicam.cs.mpgc.rpg119474.core.stats.DamageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DamageCalculatorTest {

    @Test
    void ballisticUsesFullDefense() {
        assertEquals(10, DamageCalculator.afterDefense(20, 10, DamageType.BALISTICO));
    }

    @Test
    void energyIgnoresHalfDefense() {
        assertEquals(15, DamageCalculator.afterDefense(20, 10, DamageType.ENERGIA)); // 20 - 10/2
    }

    @Test
    void poisonIgnoresDefense() {
        assertEquals(20, DamageCalculator.afterDefense(20, 10, DamageType.VELENO));
    }

    @Test
    void damageIsNeverBelowOne() {
        assertEquals(1, DamageCalculator.afterDefense(3, 100, DamageType.MISCHIA));
    }
}

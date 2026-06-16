package it.unicam.cs.mpgc.rpg119474.core.attribute;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AttributesTest {

    @Test
    void rejectsNonPositiveAttribute() {
        assertThrows(IllegalArgumentException.class, () -> new Attributes(0, 1, 1, 1, 1));
    }

    @Test
    void modifierAddsBonus() {
        Attributes a = new Attributes(5, 5, 5, 5, 5);
        assertEquals(8, a.with(new AttributeModifier(3, 0, 0, 0, 0)).forza());
    }

    @Test
    void modifierClampsToOne() {
        Attributes a = new Attributes(5, 5, 5, 5, 5);
        assertEquals(1, a.with(new AttributeModifier(-100, 0, 0, 0, 0)).forza());
    }
}

package it.unicam.cs.mpgc.rpg119474.core.character;

import it.unicam.cs.mpgc.rpg119474.core.attribute.AttributeModifier;
import it.unicam.cs.mpgc.rpg119474.core.item.Rarity;
import it.unicam.cs.mpgc.rpg119474.core.item.Weapon;
import it.unicam.cs.mpgc.rpg119474.core.stats.DamageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerCharacterTest {

    @Test
    void equippingWeaponRaisesAttributeAndDamage() {
        PlayerCharacter hero = new PlayerCharacter("Test", SurvivorClass.CECCHINO);
        int basePerception = hero.attributes().percezione();
        hero.equip(new Weapon("Fucile", Rarity.COMUNE, "", DamageType.BALISTICO, 5,
                new AttributeModifier(0, 2, 0, 0, 0)));
        assertEquals(basePerception + 2, hero.attributes().percezione());
        assertEquals(5, hero.weaponDamage());
    }

    @Test
    void levelingRaisesAttributes() {
        PlayerCharacter hero = new PlayerCharacter("Test", SurvivorClass.BRUTO);
        int baseStrength = hero.attributes().forza();
        hero.gainExperience(100); // soglia livello 2 = 100
        assertEquals(2, hero.level());
        assertEquals(baseStrength + 1, hero.attributes().forza());
    }

    @Test
    void startsAtFullHealth() {
        PlayerCharacter hero = new PlayerCharacter("Test", SurvivorClass.MEDICO);
        assertEquals(hero.maxHealth(), hero.currentHealth());
    }
}

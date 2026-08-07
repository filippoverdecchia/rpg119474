package it.unicam.cs.mpgc.rpg119474.core.character;

import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.engine.factory.CharacterFactory;
import it.unicam.cs.mpgc.rpg119474.engine.factory.ItemFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * La scheda del personaggio viene conservata tra un accesso e l'altro: questi test
 * verificano che venga ricalcolata ogni volta che qualcosa la rende obsoleta.
 * Ogni caso legge la scheda PRIMA della modifica, cosi' se l'invalidazione mancasse
 * resterebbe in memoria il valore vecchio e il test fallirebbe.
 */
class CharacterSheetCacheTest {

    @Test
    void equippingAnArmorUpdatesTheSheet() {
        PlayerCharacter hero = CharacterFactory.createSurvivor("Eroe", SurvivorClass.BRUTO);
        int healthBefore = hero.maxHealth();
        int defenceBefore = hero.derived().defense();

        Item armor = ItemFactory.combatArmor(); // +3 Resistenza
        hero.inventory().add(armor);
        hero.equipFromInventory(armor);

        assertEquals(healthBefore + 30, hero.maxHealth());
        assertEquals(defenceBefore + 6, hero.derived().defense());
    }

    @Test
    void levellingUpUpdatesTheSheet() {
        PlayerCharacter hero = CharacterFactory.createSurvivor("Eroe", SurvivorClass.BRUTO);
        int strengthBefore = hero.attributes().forza();
        int healthBefore = hero.maxHealth();

        hero.gainExperience(100); // soglia del livello 2

        assertEquals(2, hero.level());
        assertEquals(strengthBefore + 1, hero.attributes().forza());
        assertEquals(healthBefore + 10, hero.maxHealth());
    }

    @Test
    void experienceWithoutALevelUpLeavesTheSheetUnchanged() {
        PlayerCharacter hero = CharacterFactory.createSurvivor("Eroe", SurvivorClass.BRUTO);
        int strengthBefore = hero.attributes().forza();

        hero.gainExperience(5);

        assertEquals(strengthBefore, hero.attributes().forza());
    }

    @Test
    void replacingTheArmorWithAWeakerOneLowersTheSheet() {
        PlayerCharacter hero = CharacterFactory.createSurvivor("Eroe", SurvivorClass.BRUTO);
        Item heavy = ItemFactory.combatArmor();  // +3 Resistenza
        Item light = ItemFactory.leatherArmor(); // +1 Resistenza
        hero.inventory().add(heavy);
        hero.equipFromInventory(heavy);
        int healthWithHeavy = hero.maxHealth();
        hero.inventory().add(light);

        hero.equipFromInventory(light);

        assertEquals(healthWithHeavy - 20, hero.maxHealth());
        assertTrue(hero.currentHealth() <= hero.maxHealth(),
                "i punti vita correnti non devono superare il nuovo massimo");
    }

    @Test
    void damageAndHealingDoNotAlterTheSheet() {
        PlayerCharacter hero = CharacterFactory.createSurvivor("Eroe", SurvivorClass.BRUTO);
        int maxHealth = hero.maxHealth();

        hero.takeDamage(10);
        hero.heal(5);

        assertEquals(maxHealth, hero.maxHealth());
    }

    @Test
    void abilitiesAreNotModifiableFromOutside() {
        PlayerCharacter hero = CharacterFactory.createSurvivor("Eroe", SurvivorClass.CECCHINO);

        assertThrows(UnsupportedOperationException.class, () -> hero.abilities().clear());
    }
}
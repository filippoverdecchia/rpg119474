package it.unicam.cs.mpgc.rpg119474.core.character;

import it.unicam.cs.mpgc.rpg119474.core.attribute.AttributeModifier;
import it.unicam.cs.mpgc.rpg119474.core.item.Armor;
import it.unicam.cs.mpgc.rpg119474.core.item.Consumable;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.core.item.Rarity;
import it.unicam.cs.mpgc.rpg119474.core.item.Weapon;
import it.unicam.cs.mpgc.rpg119474.core.stats.DamageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void equipsAnArmorTakenFromTheInventory() {
        PlayerCharacter hero = new PlayerCharacter("Test", SurvivorClass.BRUTO);
        int healthBefore = hero.maxHealth();
        Item armor = new Armor("Giubbotto", Rarity.COMUNE, "",
                new AttributeModifier(0, 0, 2, 0, 0));
        hero.inventory().add(armor);

        assertTrue(hero.equipFromInventory(armor));
        assertTrue(hero.inventory().isEmpty(), "l'oggetto equipaggiato esce dall'inventario");
        assertEquals(healthBefore + 20, hero.maxHealth(), "+2 Resistenza = +20 PV");
    }

    @Test
    void theReplacedEquipmentGoesBackToTheInventory() {
        PlayerCharacter hero = new PlayerCharacter("Test", SurvivorClass.BRUTO);
        Item first = new Armor("Giubbotto", Rarity.COMUNE, "", new AttributeModifier(0, 0, 1, 0, 0));
        Item second = new Armor("Corazza", Rarity.RARA, "", new AttributeModifier(0, 0, 3, 0, 0));
        hero.inventory().add(first);
        hero.equipFromInventory(first);
        hero.inventory().add(second);

        hero.equipFromInventory(second);

        assertEquals("Corazza", hero.armor().orElseThrow().name());
        assertTrue(hero.inventory().asList().contains(first));
    }

    @Test
    void consumablesAndUnownedItemsCannotBeEquipped() {
        PlayerCharacter hero = new PlayerCharacter("Test", SurvivorClass.BRUTO);
        Item medikit = new Consumable("Medikit", Rarity.COMUNE, "", 30);
        hero.inventory().add(medikit);

        assertFalse(hero.equipFromInventory(medikit), "un consumabile non si indossa");
        assertTrue(hero.inventory().asList().contains(medikit));
        assertFalse(hero.equipFromInventory(
                new Armor("Ignota", Rarity.COMUNE, "", AttributeModifier.NONE)));
    }
}
package it.unicam.cs.mpgc.rpg119474.engine.campaign;

import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.engine.factory.CharacterFactory;
import it.unicam.cs.mpgc.rpg119474.engine.factory.ItemFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SuppliesLostPenaltyTest {

    private final DefeatPenalty penalty = new SuppliesLostPenalty();

    @Test
    void onlyTheSuppliesAreLost() {
        PlayerCharacter player = CharacterFactory.createSurvivor("Eroe", SurvivorClass.BRUTO);
        player.inventory().add(ItemFactory.medikit());
        player.inventory().add(ItemFactory.medikit());
        player.inventory().add(ItemFactory.combatArmor());

        List<Item> lost = penalty.applyTo(player);

        assertEquals(2, lost.size(), "si perdono i due medikit");
        assertEquals(1, player.inventory().size());
        assertEquals("Armatura da combattimento", player.inventory().asList().get(0).name(),
                "l'equipaggiamento non si perde");
    }

    @Test
    void theSurvivorWakesUpAtHalfHealth() {
        PlayerCharacter player = CharacterFactory.createSurvivor("Eroe", SurvivorClass.BRUTO);
        player.takeDamage(player.currentHealth()); // sconfitto

        penalty.applyTo(player);

        assertEquals(player.maxHealth() / 2, player.currentHealth());
        assertTrue(player.isAlive(), "deve poter ritentare la tappa");
    }

    @Test
    void anEmptyBackpackCostsNothing() {
        PlayerCharacter player = CharacterFactory.createSurvivor("Eroe", SurvivorClass.BRUTO);

        assertTrue(penalty.applyTo(player).isEmpty());
    }
}
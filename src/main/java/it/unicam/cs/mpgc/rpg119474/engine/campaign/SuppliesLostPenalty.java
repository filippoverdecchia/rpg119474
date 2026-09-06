package it.unicam.cs.mpgc.rpg119474.engine.campaign;

import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.item.Consumable;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;

import java.util.List;

/**
 * Penalita' predefinita: chi cade perde tutte le scorte e si risveglia malconcio.
 * <p>
 * L'equipaggiamento indossato resta, cosi' come l'esperienza accumulata: a essere
 * colpito e' il materiale di consumo, cioe' proprio la risorsa che permette di
 * reggere gli scontri piu' lunghi. Ritentare la stessa tappa e' quindi possibile
 * ma piu' difficile.
 */
public class SuppliesLostPenalty implements DefeatPenalty {

    /** Quota dei punti vita massimi con cui si riprende conoscenza. */
    private static final int REVIVE_PERCENT = 50;

    @Override
    public List<Item> applyTo(PlayerCharacter player) {
        List<Item> lost = player.inventory().stream()
                .filter(Consumable.class::isInstance)
                .toList();
        lost.forEach(player.inventory()::remove);
        player.heal(player.maxHealth() * REVIVE_PERCENT / 100);
        return lost;
    }
}
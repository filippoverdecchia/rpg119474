package it.unicam.cs.mpgc.rpg119474.engine.campaign;

import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;

import java.util.List;

/**
 * Prezzo pagato quando una tappa viene persa.
 * <p>
 * La campagna non si interrompe: il sopravvissuto riprende conoscenza e puo'
 * ritentare, ma qualcosa lascia sul campo. Quale sia questo prezzo e' una regola
 * di gioco a se' stante, quindi un'interfaccia funzionale: renderla piu' o meno
 * severa non richiede di toccare lo svolgimento della campagna.
 */
@FunctionalInterface
public interface DefeatPenalty {

    /**
     * Applica la penalita' al sopravvissuto sconfitto, rimettendolo in condizione
     * di ritentare la tappa.
     *
     * @return gli oggetti che ha perduto, per poterlo raccontare al giocatore
     */
    List<Item> applyTo(PlayerCharacter player);
}
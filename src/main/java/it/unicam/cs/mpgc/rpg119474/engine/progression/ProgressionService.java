package it.unicam.cs.mpgc.rpg119474.engine.progression;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;

/**
 * Regola di progressione applicata quando un duello viene vinto.
 * <p>
 * E' un'astrazione a se' stante perche' la ricompensa e' una regola di gioco
 * indipendente dallo svolgimento del combattimento: cambiarla (esperienza
 * diversa, nessuna cura, penalita') non deve richiedere modifiche al motore
 * ne' all'interfaccia (Open/Closed e Dependency Inversion).
 */
@FunctionalInterface
public interface ProgressionService {

    /** Applica al vincitore la ricompensa per aver sconfitto il nemico. */
    VictoryOutcome awardVictory(PlayerCharacter player, Enemy defeated);
}
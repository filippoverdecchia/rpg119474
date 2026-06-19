package it.unicam.cs.mpgc.rpg119474.engine.ai;

import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;

import java.util.Optional;

/**
 * Strategia dell'IA del nemico (Strategy pattern). Interfaccia funzionale: data
 * la situazione di turno, decide l'azione da compiere, oppure {@link Optional#empty()}
 * se intende passare. Cambiare comportamento = passare una strategia diversa.
 */
@FunctionalInterface
public interface EnemyStrategy {

    Optional<EnemyAction> decide(GameCharacter self, GameCharacter opponent, int actionPoints);
}
package it.unicam.cs.mpgc.rpg119474.engine.combat;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;
import it.unicam.cs.mpgc.rpg119474.engine.event.Observer;

import java.util.Optional;

/**
 * Contratto di un motore di combattimento a turni. Definisce le operazioni
 * disponibili senza vincolare l'implementazione: chi lo usa (la UI, una demo)
 * dipende da questa interfaccia, non dalla classe concreta (Dependency Inversion).
 */
public interface CombatEngine {

    /** Avvia il combattimento: calcola l'ordine dei turni e apre il primo turno. */
    void start();

    /** Combattente a cui tocca agire ora. */
    GameCharacter currentActor();

    /** Punti Azione ancora disponibili nel turno corrente. */
    int currentActionPoints();

    /** {@code true} se il combattimento e' terminato. */
    boolean isOver();

    /** Vincitore, se il combattimento e' finito. */
    Optional<GameCharacter> winner();

    /** Il combattente di turno usa un'abilita' su un bersaglio (spende i Punti Azione). */
    void useAbility(Ability ability, GameCharacter target);

    /** Chiude il turno corrente e passa al combattente successivo. */
    void endTurn();

    /** Registra un osservatore che ricevera' gli eventi di combattimento. */
    void subscribe(Observer<CombatEvent> observer);
}
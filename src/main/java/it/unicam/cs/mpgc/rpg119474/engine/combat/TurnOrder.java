package it.unicam.cs.mpgc.rpg119474.engine.combat;

import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Ordine dei turni: dispone i combattenti per iniziativa decrescente (chi e' piu'
 * agile agisce prima) e li scorre ciclicamente. E' generico sul numero di
 * combattenti, anche se nel duello 1v1 ne gestisce due.
 */
public class TurnOrder {

    private final List<GameCharacter> order;
    private int index = 0;

    public TurnOrder(List<GameCharacter> combatants) {
        Objects.requireNonNull(combatants, "combatants");
        if (combatants.isEmpty()) {
            throw new IllegalArgumentException("Serve almeno un combattente");
        }
        // Ordino per iniziativa decrescente; a parita', l'ordinamento stabile
        // mantiene l'ordine d'ingresso (quindi il giocatore, se passato per primo).
        this.order = combatants.stream()
                .sorted(Comparator.comparingInt((GameCharacter c) -> c.derived().initiative()).reversed())
                .toList();
    }

    /** Combattente di turno. */
    public GameCharacter current() {
        return order.get(index);
    }

    /** Passa al combattente successivo (in modo ciclico) e lo restituisce. */
    public GameCharacter next() {
        index = (index + 1) % order.size();
        return current();
    }

    /** Ordine completo dei turni (lista non modificabile). */
    public List<GameCharacter> ordered() {
        return order;
    }
}
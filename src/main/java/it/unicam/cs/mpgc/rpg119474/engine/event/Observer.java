package it.unicam.cs.mpgc.rpg119474.engine.event;

/**
 * Osservatore generico di eventi (pattern Observer). E' un'interfaccia
 * funzionale, quindi un osservatore puo' essere scritto anche come lambda.
 *
 * @param <E> tipo degli eventi osservati
 */
@FunctionalInterface
public interface Observer<E> {

    void onEvent(E event);
}
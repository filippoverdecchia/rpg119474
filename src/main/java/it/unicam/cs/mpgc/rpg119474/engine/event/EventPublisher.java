package it.unicam.cs.mpgc.rpg119474.engine.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Sorgente osservabile (pattern Observer): tiene un elenco di osservatori e
 * notifica loro ogni evento pubblicato. Generica sul tipo di evento.
 *
 * @param <E> tipo degli eventi pubblicati
 */
public class EventPublisher<E> {

    private final List<Observer<E>> observers = new ArrayList<>();

    public void subscribe(Observer<E> observer) {
        observers.add(Objects.requireNonNull(observer, "observer"));
    }

    public void unsubscribe(Observer<E> observer) {
        observers.remove(observer);
    }

    /** Notifica l'evento a tutti gli osservatori registrati. */
    public void publish(E event) {
        // Copia difensiva: un osservatore potrebbe disiscriversi durante la notifica.
        for (Observer<E> observer : new ArrayList<>(observers)) {
            observer.onEvent(event);
        }
    }
}
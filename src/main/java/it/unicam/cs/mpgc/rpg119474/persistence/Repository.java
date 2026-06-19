package it.unicam.cs.mpgc.rpg119474.persistence;

import java.util.List;
import java.util.Optional;

/**
 * Astrazione per salvare e recuperare entita' identificate da un id.
 * Generica sul tipo di entita': la logica di gioco dipende da questa interfaccia,
 * non dal modo concreto di salvare (serializzazione, JSON, database, ...).
 *
 * @param <T> tipo dell'entita' gestita
 */
public interface Repository<T> {

    void save(String id, T entity);

    Optional<T> load(String id);

    List<String> listIds();

    boolean delete(String id);
}
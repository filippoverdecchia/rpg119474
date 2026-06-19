package it.unicam.cs.mpgc.rpg119474.persistence;

/** Errore non controllato sollevato quando un'operazione di persistenza fallisce. */
public class PersistenceException extends RuntimeException {

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
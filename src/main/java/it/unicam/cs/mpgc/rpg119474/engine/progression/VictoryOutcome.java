package it.unicam.cs.mpgc.rpg119474.engine.progression;

/**
 * Esito della vittoria in un duello: quanta esperienza e' stata guadagnata e
 * come e' cambiato il livello del sopravvissuto.
 * <p>
 * E' un dato puro, senza testo per l'utente: la formattazione del messaggio
 * spetta allo strato di presentazione.
 */
public record VictoryOutcome(int experienceGained, int levelBefore, int levelAfter) {

    public VictoryOutcome {
        if (experienceGained < 0) {
            throw new IllegalArgumentException("experienceGained deve essere >= 0");
        }
        if (levelAfter < levelBefore) {
            throw new IllegalArgumentException("il livello non puo' diminuire");
        }
    }

    /** {@code true} se il sopravvissuto e' salito almeno di un livello. */
    public boolean leveledUp() {
        return levelAfter > levelBefore;
    }
}
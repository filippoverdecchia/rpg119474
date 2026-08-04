package it.unicam.cs.mpgc.rpg119474.engine.progression;

import it.unicam.cs.mpgc.rpg119474.core.item.Item;

import java.util.List;
import java.util.Objects;

/**
 * Esito della vittoria in un duello: esperienza guadagnata, come e' cambiato il
 * livello del sopravvissuto e quale bottino ha recuperato.
 * <p>
 * E' un dato puro, senza testo per l'utente: la formattazione del messaggio
 * spetta allo strato di presentazione.
 */
public record VictoryOutcome(int experienceGained, int levelBefore, int levelAfter, List<Item> loot) {

    public VictoryOutcome {
        Objects.requireNonNull(loot, "loot");
        if (experienceGained < 0) {
            throw new IllegalArgumentException("experienceGained deve essere >= 0");
        }
        if (levelAfter < levelBefore) {
            throw new IllegalArgumentException("il livello non puo' diminuire");
        }
        loot = List.copyOf(loot);
    }

    /** {@code true} se il sopravvissuto e' salito almeno di un livello. */
    public boolean leveledUp() {
        return levelAfter > levelBefore;
    }

    /** {@code true} se il nemico ha lasciato almeno un oggetto. */
    public boolean hasLoot() {
        return !loot.isEmpty();
    }
}
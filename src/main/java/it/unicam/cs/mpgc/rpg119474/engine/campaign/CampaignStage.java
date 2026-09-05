package it.unicam.cs.mpgc.rpg119474.engine.campaign;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Una tappa della campagna: il numero d'ordine, il luogo in cui si svolge e
 * l'avversario che vi si incontra.
 * <p>
 * L'avversario e' un costruttore e non un'istanza: ogni volta che la tappa viene
 * affrontata nasce un nemico nuovo, con i punti vita pieni. Cosi' ripetere una
 * tappa dopo una sconfitta non ripropone l'avversario ferito dallo scontro
 * precedente.
 *
 * @param number numero della tappa, a partire da 1
 * @param title  nome del luogo, mostrato come titolo del capitolo
 * @param enemy  costruttore dell'avversario di questa tappa
 */
public record CampaignStage(int number, String title, Supplier<Enemy> enemy) {

    public CampaignStage {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(enemy, "enemy");
        if (number < 1) {
            throw new IllegalArgumentException("number deve essere >= 1");
        }
    }

    /** Crea un nuovo avversario per questa tappa. */
    public Enemy spawnEnemy() {
        return enemy.get();
    }
}
package it.unicam.cs.mpgc.rpg119474.engine.campaign;

import it.unicam.cs.mpgc.rpg119474.core.item.Item;

import java.util.List;
import java.util.Objects;

/**
 * Esito di una tappa della campagna: se sia stata superata, cosa e' stato perduto
 * in caso di sconfitta e se con essa la campagna sia giunta al termine.
 * <p>
 * Come {@code VictoryOutcome}, riporta dati e non frasi: e' lo strato di
 * presentazione a decidere come raccontarli.
 *
 * @param stageNumber      numero della tappa appena affrontata
 * @param stageTitle       luogo della tappa
 * @param cleared          {@code true} se la tappa e' stata superata
 * @param suppliesLost     oggetti perduti applicando la penalita' (vuoto se vinta)
 * @param campaignComplete {@code true} se era l'ultima tappa ed e' stata superata
 */
public record StageOutcome(int stageNumber, String stageTitle, boolean cleared,
                           List<Item> suppliesLost, boolean campaignComplete) {

    public StageOutcome {
        Objects.requireNonNull(stageTitle, "stageTitle");
        Objects.requireNonNull(suppliesLost, "suppliesLost");
        if (cleared && !suppliesLost.isEmpty()) {
            throw new IllegalArgumentException("una tappa superata non comporta perdite");
        }
        if (campaignComplete && !cleared) {
            throw new IllegalArgumentException("la campagna si conclude solo superando l'ultima tappa");
        }
        suppliesLost = List.copyOf(suppliesLost);
    }

    /** {@code true} se la sconfitta e' costata delle scorte. */
    public boolean hasLosses() {
        return !suppliesLost.isEmpty();
    }
}
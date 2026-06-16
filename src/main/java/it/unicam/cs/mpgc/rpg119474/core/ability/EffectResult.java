package it.unicam.cs.mpgc.rpg119474.core.ability;

import java.util.Objects;

/**
 * Esito dell'applicazione di un effetto, usato per la narrazione e per generare
 * gli eventi di combattimento. {@code healthDelta} e' negativo per i danni
 * inflitti e positivo per la salute recuperata.
 */
public record EffectResult(int healthDelta, String message, boolean critical) {

    public EffectResult {
        Objects.requireNonNull(message, "message");
    }
}

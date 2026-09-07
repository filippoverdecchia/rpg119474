package it.unicam.cs.mpgc.rpg119474.ui;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Le azioni che la schermata di combattimento delega a chi la ospita: salvare,
 * tornare al menu, chiudere la tappa e proseguire.
 * <p>
 * Raccoglierle in un unico oggetto evita un costruttore con troppi parametri
 * sciolti, in cui e' facile scambiarne due di posto senza che il compilatore se
 * ne accorga.
 *
 * @param onSave       salva la partita
 * @param onStageEnded chiude la tappa e restituisce il resoconto da mostrare
 * @param onContinue   prosegue: tappa successiva, nuovo tentativo o esito finale
 * @param onBackToMenu abbandona e torna alla schermata iniziale
 */
public record CombatActions(Runnable onSave, Supplier<String> onStageEnded,
                            Runnable onContinue, Runnable onBackToMenu) {

    public CombatActions {
        Objects.requireNonNull(onSave, "onSave");
        Objects.requireNonNull(onStageEnded, "onStageEnded");
        Objects.requireNonNull(onContinue, "onContinue");
        Objects.requireNonNull(onBackToMenu, "onBackToMenu");
    }
}
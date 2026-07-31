package it.unicam.cs.mpgc.rpg119474.ui;

import it.unicam.cs.mpgc.rpg119474.engine.combat.CombatEvent;

/**
 * Traduce in testo per l'utente gli eventi di combattimento. Sta nello strato di
 * presentazione perche' e' formattazione, non logica di gioco: il motore produce
 * eventi, qui diventano frasi. Averla in un solo posto evita che la stessa
 * traduzione venga duplicata in piu' schermate.
 */
public final class CombatEventFormatter {

    private CombatEventFormatter() {
        // Classe di utilita': non istanziabile.
    }

    /** Descrive un evento con uno switch esaustivo sulla gerarchia sealed. */
    public static String describe(CombatEvent event) {
        return switch (event) {
            case CombatEvent.CombatStarted s ->
                    "Inizia lo scontro: " + s.player().name() + " contro " + s.enemy().name();
            case CombatEvent.TurnStarted t ->
                    "-- Turno di " + t.actor().name() + " (PA " + t.actionPoints() + ")";
            case CombatEvent.AbilityUsed a -> "   " + a.result().message();
            case CombatEvent.CharacterDefeated d ->
                    "   " + d.character().name() + " e' stato sconfitto!";
            case CombatEvent.CombatEnded c -> "Vince " + c.winner().name() + "!";
        };
    }
}
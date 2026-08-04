package it.unicam.cs.mpgc.rpg119474.ui;

import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.engine.combat.CombatEvent;
import it.unicam.cs.mpgc.rpg119474.engine.progression.VictoryOutcome;

/**
 * Traduce in testo per l'utente gli eventi di combattimento e l'esito di una
 * vittoria. Sta nello strato di presentazione perche' e' formattazione, non
 * logica di gioco: il motore produce dati, qui diventano frasi. Averla in un
 * solo posto evita che la stessa traduzione venga duplicata in piu' schermate.
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
            case CombatEvent.ItemUsed i ->
                    "   " + i.actor().name() + " usa " + i.item().name()
                            + " e recupera " + i.healthRestored() + " PV.";
            case CombatEvent.CharacterDefeated d ->
                    "   " + d.character().name() + " e' stato sconfitto!";
            case CombatEvent.CombatEnded c -> "Vince " + c.winner().name() + "!";
        };
    }

    /** Descrive la ricompensa ottenuta vincendo il duello. */
    public static String describe(VictoryOutcome outcome) {
        StringBuilder message = new StringBuilder();
        message.append("Hai guadagnato ").append(outcome.experienceGained()).append(" punti esperienza.");
        if (outcome.leveledUp()) {
            message.append(" Sei salito al livello ").append(outcome.levelAfter()).append("!");
        }
        if (outcome.hasLoot()) {
            message.append(" Hai recuperato: ")
                    .append(String.join(", ", outcome.loot().stream().map(Item::name).toList()))
                    .append(".");
        }
        return message.toString();
    }
}
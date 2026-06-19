package it.unicam.cs.mpgc.rpg119474;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.engine.DefaultGameService;
import it.unicam.cs.mpgc.rpg119474.engine.GameService;
import it.unicam.cs.mpgc.rpg119474.engine.ai.EnemyStrategies;
import it.unicam.cs.mpgc.rpg119474.engine.combat.CombatEvent;
import it.unicam.cs.mpgc.rpg119474.engine.factory.CharacterFactory;
import it.unicam.cs.mpgc.rpg119474.engine.factory.EnemyFactory;

import java.util.Comparator;

/**
 * Punto di ingresso: dimostrazione testuale di un duello a turni.
 * Verra' sostituita dalla GUI JavaFX, che usera' lo stesso {@link GameService}.
 */
public final class App {

    private App() {
        // Classe di sola attivazione: non deve essere istanziata.
    }

    public static void main(String[] args) {
        RandomSource rng = RandomSource.seeded(7);
        PlayerCharacter hero = CharacterFactory.createSurvivor("Bruto", SurvivorClass.BRUTO);
        Enemy enemy = EnemyFactory.raider();

        GameService game = new DefaultGameService(rng);
        game.startDuel(hero, enemy, EnemyStrategies.aggressive(),
                event -> System.out.println(describe(event)));

        // Giocatore automatico: usa l'abilita' piu' forte che puo' permettersi, poi passa.
        int guard = 0;
        while (!game.isOver() && guard++ < 1000) {
            Ability chosen = hero.abilities().stream()
                    .filter(ability -> ability.actionPointCost() <= game.currentActionPoints())
                    .max(Comparator.comparingInt(Ability::actionPointCost))
                    .orElse(hero.abilities().get(0));
            game.playerUseAbility(chosen, enemy);
            if (!game.isOver()) {
                game.playerEndTurn();
            }
        }
    }

    /** Traduce un evento in testo con uno switch esaustivo sulla gerarchia sealed. */
    private static String describe(CombatEvent event) {
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
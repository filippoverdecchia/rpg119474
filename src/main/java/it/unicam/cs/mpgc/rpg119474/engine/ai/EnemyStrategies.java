package it.unicam.cs.mpgc.rpg119474.engine.ai;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Strategie predefinite per l'IA del nemico (metodi factory). */
public final class EnemyStrategies {

    private EnemyStrategies() {
        // Classe di utilita': non istanziabile.
    }

    /** Aggressiva: usa l'abilita' piu' costosa che puo' permettersi contro l'avversario. */
    public static EnemyStrategy aggressive() {
        return (self, opponent, actionPoints) -> self.abilities().stream()
                .filter(ability -> ability.actionPointCost() <= actionPoints)
                .max(Comparator.comparingInt(Ability::actionPointCost))
                .map(ability -> new EnemyAction(ability, opponent));
    }

    /** Casuale: sceglie a caso tra le abilita' che puo' permettersi. */
    public static EnemyStrategy random(RandomSource rng) {
        return (self, opponent, actionPoints) -> {
            List<Ability> affordable = self.abilities().stream()
                    .filter(ability -> ability.actionPointCost() <= actionPoints)
                    .toList();
            if (affordable.isEmpty()) {
                return Optional.empty();
            }
            Ability chosen = affordable.get(rng.nextInt(affordable.size()));
            return Optional.of(new EnemyAction(chosen, opponent));
        };
    }
}
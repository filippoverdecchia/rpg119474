package it.unicam.cs.mpgc.rpg119474.engine.ai;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.ability.EffectResult;
import it.unicam.cs.mpgc.rpg119474.core.attribute.Attributes;
import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnemyStrategiesTest {

    private static Ability ability(String name, int cost) {
        return new Ability(name, cost, (actor, target, rng) -> new EffectResult(0, name, false));
    }

    private final Enemy enemy = new Enemy("Nemico", new Attributes(5, 5, 5, 5, 5), 5,
            List.of(ability("Debole", 2), ability("Medio", 4), ability("Forte", 6)), 10);

    private final GameCharacter target = new Enemy("Bersaglio", new Attributes(5, 5, 5, 5, 5), 5,
            List.of(), 10);

    @Test
    void theAggressiveStrategyPicksTheStrongestAffordableAbility() {
        Optional<EnemyAction> action = EnemyStrategies.aggressive().decide(enemy, target, 5);

        assertTrue(action.isPresent());
        assertEquals("Medio", action.orElseThrow().ability().name(),
                "con 5 Punti Azione la piu' costosa sostenibile costa 4");
        assertSame(target, action.orElseThrow().target());
    }

    @Test
    void theAggressiveStrategyPassesWhenNothingIsAffordable() {
        assertTrue(EnemyStrategies.aggressive().decide(enemy, target, 1).isEmpty());
    }

    @Test
    void theRandomStrategyOnlyPicksAffordableAbilities() {
        RandomSource lastChoice = bound -> bound - 1;

        Optional<EnemyAction> action = EnemyStrategies.random(lastChoice).decide(enemy, target, 4);

        assertEquals("Medio", action.orElseThrow().ability().name(),
                "con 4 Punti Azione le sostenibili sono due: viene scelta l'ultima");
    }

    @Test
    void theRandomStrategyPassesWhenNothingIsAffordable() {
        assertTrue(EnemyStrategies.random(bound -> 0).decide(enemy, target, 0).isEmpty());
    }
}
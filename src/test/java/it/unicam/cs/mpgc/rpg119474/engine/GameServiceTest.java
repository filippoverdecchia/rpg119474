package it.unicam.cs.mpgc.rpg119474.engine;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.engine.ai.EnemyStrategies;
import it.unicam.cs.mpgc.rpg119474.engine.factory.CharacterFactory;
import it.unicam.cs.mpgc.rpg119474.engine.factory.EnemyFactory;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GameServiceTest {

    @Test
    void duelEndsWithAWinner() {
        RandomSource rng = RandomSource.seeded(1);
        GameService game = new DefaultGameService(rng);
        PlayerCharacter hero = CharacterFactory.createSurvivor("Eroe", SurvivorClass.BRUTO);
        Enemy enemy = EnemyFactory.mutantDog();

        game.startDuel(hero, enemy, EnemyStrategies.aggressive(), event -> { });

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

        assertTrue(game.isOver(), "il duello deve terminare");
        assertTrue(game.winner().isPresent(), "ci deve essere un vincitore");
    }
}
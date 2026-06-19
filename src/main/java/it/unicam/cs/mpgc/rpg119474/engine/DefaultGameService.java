package it.unicam.cs.mpgc.rpg119474.engine;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.engine.ai.EnemyAction;
import it.unicam.cs.mpgc.rpg119474.engine.ai.EnemyStrategy;
import it.unicam.cs.mpgc.rpg119474.engine.combat.CombatEngine;
import it.unicam.cs.mpgc.rpg119474.engine.combat.CombatEvent;
import it.unicam.cs.mpgc.rpg119474.engine.combat.TurnBasedCombatEngine;
import it.unicam.cs.mpgc.rpg119474.engine.event.Observer;

import java.util.Objects;
import java.util.Optional;

/**
 * Implementazione di default del {@link GameService}. Crea un motore a turni per
 * il duello e, quando tocca al nemico, ne guida le mosse tramite la
 * {@link EnemyStrategy}. La casualita' e' iniettata (testabilita').
 */
public class DefaultGameService implements GameService {

    private final RandomSource rng;

    private CombatEngine engine;
    private GameCharacter player;
    private GameCharacter enemy;
    private EnemyStrategy enemyStrategy;

    public DefaultGameService(RandomSource rng) {
        this.rng = Objects.requireNonNull(rng, "rng");
    }

    @Override
    public void startDuel(GameCharacter player, GameCharacter enemy,
                          EnemyStrategy enemyStrategy, Observer<CombatEvent> observer) {
        this.player = Objects.requireNonNull(player, "player");
        this.enemy = Objects.requireNonNull(enemy, "enemy");
        this.enemyStrategy = Objects.requireNonNull(enemyStrategy, "enemyStrategy");
        this.engine = new TurnBasedCombatEngine(player, enemy, rng);
        if (observer != null) {
            engine.subscribe(observer);
        }
        engine.start();
        runEnemyTurnsIfNeeded();
    }

    @Override
    public GameCharacter currentActor() {
        return engine.currentActor();
    }

    @Override
    public int currentActionPoints() {
        return engine.currentActionPoints();
    }

    @Override
    public boolean isOver() {
        return engine.isOver();
    }

    @Override
    public Optional<GameCharacter> winner() {
        return engine.winner();
    }

    @Override
    public void playerUseAbility(Ability ability, GameCharacter target) {
        requirePlayerTurn();
        engine.useAbility(ability, target);
    }

    @Override
    public void playerEndTurn() {
        requirePlayerTurn();
        engine.endTurn();
        runEnemyTurnsIfNeeded();
    }

    /** Fa agire il nemico (guidato dall'IA) finche' e' il suo turno. */
    private void runEnemyTurnsIfNeeded() {
        while (!engine.isOver() && engine.currentActor() == enemy) {
            Optional<EnemyAction> action =
                    enemyStrategy.decide(enemy, player, engine.currentActionPoints());
            action.ifPresent(a -> engine.useAbility(a.ability(), a.target()));
            if (!engine.isOver()) {
                engine.endTurn();
            }
        }
    }

    private void requirePlayerTurn() {
        if (engine.currentActor() != player) {
            throw new IllegalStateException("Non e' il turno del giocatore");
        }
    }
}
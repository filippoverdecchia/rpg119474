package it.unicam.cs.mpgc.rpg119474.engine;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.core.item.Consumable;
import it.unicam.cs.mpgc.rpg119474.engine.ai.EnemyAction;
import it.unicam.cs.mpgc.rpg119474.engine.ai.EnemyStrategy;
import it.unicam.cs.mpgc.rpg119474.engine.combat.CombatEngine;
import it.unicam.cs.mpgc.rpg119474.engine.combat.CombatEvent;
import it.unicam.cs.mpgc.rpg119474.engine.combat.TurnBasedCombatEngine;
import it.unicam.cs.mpgc.rpg119474.engine.event.Observer;
import it.unicam.cs.mpgc.rpg119474.engine.loot.DefaultLootService;
import it.unicam.cs.mpgc.rpg119474.engine.progression.DefaultProgressionService;
import it.unicam.cs.mpgc.rpg119474.engine.progression.ProgressionService;
import it.unicam.cs.mpgc.rpg119474.engine.progression.VictoryOutcome;

import java.util.Objects;
import java.util.Optional;

/**
 * Implementazione di default del {@link GameService}. Crea un motore a turni per
 * il duello, guida le mosse del nemico tramite la {@link EnemyStrategy} e delega
 * la ricompensa della vittoria al {@link ProgressionService}. Sia la casualita'
 * sia la regola di progressione sono iniettate, quindi sostituibili e testabili.
 */
public class DefaultGameService implements GameService {

    private final RandomSource rng;
    private final ProgressionService progressionService;

    private CombatEngine engine;
    private PlayerCharacter player;
    private Enemy enemy;
    private EnemyStrategy enemyStrategy;
    private boolean victoryResolved;

    public DefaultGameService(RandomSource rng) {
        this(rng, new DefaultProgressionService(new DefaultLootService(rng)));
    }

    public DefaultGameService(RandomSource rng, ProgressionService progressionService) {
        this.rng = Objects.requireNonNull(rng, "rng");
        this.progressionService = Objects.requireNonNull(progressionService, "progressionService");
    }

    @Override
    public void startDuel(PlayerCharacter player, Enemy enemy,
                          EnemyStrategy enemyStrategy, Observer<CombatEvent> observer) {
        this.player = Objects.requireNonNull(player, "player");
        this.enemy = Objects.requireNonNull(enemy, "enemy");
        this.enemyStrategy = Objects.requireNonNull(enemyStrategy, "enemyStrategy");
        this.engine = new TurnBasedCombatEngine(player, enemy, rng);
        this.victoryResolved = false;
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
    public void playerUseConsumable(Consumable item) {
        Objects.requireNonNull(item, "item");
        requirePlayerTurn();
        if (!player.inventory().asList().contains(item)) {
            throw new IllegalArgumentException("Il giocatore non possiede " + item.name());
        }
        engine.useConsumable(item);
        player.inventory().remove(item);
    }

    @Override
    public void playerEndTurn() {
        requirePlayerTurn();
        engine.endTurn();
        runEnemyTurnsIfNeeded();
    }

    @Override
    public Optional<VictoryOutcome> resolveVictory() {
        boolean playerWon = engine.isOver()
                && engine.winner().map(winner -> winner == player).orElse(false);
        if (!playerWon || victoryResolved) {
            return Optional.empty();
        }
        victoryResolved = true;
        return Optional.of(progressionService.awardVictory(player, enemy));
    }

    /**
     * Fa agire il nemico finche' e' il suo turno. Dentro al turno l'IA continua a
     * decidere finche' le restano Punti Azione da spendere, esattamente come puo'
     * fare il giocatore: ogni abilita' ne costa almeno uno, quindi il ciclo termina.
     */
    private void runEnemyTurnsIfNeeded() {
        while (!engine.isOver() && engine.currentActor() == enemy) {
            Optional<EnemyAction> action =
                    enemyStrategy.decide(enemy, player, engine.currentActionPoints());
            if (action.isPresent()) {
                engine.useAbility(action.get().ability(), action.get().target());
                continue;
            }
            engine.endTurn();
        }
    }

    private void requirePlayerTurn() {
        if (engine.currentActor() != player) {
            throw new IllegalStateException("Non e' il turno del giocatore");
        }
    }
}
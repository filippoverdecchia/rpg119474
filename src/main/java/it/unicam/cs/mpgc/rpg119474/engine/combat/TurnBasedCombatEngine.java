package it.unicam.cs.mpgc.rpg119474.engine.combat;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.ability.EffectResult;
import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.core.item.Consumable;
import it.unicam.cs.mpgc.rpg119474.engine.event.EventPublisher;
import it.unicam.cs.mpgc.rpg119474.engine.event.Observer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Motore di combattimento a turni 1v1 basato sui Punti Azione.
 * <p>
 * Ogni combattente, nel proprio turno, ha un numero di Punti Azione (derivato
 * dall'Agilita') e puo' usare piu' abilita' finche' gli bastano. Il motore non
 * conosce la UI: comunica solo pubblicando {@link CombatEvent} agli osservatori.
 * La sorgente di casualita' e' iniettata, cosi' il combattimento e' testabile.
 */
public class TurnBasedCombatEngine implements CombatEngine {

    private final GameCharacter player;
    private final GameCharacter enemy;
    private final RandomSource rng;
    private final EventPublisher<CombatEvent> events = new EventPublisher<>();

    private TurnOrder turnOrder;
    private GameCharacter currentActor;
    private int currentActionPoints;
    private boolean over = false;
    private GameCharacter winner;

    public TurnBasedCombatEngine(GameCharacter player, GameCharacter enemy, RandomSource rng) {
        this.player = Objects.requireNonNull(player, "player");
        this.enemy = Objects.requireNonNull(enemy, "enemy");
        this.rng = Objects.requireNonNull(rng, "rng");
    }

    @Override
    public void start() {
        turnOrder = new TurnOrder(List.of(player, enemy));
        currentActor = turnOrder.current();
        currentActionPoints = currentActor.derived().maxActionPoints();
        events.publish(new CombatEvent.CombatStarted(player, enemy));
        events.publish(new CombatEvent.TurnStarted(currentActor, currentActionPoints));
    }

    @Override
    public GameCharacter currentActor() {
        return currentActor;
    }

    @Override
    public int currentActionPoints() {
        return currentActionPoints;
    }

    @Override
    public boolean isOver() {
        return over;
    }

    @Override
    public Optional<GameCharacter> winner() {
        return Optional.ofNullable(winner);
    }

    @Override
    public void useAbility(Ability ability, GameCharacter target) {
        Objects.requireNonNull(ability, "ability");
        Objects.requireNonNull(target, "target");
        requireNotOver();
        if (ability.actionPointCost() > currentActionPoints) {
            throw new IllegalStateException("Punti Azione insufficienti per " + ability.name());
        }
        currentActionPoints -= ability.actionPointCost();

        EffectResult result = ability.applyTo(currentActor, target, rng);
        events.publish(new CombatEvent.AbilityUsed(currentActor, target, ability, result));

        if (!target.isAlive()) {
            events.publish(new CombatEvent.CharacterDefeated(target));
            over = true;
            winner = currentActor;
            events.publish(new CombatEvent.CombatEnded(winner));
        }
    }

    @Override
    public void useConsumable(Consumable item) {
        Objects.requireNonNull(item, "item");
        requireNotOver();
        if (CONSUMABLE_ACTION_POINT_COST > currentActionPoints) {
            throw new IllegalStateException("Punti Azione insufficienti per usare " + item.name());
        }
        currentActionPoints -= CONSUMABLE_ACTION_POINT_COST;

        int healthBefore = currentActor.currentHealth();
        currentActor.heal(item.healthRestored());
        int restored = currentActor.currentHealth() - healthBefore;
        events.publish(new CombatEvent.ItemUsed(currentActor, item, restored));
    }

    @Override
    public void endTurn() {
        requireNotOver();
        currentActor = turnOrder.next();
        currentActionPoints = currentActor.derived().maxActionPoints();
        events.publish(new CombatEvent.TurnStarted(currentActor, currentActionPoints));
    }

    @Override
    public void subscribe(Observer<CombatEvent> observer) {
        events.subscribe(observer);
    }

    private void requireNotOver() {
        if (over) {
            throw new IllegalStateException("Il combattimento e' gia' terminato");
        }
    }
}
package it.unicam.cs.mpgc.rpg119474.engine.combat;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.ability.EffectResult;
import it.unicam.cs.mpgc.rpg119474.core.attribute.Attributes;
import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.core.item.Consumable;
import it.unicam.cs.mpgc.rpg119474.core.item.Rarity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TurnBasedCombatEngineTest {

    /** Colpo deterministico: toglie una quantita' fissa di punti vita. */
    private static Ability blow(String name, int cost, int damage) {
        return new Ability(name, cost, (actor, target, rng) -> {
            target.takeDamage(damage);
            return new EffectResult(-damage, name, false);
        });
    }

    private static Enemy fighter(String name, int agilita) {
        return new Enemy(name, new Attributes(5, 5, 5, agilita, 5), 5, List.of(), 10);
    }

    private Enemy fast;
    private Enemy slow;
    private CombatEngine engine;
    private List<CombatEvent> events;

    @BeforeEach
    void setUp() {
        fast = fighter("Veloce", 8);   // Punti Azione: 6 + 8/2 = 10
        slow = fighter("Lento", 2);    // Punti Azione: 6 + 2/2 = 7
        engine = new TurnBasedCombatEngine(fast, slow, RandomSource.seeded(1));
        events = new ArrayList<>();
        engine.subscribe(events::add);
    }

    @Test
    void startOpensTheCombatAndTheFirstTurn() {
        engine.start();

        assertSame(fast, engine.currentActor(), "inizia chi ha piu' iniziativa");
        assertEquals(10, engine.currentActionPoints());
        assertFalse(engine.isOver());
        assertTrue(events.get(0) instanceof CombatEvent.CombatStarted);
        assertTrue(events.get(1) instanceof CombatEvent.TurnStarted);
    }

    @Test
    void usingAnAbilitySpendsActionPoints() {
        engine.start();

        engine.useAbility(blow("Colpo", 3, 5), slow);

        assertEquals(7, engine.currentActionPoints());
        assertEquals(slow.maxHealth() - 5, slow.currentHealth());
    }

    @Test
    void anAbilityTooExpensiveIsRejected() {
        engine.start();

        assertThrows(IllegalStateException.class,
                () -> engine.useAbility(blow("Devastante", 99, 5), slow));
        assertEquals(10, engine.currentActionPoints(), "un'azione rifiutata non consuma nulla");
    }

    @Test
    void endTurnPassesTheTurnAndRefillsActionPoints() {
        engine.start();
        engine.useAbility(blow("Colpo", 3, 5), slow);

        engine.endTurn();

        assertSame(slow, engine.currentActor());
        assertEquals(7, engine.currentActionPoints(), "ogni turno riparte con i Punti Azione pieni");
    }

    @Test
    void aConsumableHealsAndCostsActionPoints() {
        engine.start();
        fast.takeDamage(40);
        int healthBefore = fast.currentHealth();

        engine.useConsumable(new Consumable("Medikit", Rarity.COMUNE, "", 30));

        assertEquals(healthBefore + 30, fast.currentHealth());
        assertEquals(10 - CombatEngine.CONSUMABLE_ACTION_POINT_COST, engine.currentActionPoints());
        assertTrue(events.stream().anyMatch(CombatEvent.ItemUsed.class::isInstance));
    }

    @Test
    void healingNeverExceedsTheMaximum() {
        engine.start();
        fast.takeDamage(5);

        engine.useConsumable(new Consumable("Medikit", Rarity.COMUNE, "", 100));

        assertEquals(fast.maxHealth(), fast.currentHealth());
        CombatEvent.ItemUsed used = events.stream()
                .filter(CombatEvent.ItemUsed.class::isInstance)
                .map(CombatEvent.ItemUsed.class::cast)
                .findFirst().orElseThrow();
        assertEquals(5, used.healthRestored(), "l'evento riporta la cura effettiva, non quella teorica");
    }

    @Test
    void bringingTheTargetToZeroEndsTheCombat() {
        engine.start();

        engine.useAbility(blow("Colpo fatale", 0, 1000), slow);

        assertTrue(engine.isOver());
        assertEquals(fast, engine.winner().orElseThrow());
        assertTrue(events.stream().anyMatch(CombatEvent.CharacterDefeated.class::isInstance));
        assertTrue(events.stream().anyMatch(CombatEvent.CombatEnded.class::isInstance));
    }

    @Test
    void noActionIsAllowedAfterTheEnd() {
        engine.start();
        engine.useAbility(blow("Colpo fatale", 0, 1000), slow);

        assertThrows(IllegalStateException.class, () -> engine.useAbility(blow("Ancora", 0, 1), slow));
        assertThrows(IllegalStateException.class, engine::endTurn);
    }

    @Test
    void theWinnerIsUnknownUntilTheEnd() {
        engine.start();
        GameCharacter actor = engine.currentActor();

        assertTrue(engine.winner().isEmpty());
        assertSame(fast, actor);
    }
}
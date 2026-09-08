package it.unicam.cs.mpgc.rpg119474.engine.campaign;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.engine.GameService;
import it.unicam.cs.mpgc.rpg119474.engine.ai.EnemyStrategies;
import it.unicam.cs.mpgc.rpg119474.engine.combat.CombatEvent;
import it.unicam.cs.mpgc.rpg119474.engine.factory.CharacterFactory;
import it.unicam.cs.mpgc.rpg119474.engine.factory.ItemFactory;
import it.unicam.cs.mpgc.rpg119474.engine.loot.DefaultLootService;
import it.unicam.cs.mpgc.rpg119474.engine.progression.DefaultProgressionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica la facciata della campagna: prepara e avvia una tappa, ne registra
 * l'esito e tiene il conto del percorso. La casualita' e' seminata, quindi ogni
 * esecuzione ripercorre gli stessi scontri.
 */
class CampaignSessionTest {

    /** Quota di punti vita recuperata a ogni vittoria, come nella partita vera. */
    private static final int RECOVERY_PERCENT = 30;

    private CampaignSession session;
    private PlayerCharacter survivor;

    private static CampaignSession newSession(long seed) {
        RandomSource rng = RandomSource.seeded(seed);
        return new CampaignSession(Campaigns.wasteland(), new SuppliesLostPenalty(),
                new DefaultProgressionService(new DefaultLootService(rng), RECOVERY_PERCENT),
                EnemyStrategies.aggressive(), rng);
    }

    @BeforeEach
    void setUp() {
        session = newSession(3);
        survivor = CharacterFactory.createSurvivor("Eroe", SurvivorClass.BRUTO);
    }

    /** Combatte finche' lo scontro non si chiude, usando il colpo piu' forte disponibile. */
    private void fightToTheEnd() {
        GameService game = session.game();
        int guard = 0;
        while (!game.isOver() && guard++ < 300) {
            if (game.currentActor() != survivor) {
                break;
            }
            boolean acted = false;
            while (!game.isOver()) {
                Optional<Ability> chosen = survivor.abilities().stream()
                        .filter(ability -> ability.actionPointCost() <= game.currentActionPoints())
                        .max(Comparator.comparingInt(Ability::actionPointCost));
                if (chosen.isEmpty()) {
                    break;
                }
                game.playerUseAbility(chosen.get(), session.enemy());
                acted = true;
            }
            if (!game.isOver()) {
                game.playerEndTurn();
            }
            if (!acted) {
                break;
            }
        }
    }

    /** Si lascia sconfiggere: scende a un punto vita e cede il turno all'avversario. */
    private void loseOnPurpose(PlayerCharacter player) {
        GameService game = session.game();
        int guard = 0;
        while (!game.isOver() && guard++ < 50 && game.currentActor() == player) {
            player.takeDamage(player.currentHealth() - 1);
            game.playerEndTurn();
        }
    }

    @Test
    void nothingCanBeDoneBeforeAGameIsStarted() {
        CampaignSession untouched = newSession(1);

        assertThrows(IllegalStateException.class, untouched::currentStage);
        assertThrows(IllegalStateException.class, untouched::toSave);
    }

    @Test
    void aNewGameStartsFromTheFirstStage() {
        session.begin(survivor);

        assertEquals(1, session.currentStage().number());
        assertFalse(session.isCampaignComplete());
        assertSame(survivor, session.player());
        assertNull(session.enemy(), "l'avversario nasce solo preparando la tappa");
    }

    @Test
    void aStageMustBePreparedBeforeBeingStarted() {
        session.begin(survivor);

        assertThrows(IllegalStateException.class, () -> session.startStage(event -> { }));
    }

    @Test
    void theObserverReceivesTheCombatFromTheVeryBeginning() {
        session.begin(survivor);
        session.prepareStage();
        List<CombatEvent> events = new ArrayList<>();

        session.startStage(events::add);

        assertTrue(events.get(0) instanceof CombatEvent.CombatStarted,
                "preparare e avviare sono separati proprio per non perdere i primi eventi");
    }

    @Test
    void theStageEnemyIsTheOneOfTheCurrentStage() {
        session.begin(survivor);

        Enemy enemy = session.prepareStage();

        assertEquals("Sciacallo", enemy.name());
        assertSame(enemy, session.enemy());
    }

    @Test
    void aStageCannotBeClosedWhileTheFightIsOn() {
        session.begin(survivor);
        session.prepareStage();
        session.startStage(event -> { });

        assertThrows(IllegalStateException.class, session::endStage);
        assertTrue(session.reward().isEmpty());
    }

    @Test
    void winningAStageAwardsTheRewardAndMovesOn() {
        session.begin(survivor);
        session.prepareStage();
        session.startStage(event -> { });
        fightToTheEnd();

        StageOutcome outcome = session.endStage();

        assertTrue(outcome.cleared());
        assertEquals(1, outcome.stageNumber());
        assertTrue(session.reward().isPresent());
        assertEquals(40, survivor.experience(), "lo Sciacallo vale 40 punti esperienza");
        assertEquals(2, session.currentStage().number());
    }

    @Test
    void closingAStageTwiceChangesNothing() {
        session.begin(survivor);
        session.prepareStage();
        session.startStage(event -> { });
        fightToTheEnd();

        StageOutcome first = session.endStage();
        StageOutcome second = session.endStage();

        assertSame(first, second);
        assertEquals(40, survivor.experience(), "la ricompensa non si incassa due volte");
    }

    @Test
    void losingAStageCostsTheSuppliesAndLeavesThePlayerWhereHeWas() {
        PlayerCharacter unlucky = CharacterFactory.createSurvivor("Sfortunato", SurvivorClass.BRUTO);
        session.begin(unlucky);
        unlucky.inventory().add(ItemFactory.medikit());
        session.prepareStage();
        session.startStage(event -> { });
        loseOnPurpose(unlucky);

        StageOutcome outcome = session.endStage();

        assertFalse(outcome.cleared());
        assertTrue(outcome.hasLosses());
        assertTrue(unlucky.inventory().isEmpty());
        assertTrue(unlucky.isAlive(), "deve poter ritentare la tappa");
        assertTrue(session.reward().isEmpty());
        assertEquals(1, session.currentStage().number());
    }

    @Test
    void aSavedGameIsResumedWhereItWasLeft() {
        session.begin(survivor);
        session.prepareStage();
        session.startStage(event -> { });
        fightToTheEnd();
        session.endStage();

        CampaignSession resumed = newSession(3);
        resumed.resume(session.toSave());

        assertEquals(2, resumed.currentStage().number());
        assertEquals(survivor.experience(), resumed.player().experience());
        assertEquals(survivor.name(), resumed.player().name());
    }
}
package it.unicam.cs.mpgc.rpg119474.engine;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.item.Consumable;
import it.unicam.cs.mpgc.rpg119474.engine.ai.EnemyStrategy;
import it.unicam.cs.mpgc.rpg119474.engine.combat.CombatEvent;
import it.unicam.cs.mpgc.rpg119474.engine.event.Observer;
import it.unicam.cs.mpgc.rpg119474.engine.progression.VictoryOutcome;

import java.util.Optional;

/**
 * API di alto livello del gioco, pensata per essere usata dalla UI (facade).
 * Nasconde i dettagli del motore e dell'IA: il chiamante avvia un duello, fa
 * agire il giocatore, e il servizio guida automaticamente i turni del nemico.
 */
public interface GameService {

    /** Avvia un duello tra giocatore e nemico; l'osservatore ricevera' gli eventi. */
    void startDuel(PlayerCharacter player, Enemy enemy, EnemyStrategy enemyStrategy, Observer<CombatEvent> observer);

    GameCharacter currentActor();

    int currentActionPoints();

    boolean isOver();

    Optional<GameCharacter> winner();

    /** Il giocatore usa un'abilita' su un bersaglio. */
    void playerUseAbility(Ability ability, GameCharacter target);

    /**
     * Il giocatore usa un consumabile del proprio inventario: spende Punti Azione,
     * recupera salute e l'oggetto viene consumato.
     */
    void playerUseConsumable(Consumable item);

    /** Il giocatore termina il turno; il servizio fa quindi agire il nemico. */
    void playerEndTurn();

    /**
     * Assegna al giocatore la ricompensa del duello, se lo ha vinto.
     * Applicata una sola volta per duello: le chiamate successive non hanno effetto.
     *
     * @return l'esito della vittoria, oppure {@link Optional#empty()} se il duello
     *         non e' finito, e' stato perso, o la ricompensa e' gia' stata assegnata
     */
    Optional<VictoryOutcome> resolveVictory();
}
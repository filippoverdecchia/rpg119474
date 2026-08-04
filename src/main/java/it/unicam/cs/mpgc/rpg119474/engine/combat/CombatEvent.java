package it.unicam.cs.mpgc.rpg119474.engine.combat;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.ability.EffectResult;
import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;
import it.unicam.cs.mpgc.rpg119474.core.item.Consumable;

/**
 * Eventi che possono accadere durante un combattimento.
 * <p>
 * Gerarchia <em>sealed</em> con i sottotipi (record) dichiarati qui dentro:
 * l'insieme degli eventi e' chiuso e noto, quindi chi li consuma (il log, la
 * GUI) puo' usare uno {@code switch} esaustivo con pattern matching, senza ramo
 * di default e con il controllo del compilatore.
 */
public sealed interface CombatEvent {

    /** Inizio del combattimento tra il giocatore e il nemico. */
    record CombatStarted(GameCharacter player, GameCharacter enemy) implements CombatEvent {}

    /** Inizio del turno di un combattente, con i Punti Azione a disposizione. */
    record TurnStarted(GameCharacter actor, int actionPoints) implements CombatEvent {}

    /** Un'abilita' e' stata usata su un bersaglio, con il relativo esito. */
    record AbilityUsed(GameCharacter actor, GameCharacter target, Ability ability, EffectResult result) implements CombatEvent {}

    /** Un combattente ha usato un oggetto consumabile, recuperando punti vita. */
    record ItemUsed(GameCharacter actor, Consumable item, int healthRestored) implements CombatEvent {}

    /** Un combattente e' stato sconfitto (punti vita a zero). */
    record CharacterDefeated(GameCharacter character) implements CombatEvent {}

    /** Fine del combattimento, con il vincitore. */
    record CombatEnded(GameCharacter winner) implements CombatEvent {}
}
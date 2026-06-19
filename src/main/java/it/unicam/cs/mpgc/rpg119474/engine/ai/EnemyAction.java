package it.unicam.cs.mpgc.rpg119474.engine.ai;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;

import java.util.Objects;

/** Azione scelta dall'IA: quale abilita' usare e su quale bersaglio. */
public record EnemyAction(Ability ability, GameCharacter target) {

    public EnemyAction {
        Objects.requireNonNull(ability, "ability");
        Objects.requireNonNull(target, "target");
    }
}
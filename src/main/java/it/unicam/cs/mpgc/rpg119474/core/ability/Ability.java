package it.unicam.cs.mpgc.rpg119474.core.ability;

import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;

import java.util.Objects;

/**
 * Abilita' usabile in combattimento: nome, costo in Punti Azione e l'effetto
 * associato. La composizione con l'effetto (anziche' una gerarchia di sottoclassi)
 * mantiene la classe semplice ed estendibile.
 */
public record Ability(String name, int actionPointCost, AbilityEffect effect) {

    public Ability {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(effect, "effect");
        if (actionPointCost < 0) {
            throw new IllegalArgumentException("actionPointCost deve essere >= 0");
        }
    }

    public EffectResult applyTo(GameCharacter source, GameCharacter target, RandomSource rng) {
        return effect.apply(source, target, rng);
    }
}

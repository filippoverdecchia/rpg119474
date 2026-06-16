package it.unicam.cs.mpgc.rpg119474.core.ability;

import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;

/**
 * Effetto di un'abilita': interfaccia funzionale, cosi' ogni effetto puo' essere
 * scritto come lambda e nuovi effetti si aggiungono senza toccare il resto.
 */
@FunctionalInterface
public interface AbilityEffect {

    EffectResult apply(GameCharacter source, GameCharacter target, RandomSource rng);
}

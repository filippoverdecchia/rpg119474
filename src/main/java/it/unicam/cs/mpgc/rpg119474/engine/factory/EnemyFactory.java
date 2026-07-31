package it.unicam.cs.mpgc.rpg119474.engine.factory;

import it.unicam.cs.mpgc.rpg119474.core.ability.Abilities;
import it.unicam.cs.mpgc.rpg119474.core.attribute.Attributes;
import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import java.util.function.Supplier;
import java.util.List;

/** Crea i nemici predefiniti del gioco (Factory pattern). */
public final class EnemyFactory {

    private EnemyFactory() {
        // Classe di utilita': non istanziabile.
    }

    public static Enemy raider() {
        return new Enemy("Predone", new Attributes(5, 5, 4, 5, 3), 6,
                List.of(Abilities.basicMelee(), Abilities.aimedShot()), 60);
    }

    public static Enemy mutantDog() {
        return new Enemy("Cane mutante", new Attributes(6, 4, 3, 8, 2), 5,
                List.of(Abilities.basicMelee()), 50);
    }

    public static Enemy ghoul() {
        return new Enemy("Ghoul inferocito", new Attributes(7, 3, 6, 4, 2), 8,
                List.of(Abilities.basicMelee(), Abilities.heavyBlow()), 90);
    }

    public static Enemy raiderBoss() {
        return new Enemy("Capo dei predoni", new Attributes(7, 7, 6, 6, 4), 9,
                List.of(Abilities.aimedShot(), Abilities.burstFire(), Abilities.heavyBlow()), 150);
    }

    private static final List<Supplier<Enemy>> CATALOG =
            List.of(EnemyFactory::raider, EnemyFactory::mutantDog,
                    EnemyFactory::ghoul, EnemyFactory::raiderBoss);

    public static Enemy random(RandomSource rng) {
        return CATALOG.get(rng.nextInt(CATALOG.size())).get();
    }
}
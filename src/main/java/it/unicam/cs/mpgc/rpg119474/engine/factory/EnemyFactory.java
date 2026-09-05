package it.unicam.cs.mpgc.rpg119474.engine.factory;

import it.unicam.cs.mpgc.rpg119474.core.ability.Abilities;
import it.unicam.cs.mpgc.rpg119474.core.attribute.Attributes;
import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;

import java.util.List;
import java.util.function.Supplier;

/** Crea i nemici predefiniti del gioco (Factory pattern). */
public final class EnemyFactory {

    private EnemyFactory() {
        // Classe di utilita': non istanziabile.
    }

    public static Enemy scavenger() {
        return new Enemy("Sciacallo", new Attributes(4, 4, 3, 6, 3), 4,
                List.of(Abilities.basicMelee()), 40);
    }

    public static Enemy mutantDog() {
        return new Enemy("Cane mutante", new Attributes(6, 4, 3, 8, 2), 5,
                List.of(Abilities.basicMelee()), 50);
    }

    public static Enemy raider() {
        return new Enemy("Predone", new Attributes(5, 5, 4, 5, 3), 6,
                List.of(Abilities.basicMelee(), Abilities.aimedShot()), 60);
    }

    public static Enemy militiaman() {
        return new Enemy("Miliziano", new Attributes(5, 7, 5, 5, 4), 7,
                List.of(Abilities.aimedShot(), Abilities.burstFire()), 75);
    }

    public static Enemy ghoul() {
        return new Enemy("Ghoul inferocito", new Attributes(7, 3, 6, 4, 2), 8,
                List.of(Abilities.basicMelee(), Abilities.heavyBlow()), 90);
    }

    public static Enemy mutatedBrute() {
        return new Enemy("Bruto mutato", new Attributes(9, 3, 7, 3, 2), 10,
                List.of(Abilities.basicMelee(), Abilities.heavyBlow()), 110);
    }

    public static Enemy raiderBoss() {
        return new Enemy("Capo dei predoni", new Attributes(7, 7, 6, 6, 4), 9,
                List.of(Abilities.aimedShot(), Abilities.burstFire(), Abilities.heavyBlow()), 150);
    }

    /** Avversario finale della campagna: colpisce forte e con danni di tipo diverso. */
    public static Enemy wasteWarlord() {
        return new Enemy("Signore della Zona", new Attributes(8, 8, 8, 7, 5), 11,
                List.of(Abilities.heavyBlow(), Abilities.burstFire(), Abilities.overcharge()), 250);
    }

    /** Tutti i nemici disponibili, come costruttori: un nuovo nemico si aggiunge qui. */
    private static final List<Supplier<Enemy>> CATALOG =
            List.of(EnemyFactory::scavenger, EnemyFactory::raider, EnemyFactory::mutantDog,
                    EnemyFactory::militiaman, EnemyFactory::ghoul, EnemyFactory::mutatedBrute,
                    EnemyFactory::raiderBoss);

    /** Crea un nemico scelto a caso dal catalogo. */
    public static Enemy random(RandomSource rng) {
        return CATALOG.get(rng.nextInt(CATALOG.size())).get();
    }
}
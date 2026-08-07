package it.unicam.cs.mpgc.rpg119474.core.ability;

import it.unicam.cs.mpgc.rpg119474.core.attribute.Attributes;
import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;
import it.unicam.cs.mpgc.rpg119474.core.dice.Dice;
import it.unicam.cs.mpgc.rpg119474.core.stats.DamageType;

import java.util.function.ToIntFunction;

/**
 * Factory delle abilita' predefinite (metodi statici factory).
 * Le abilita' d'attacco condividono lo stesso schema tramite {@link #attack},
 * differenziandosi per attributo di riferimento, tipo di danno e dado bonus.
 */
public final class Abilities {

    private Abilities() {
        // Classe di utilita': non istanziabile.
    }

    // --- Mischia (Bruto) ---
    public static Ability basicMelee() {
        return attack("Colpo in mischia", 2, DamageType.MISCHIA, Attributes::forza, Dice.of(1, 6));
    }

    public static Ability heavyBlow() {
        return attack("Colpo Pesante", 3, DamageType.MISCHIA, Attributes::forza, Dice.of(2, 6));
    }

    // --- Armi da fuoco (Cecchino) ---
    public static Ability aimedShot() {
        return attack("Colpo Mirato", 2, DamageType.BALISTICO, Attributes::percezione, Dice.of(1, 8));
    }

    public static Ability burstFire() {
        return new Ability("Raffica", 3, (actor, target, rng) -> {
            int total = 0;
            boolean anyCrit = false;
            for (int shot = 0; shot < 3; shot++) {
                int raw = actor.attributes().percezione() / 2 + actor.weaponDamage() / 2
                        + Dice.of(1, 4).roll(rng).total();
                boolean crit = rng.nextInt(100) < actor.derived().critChance();
                if (crit) {
                    raw = (int) Math.round(raw * 1.5);
                    anyCrit = true;
                }
                int dmg = DamageCalculator.afterDefense(raw, target.derived().defense(), DamageType.BALISTICO);
                target.takeDamage(dmg);
                total += dmg;
            }
            String message = actor.name() + " scarica una raffica su " + target.name()
                    + ": " + total + " danni" + (anyCrit ? " (con critico!)" : "") + ".";
            return new EffectResult(-total, message, anyCrit);
        });
    }

    // --- Energia (Tecnico) ---
    public static Ability plasmaBlast() {
        return attack("Scarica al Plasma", 3, DamageType.ENERGIA, Attributes::percezione, Dice.of(2, 6));
    }

    public static Ability overcharge() {
        return attack("Sovraccarico", 4, DamageType.RADIAZIONI, Attributes::percezione, Dice.of(2, 8));
    }

    // --- Supporto (Medico) ---
    public static Ability toxicGrenade() {
        return attack("Granata Tossica", 3, DamageType.VELENO, Attributes::percezione, Dice.of(1, 6));
    }

    public static Ability medkit() {
        return new Ability("Medikit", 2, (actor, target, rng) -> {
            int amount = 20 + actor.attributes().fortuna() + Dice.of(1, 6).roll(rng).total();
            actor.heal(amount);
            return new EffectResult(amount, actor.name() + " usa un medikit e recupera " + amount + " PV.", false);
        });
    }

    /** Schema comune delle abilita' d'attacco: attributo + danno arma + dado, con possibile critico. */
    private static Ability attack(String name, int actionPointCost, DamageType type,
                                  ToIntFunction<Attributes> scaling, Dice bonus) {
        return new Ability(name, actionPointCost, (actor, target, rng) -> {
            int raw = scaling.applyAsInt(actor.attributes()) + actor.weaponDamage() + bonus.roll(rng).total();
            boolean crit = rng.nextInt(100) < actor.derived().critChance();
            if (crit) {
                raw = (int) Math.round(raw * 1.5);
            }
            int dmg = DamageCalculator.afterDefense(raw, target.derived().defense(), type);
            target.takeDamage(dmg);
            return new EffectResult(-dmg, narrate(actor, target, name, dmg, crit), crit);
        });
    }

    private static String narrate(GameCharacter actor, GameCharacter target, String ability, int dmg, boolean crit) {
        return actor.name() + " usa " + ability + " su " + target.name()
                + ": " + dmg + " danni" + (crit ? " (CRITICO!)" : "") + ".";
    }
}

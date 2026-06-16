package it.unicam.cs.mpgc.rpg119474;

import it.unicam.cs.mpgc.rpg119474.core.ability.Abilities;
import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.ability.EffectResult;
import it.unicam.cs.mpgc.rpg119474.core.attribute.AttributeModifier;
import it.unicam.cs.mpgc.rpg119474.core.attribute.Attributes;
import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.core.item.Rarity;
import it.unicam.cs.mpgc.rpg119474.core.item.Weapon;
import it.unicam.cs.mpgc.rpg119474.core.stats.DamageType;

import java.util.List;

/**
 * Punto di ingresso dell'applicazione.
 * <p>
 * In questa fase mostra una breve dimostrazione del modulo {@code core}: crea un
 * sopravvissuto, ne stampa la scheda e applica un'abilita' su un nemico. Verra'
 * sostituita dal motore di combattimento (Parte 2) e poi dalla GUI JavaFX, senza
 * toccare il dominio.
 */
public final class App {

    private App() {
        // Classe di sola attivazione: non deve essere istanziata.
    }

    public static void main(String[] args) {
        RandomSource rng = RandomSource.seeded(7);

        PlayerCharacter hero = new PlayerCharacter("Vagabondo", SurvivorClass.CECCHINO);
        hero.equip(new Weapon("Fucile arrugginito", Rarity.COMUNE, "Recuperato in una stazione di servizio",
                DamageType.BALISTICO, 6, new AttributeModifier(0, 2, 0, 0, 0)));

        printSheet(hero);

        Enemy raider = new Enemy("Predone", new Attributes(5, 4, 4, 5, 2), 6,
                List.of(Abilities.basicMelee()), 60);

        System.out.println();
        System.out.println("== Scontro dimostrativo ==");
        Ability aimedShot = hero.abilities().get(0);
        EffectResult result = aimedShot.applyTo(hero, raider, rng);
        System.out.println(result.message()
                + " (Predone PV " + raider.currentHealth() + "/" + raider.maxHealth() + ")");
    }

    private static void printSheet(PlayerCharacter hero) {
        Attributes a = hero.attributes();
        System.out.println("Sopravvissuto: " + hero.name()
                + " [" + hero.survivorClass().displayName() + "] liv." + hero.level());
        System.out.println("Attributi   Forza " + a.forza() + " | Percezione " + a.percezione()
                + " | Resistenza " + a.resistenza() + " | Agilita' " + a.agilita() + " | Fortuna " + a.fortuna());
        System.out.println("Derivate    PV " + hero.maxHealth() + " | Difesa " + hero.derived().defense()
                + " | Iniziativa " + hero.derived().initiative() + " | PA " + hero.derived().maxActionPoints()
                + " | Critico " + hero.derived().critChance() + "%");
        System.out.println("Abilita'    " + hero.abilities().stream().map(Ability::name).toList());
    }
}

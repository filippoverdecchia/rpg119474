package it.unicam.cs.mpgc.rpg119474.core.character;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.attribute.Attributes;

import java.util.List;

/** Avversario controllato dall'IA: ha un danno d'arma naturale e fornisce XP se sconfitto. */
public class Enemy extends AbstractCharacter {

    private final int naturalWeaponDamage;
    private final int experienceReward;

    public Enemy(String name, Attributes attributes, int naturalWeaponDamage,
                 List<Ability> abilities, int experienceReward) {
        super(name, attributes, abilities);
        if (naturalWeaponDamage < 0) {
            throw new IllegalArgumentException("naturalWeaponDamage deve essere >= 0");
        }
        if (experienceReward < 0) {
            throw new IllegalArgumentException("experienceReward deve essere >= 0");
        }
        this.naturalWeaponDamage = naturalWeaponDamage;
        this.experienceReward = experienceReward;
    }

    @Override
    public int weaponDamage() {
        return naturalWeaponDamage;
    }

    public int experienceReward() {
        return experienceReward;
    }
}

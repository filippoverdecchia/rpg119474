package it.unicam.cs.mpgc.rpg119474.core.character;

import it.unicam.cs.mpgc.rpg119474.core.attribute.AttributeModifier;
import it.unicam.cs.mpgc.rpg119474.core.attribute.Attributes;
import it.unicam.cs.mpgc.rpg119474.core.item.Armor;
import it.unicam.cs.mpgc.rpg119474.core.item.Inventory;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.core.item.Weapon;

import java.util.Objects;
import java.util.Optional;

/**
 * Sopravvissuto controllato dal giocatore: gestisce classe, equipaggiamento,
 * inventario e progressione (livello ed esperienza). Gli attributi effettivi
 * combinano base di classe, crescita di livello e bonus dell'equipaggiamento.
 */
public class PlayerCharacter extends AbstractCharacter {

    private final SurvivorClass survivorClass;
    private final Inventory<Item> inventory = new Inventory<>();
    private final ExperienceCurve experienceCurve;

    private Weapon weapon;
    private Armor armor;
    private int level = 1;
    private int experience = 0;

    public PlayerCharacter(String name, SurvivorClass survivorClass) {
        this(name, survivorClass, ExperienceCurve.standard());
    }

    public PlayerCharacter(String name, SurvivorClass survivorClass, ExperienceCurve experienceCurve) {
        super(name, survivorClass.baseAttributes(), survivorClass.startingAbilities());
        this.survivorClass = Objects.requireNonNull(survivorClass, "survivorClass");
        this.experienceCurve = Objects.requireNonNull(experienceCurve, "experienceCurve");
    }

    @Override
    public Attributes attributes() {
        return withLevelGrowth(baseAttributes(), level).with(equipmentModifier());
    }

    @Override
    public int weaponDamage() {
        return weapon != null ? weapon.baseDamage() : 0;
    }

    private AttributeModifier equipmentModifier() {
        AttributeModifier modifier = AttributeModifier.NONE;
        if (weapon != null) {
            modifier = modifier.add(weapon.attributeBonus());
        }
        if (armor != null) {
            modifier = modifier.add(armor.attributeBonus());
        }
        return modifier;
    }

    private static Attributes withLevelGrowth(Attributes base, int level) {
        int growth = level - 1; // +1 a ogni attributo per livello oltre il primo
        return base.with(new AttributeModifier(growth, growth, growth, growth, growth));
    }

    public void equip(Weapon newWeapon) {
        this.weapon = Objects.requireNonNull(newWeapon, "newWeapon");
        clampHealth();
    }

    public void equip(Armor newArmor) {
        this.armor = Objects.requireNonNull(newArmor, "newArmor");
        clampHealth();
    }

    /** Aggiunge esperienza e applica eventuali passaggi di livello. */
    public void gainExperience(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount deve essere >= 0");
        }
        experience += amount;
        while (experience >= experienceCurve.experienceForLevel(level + 1)) {
            level++;
        }
    }

    public SurvivorClass survivorClass() {
        return survivorClass;
    }

    public Inventory<Item> inventory() {
        return inventory;
    }

    public int level() {
        return level;
    }

    public int experience() {
        return experience;
    }

    public Optional<Weapon> weapon() {
        return Optional.ofNullable(weapon);
    }

    public Optional<Armor> armor() {
        return Optional.ofNullable(armor);
    }
}

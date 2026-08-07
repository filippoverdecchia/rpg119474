package it.unicam.cs.mpgc.rpg119474.core.character;

import it.unicam.cs.mpgc.rpg119474.core.attribute.AttributeModifier;
import it.unicam.cs.mpgc.rpg119474.core.attribute.Attributes;
import it.unicam.cs.mpgc.rpg119474.core.item.Armor;
import it.unicam.cs.mpgc.rpg119474.core.item.Consumable;
import it.unicam.cs.mpgc.rpg119474.core.item.Inventory;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.core.item.Weapon;
import it.unicam.cs.mpgc.rpg119474.core.stats.DerivedStats;

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

    // Scheda del personaggio calcolata su richiesta e conservata finche' resta valida:
    // la UI e il calcolo del danno la interrogano molte volte per ogni azione, mentre
    // cambia solo con l'equipaggiamento o con un passaggio di livello.
    private Attributes cachedAttributes;
    private DerivedStats cachedDerived;

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
        if (cachedAttributes == null) {
            cachedAttributes = withLevelGrowth(baseAttributes(), level).with(equipmentModifier());
        }
        return cachedAttributes;
    }

    @Override
    public DerivedStats derived() {
        if (cachedDerived == null) {
            cachedDerived = DerivedStats.from(attributes());
        }
        return cachedDerived;
    }

    /** Da chiamare a ogni cambiamento che altera la scheda: equipaggiamento o livello. */
    private void invalidateSheet() {
        cachedAttributes = null;
        cachedDerived = null;
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
        invalidateSheet();
        clampHealth();
    }

    public void equip(Armor newArmor) {
        this.armor = Objects.requireNonNull(newArmor, "newArmor");
        invalidateSheet();
        clampHealth();
    }

    /**
     * Equipaggia un oggetto preso dall'inventario. L'oggetto esce dall'inventario e
     * quello eventualmente sostituito vi rientra, cosi' nulla va perduto.
     * <p>
     * Lo switch sulla gerarchia sealed {@link Item} e' esaustivo: se in futuro
     * comparisse un nuovo tipo di oggetto, il compilatore obbligherebbe a decidere
     * qui come trattarlo.
     *
     * @return {@code true} se l'oggetto era nell'inventario ed e' equipaggiabile
     */
    public boolean equipFromInventory(Item item) {
        Objects.requireNonNull(item, "item");
        if (!inventory.asList().contains(item)) {
            return false;
        }
        return switch (item) {
            case Weapon newWeapon -> {
                inventory.remove(item);
                weapon().ifPresent(inventory::add);
                equip(newWeapon);
                yield true;
            }
            case Armor newArmor -> {
                inventory.remove(item);
                armor().ifPresent(inventory::add);
                equip(newArmor);
                yield true;
            }
            case Consumable ignored -> false; // si consuma, non si indossa
        };
    }

    /** Aggiunge esperienza e applica eventuali passaggi di livello. */
    public void gainExperience(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount deve essere >= 0");
        }
        experience += amount;
        int levelBefore = level;
        while (experience >= experienceCurve.experienceForLevel(level + 1)) {
            level++;
        }
        if (level != levelBefore) {
            invalidateSheet();
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

    /** Crea la fotografia salvabile dello stato attuale del personaggio. */
    public CharacterSnapshot toSnapshot() {
        return new CharacterSnapshot(name(), survivorClass, experience, currentHealth(),
                weapon, armor, inventory.asList());
    }

    /** Ricostruisce un personaggio a partire da una fotografia salvata. */
    public static PlayerCharacter fromSnapshot(CharacterSnapshot snapshot) {
        PlayerCharacter survivor = new PlayerCharacter(snapshot.name(), snapshot.survivorClass());
        if (snapshot.weapon() != null) {
            survivor.equip(snapshot.weapon());
        }
        if (snapshot.armor() != null) {
            survivor.equip(snapshot.armor());
        }
        snapshot.inventory().forEach(survivor.inventory()::add);
        survivor.gainExperience(snapshot.experience());
        survivor.setCurrentHealth(snapshot.currentHealth());
        return survivor;
    }
}
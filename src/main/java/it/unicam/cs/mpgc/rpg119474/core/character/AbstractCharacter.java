package it.unicam.cs.mpgc.rpg119474.core.character;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.attribute.Attributes;
import it.unicam.cs.mpgc.rpg119474.core.stats.DerivedStats;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementazione comune delle entita' di combattimento: nome, attributi base,
 * salute corrente e abilita'. Le sottoclassi specializzano gli attributi
 * effettivi ({@link #attributes()}) e il danno dell'arma ({@link #weaponDamage()}).
 */
public abstract class AbstractCharacter implements GameCharacter {

    private final String name;
    private final Attributes baseAttributes;
    private final List<Ability> abilities;
    private int currentHealth;

    protected AbstractCharacter(String name, Attributes baseAttributes, List<Ability> abilities) {
        this.name = Objects.requireNonNull(name, "name");
        this.baseAttributes = Objects.requireNonNull(baseAttributes, "baseAttributes");
        this.abilities = new ArrayList<>(Objects.requireNonNull(abilities, "abilities"));
        this.currentHealth = DerivedStats.from(baseAttributes).maxHealth();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Attributes attributes() {
        return baseAttributes;
    }

    protected Attributes baseAttributes() {
        return baseAttributes;
    }

    @Override
    public int currentHealth() {
        return currentHealth;
    }

    @Override
    public List<Ability> abilities() {
        return List.copyOf(abilities);
    }

    @Override
    public void takeDamage(int amount) {
        requireNonNegative(amount);
        currentHealth = Math.max(0, currentHealth - amount);
    }

    @Override
    public void heal(int amount) {
        requireNonNegative(amount);
        currentHealth = Math.min(maxHealth(), currentHealth + amount);
    }

    /** Mantiene la salute entro [0, maxHealth], es. dopo un cambio di equipaggiamento. */
    protected void clampHealth() {
        currentHealth = Math.min(currentHealth, maxHealth());
    }

    /** Imposta direttamente la salute (limitata a [0, maxHealth]); usato per ripristinare uno stato salvato. */
    protected void setCurrentHealth(int value) {
        currentHealth = Math.max(0, Math.min(maxHealth(), value));
    }

    private static void requireNonNegative(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount deve essere >= 0");
        }
    }
}

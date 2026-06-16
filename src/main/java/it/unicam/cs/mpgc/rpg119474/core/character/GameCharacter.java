package it.unicam.cs.mpgc.rpg119474.core.character;

import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.attribute.Attributes;
import it.unicam.cs.mpgc.rpg119474.core.stats.DerivedStats;

import java.util.List;

/**
 * Contratto comune di ogni entita' che combatte (sopravvissuto del giocatore o
 * nemico). La logica di gioco dipende da questa astrazione, non dalle classi
 * concrete.
 */
public interface GameCharacter {

    String name();

    /** Attributi effettivi (base, livello ed eventuale equipaggiamento). */
    Attributes attributes();

    /** Danno base dell'arma in uso (0 se disarmato). */
    int weaponDamage();

    int currentHealth();

    List<Ability> abilities();

    void takeDamage(int amount);

    void heal(int amount);

    /** Statistiche di combattimento, derivate dagli attributi effettivi. */
    default DerivedStats derived() {
        return DerivedStats.from(attributes());
    }

    default int maxHealth() {
        return derived().maxHealth();
    }

    default boolean isAlive() {
        return currentHealth() > 0;
    }
}

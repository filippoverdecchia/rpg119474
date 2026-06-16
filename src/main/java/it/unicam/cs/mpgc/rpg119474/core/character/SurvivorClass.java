package it.unicam.cs.mpgc.rpg119474.core.character;

import it.unicam.cs.mpgc.rpg119474.core.ability.Abilities;
import it.unicam.cs.mpgc.rpg119474.core.ability.Ability;
import it.unicam.cs.mpgc.rpg119474.core.attribute.Attributes;

import java.util.List;

/**
 * Le quattro classi giocabili. Ognuna ha attributi base diversi e un proprio
 * set di abilita' iniziali, che ne definiscono lo stile di combattimento.
 */
public enum SurvivorClass {

    BRUTO("Bruto", new Attributes(8, 3, 7, 4, 3)),
    CECCHINO("Cecchino", new Attributes(3, 8, 4, 7, 5)),
    TECNICO("Tecnico", new Attributes(3, 7, 5, 4, 6)),
    MEDICO("Medico", new Attributes(4, 5, 7, 4, 6));

    private final String displayName;
    private final Attributes baseAttributes;

    SurvivorClass(String displayName, Attributes baseAttributes) {
        this.displayName = displayName;
        this.baseAttributes = baseAttributes;
    }

    public String displayName() {
        return displayName;
    }

    public Attributes baseAttributes() {
        return baseAttributes;
    }

    /** Abilita' iniziali della classe (switch esaustivo sull'enum). */
    public List<Ability> startingAbilities() {
        return switch (this) {
            case BRUTO -> List.of(Abilities.basicMelee(), Abilities.heavyBlow());
            case CECCHINO -> List.of(Abilities.aimedShot(), Abilities.burstFire());
            case TECNICO -> List.of(Abilities.plasmaBlast(), Abilities.overcharge());
            case MEDICO -> List.of(Abilities.aimedShot(), Abilities.medkit(), Abilities.toxicGrenade());
        };
    }
}

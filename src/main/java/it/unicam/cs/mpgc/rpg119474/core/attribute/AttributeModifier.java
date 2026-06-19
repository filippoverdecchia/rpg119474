package it.unicam.cs.mpgc.rpg119474.core.attribute;

import java.io.Serializable;

/**
 * Variazione (delta) applicabile agli attributi, ad esempio il bonus fornito da
 * un pezzo di equipaggiamento. Ammette valori nulli o negativi perche'
 * rappresenta una differenza, non uno stato. Serializzabile per la persistenza.
 */
public record AttributeModifier(int forza, int percezione, int resistenza, int agilita, int fortuna)
        implements Serializable {

    public static final AttributeModifier NONE = new AttributeModifier(0, 0, 0, 0, 0);

    public AttributeModifier add(AttributeModifier other) {
        return new AttributeModifier(
                forza + other.forza,
                percezione + other.percezione,
                resistenza + other.resistenza,
                agilita + other.agilita,
                fortuna + other.fortuna);
    }
}
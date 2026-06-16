package it.unicam.cs.mpgc.rpg119474.core.attribute;

/**
 * I cinque attributi primari di un personaggio: sono i valori "base" da cui
 * vengono calcolate tutte le statistiche di combattimento (vedi DerivedStats).
 * Value object immutabile, quindi modellato come record.
 */
public record Attributes(int forza, int percezione, int resistenza, int agilita, int fortuna) {

    public Attributes {
        if (forza < 1 || percezione < 1 || resistenza < 1 || agilita < 1 || fortuna < 1) {
            throw new IllegalArgumentException("Ogni attributo deve essere >= 1");
        }
    }

    /** Applica una variazione (es. bonus di equipaggiamento), mantenendo ogni attributo >= 1. */
    public Attributes with(AttributeModifier modifier) {
        return new Attributes(
                Math.max(1, forza + modifier.forza()),
                Math.max(1, percezione + modifier.percezione()),
                Math.max(1, resistenza + modifier.resistenza()),
                Math.max(1, agilita + modifier.agilita()),
                Math.max(1, fortuna + modifier.fortuna()));
    }
}

package it.unicam.cs.mpgc.rpg119474.engine.factory;

import it.unicam.cs.mpgc.rpg119474.core.attribute.AttributeModifier;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.core.item.Armor;
import it.unicam.cs.mpgc.rpg119474.core.item.Consumable;
import it.unicam.cs.mpgc.rpg119474.core.item.Rarity;
import it.unicam.cs.mpgc.rpg119474.core.item.Weapon;
import it.unicam.cs.mpgc.rpg119474.core.stats.DamageType;

/** Crea gli oggetti predefiniti del gioco (Factory pattern). */
public final class ItemFactory {

    private ItemFactory() {
        // Classe di utilita': non istanziabile.
    }

    public static Weapon rustyPipe() {
        return new Weapon("Tubo arrugginito", Rarity.COMUNE, "Un tubo di metallo pesante.",
                DamageType.MISCHIA, 6, AttributeModifier.NONE);
    }

    public static Weapon machete() {
        return new Weapon("Machete", Rarity.COMUNE, "Affilato e fidato.",
                DamageType.MISCHIA, 5, AttributeModifier.NONE);
    }

    public static Weapon scavengedRifle() {
        return new Weapon("Fucile recuperato", Rarity.NON_COMUNE, "Tiene ancora botta.",
                DamageType.BALISTICO, 7, new AttributeModifier(0, 1, 0, 0, 0));
    }

    public static Weapon plasmaPistol() {
        return new Weapon("Pistola al plasma", Rarity.RARA, "Surriscalda l'aria intorno al bersaglio.",
                DamageType.ENERGIA, 8, AttributeModifier.NONE);
    }

    public static Armor leatherArmor() {
        return new Armor("Giubbotto di cuoio", Rarity.COMUNE, "Protezione leggera.",
                new AttributeModifier(0, 0, 1, 0, 0));
    }

    public static Armor combatArmor() {
        return new Armor("Armatura da combattimento", Rarity.RARA, "Piastre rinforzate.",
                new AttributeModifier(0, 0, 3, 0, 0));
    }

    public static Consumable medikit() {
        return new Consumable("Medikit", Rarity.COMUNE, "Ripristina punti vita.", 30);
    }

    /** Arma iniziale adatta alla classe scelta. */
    public static Weapon startingWeaponFor(SurvivorClass survivorClass) {
        return switch (survivorClass) {
            case BRUTO -> rustyPipe();
            case CECCHINO -> scavengedRifle();
            case TECNICO -> plasmaPistol();
            case MEDICO -> machete();
        };
    }
}
package it.unicam.cs.mpgc.rpg119474.engine.factory;

import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;

/** Crea i personaggi giocanti (Factory pattern). */
public final class CharacterFactory {

    private CharacterFactory() {
        // Classe di utilita': non istanziabile.
    }

    /** Crea un sopravvissuto della classe scelta, gia' equipaggiato con un'arma iniziale. */
    public static PlayerCharacter createSurvivor(String name, SurvivorClass survivorClass) {
        PlayerCharacter survivor = new PlayerCharacter(name, survivorClass);
        survivor.equip(ItemFactory.startingWeaponFor(survivorClass));
        return survivor;
    }
}
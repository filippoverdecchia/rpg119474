package it.unicam.cs.mpgc.rpg119474.core.character;

import java.util.Objects;

/**
 * Converte un sopravvissuto nella sua fotografia salvabile e viceversa.
 * <p>
 * La conversione sta qui e non dentro {@link PlayerCharacter} perche' e' un
 * compito diverso dal suo: il personaggio deve sapere come cresce, come si
 * equipaggia e come combatte, non in quale forma venga conservato su disco.
 * Tenerli separati permette di aggiungere altri formati di fotografia senza
 * gonfiare l'entita' di dominio, e rende evidente che il modello di gioco non
 * dipende dalla persistenza.
 */
public final class CharacterSnapshots {

    private CharacterSnapshots() {
        // Classe di utilita': non istanziabile.
    }

    /** Fotografia dello stato attuale del sopravvissuto. */
    public static CharacterSnapshot of(PlayerCharacter survivor) {
        Objects.requireNonNull(survivor, "survivor");
        return new CharacterSnapshot(survivor.name(), survivor.survivorClass(),
                survivor.experience(), survivor.currentHealth(),
                survivor.weapon().orElse(null), survivor.armor().orElse(null),
                survivor.inventory().asList());
    }

    /**
     * Ricostruisce il sopravvissuto da una fotografia.
     * <p>
     * Le abilita' non vengono lette dal salvataggio ma ricavate dalla classe del
     * personaggio: restano cosi' sempre allineate alla versione corrente del gioco.
     */
    public static PlayerCharacter restore(CharacterSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
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
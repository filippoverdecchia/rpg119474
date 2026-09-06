package it.unicam.cs.mpgc.rpg119474.engine.campaign;

import it.unicam.cs.mpgc.rpg119474.core.character.CharacterSnapshot;

import java.io.Serializable;
import java.util.Objects;

/**
 * Stato salvabile di una partita: la fotografia del sopravvissuto e il punto del
 * percorso a cui e' arrivato.
 * <p>
 * Il progresso nella campagna non e' stato messo dentro {@code CharacterSnapshot}
 * perche' non e' una caratteristica del personaggio: lo stesso sopravvissuto
 * potrebbe affrontare percorsi diversi. Comporre i due dati qui tiene il dominio
 * indipendente dalla modalita' di gioco.
 *
 * @param survivor     stato del personaggio
 * @param currentStage numero della tappa da affrontare; se supera la lunghezza
 *                     della campagna, il percorso e' stato completato
 */
public record CampaignSave(CharacterSnapshot survivor, int currentStage) implements Serializable {

    public CampaignSave {
        Objects.requireNonNull(survivor, "survivor");
        if (currentStage < 1) {
            throw new IllegalArgumentException("currentStage deve essere >= 1");
        }
    }
}
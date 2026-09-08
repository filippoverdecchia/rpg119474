package it.unicam.cs.mpgc.rpg119474.engine.campaign;

import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.core.character.CharacterSnapshots;

import java.util.List;
import java.util.Objects;

/**
 * Una partita in corso: un sopravvissuto che percorre una campagna, con il
 * ricordo di quanta strada ha fatto.
 * <p>
 * Tiene insieme il percorso (immutabile e condivisibile), il personaggio e la
 * posizione raggiunta, e applica le conseguenze di ogni tappa: superandola si
 * avanza, perdendola si paga il prezzo stabilito dalla {@link DefeatPenalty} e
 * si resta dove si era. Non sa nulla di come si combatte: lo scontro e'
 * responsabilita' del motore, qui si registra soltanto com'e' andato.
 */
public class CampaignRun {

    private final Campaign campaign;
    private final DefeatPenalty defeatPenalty;
    private final PlayerCharacter player;
    private int currentStage;

    /** Inizia una nuova partita dalla prima tappa. */
    public CampaignRun(Campaign campaign, DefeatPenalty defeatPenalty, PlayerCharacter player) {
        this(campaign, defeatPenalty, player, 1);
    }

    /** Riprende una partita dalla tappa indicata. */
    public CampaignRun(Campaign campaign, DefeatPenalty defeatPenalty,
                       PlayerCharacter player, int currentStage) {
        this.campaign = Objects.requireNonNull(campaign, "campaign");
        this.defeatPenalty = Objects.requireNonNull(defeatPenalty, "defeatPenalty");
        this.player = Objects.requireNonNull(player, "player");
        if (currentStage < 1 || currentStage > campaign.length() + 1) {
            throw new IllegalArgumentException("Tappa iniziale non valida: " + currentStage);
        }
        this.currentStage = currentStage;
    }

    public Campaign campaign() {
        return campaign;
    }

    public PlayerCharacter player() {
        return player;
    }

    /** Numero della tappa da affrontare (oltre la lunghezza se la campagna e' finita). */
    public int currentStageNumber() {
        return currentStage;
    }

    /** {@code true} se tutte le tappe sono state superate. */
    public boolean isComplete() {
        return currentStage > campaign.length();
    }

    /**
     * Tappa da affrontare ora.
     *
     * @throws IllegalStateException se la campagna e' gia' stata completata
     */
    public CampaignStage currentStage() {
        if (isComplete()) {
            throw new IllegalStateException("La campagna e' gia' stata completata");
        }
        return campaign.stage(currentStage);
    }

    /** Registra la vittoria nella tappa corrente e avanza lungo il percorso. */
    public StageOutcome recordVictory() {
        CampaignStage stage = currentStage();
        boolean wasFinal = campaign.isFinalStage(stage.number());
        currentStage++;
        return new StageOutcome(stage.number(), stage.title(), true, List.of(), wasFinal);
    }

    /** Registra la sconfitta: si applica la penalita' e la tappa resta da affrontare. */
    public StageOutcome recordDefeat() {
        CampaignStage stage = currentStage();
        List<Item> lost = defeatPenalty.applyTo(player);
        return new StageOutcome(stage.number(), stage.title(), false, lost, false);
    }

    /** Fotografia salvabile della partita. */
    public CampaignSave toSave() {
        return new CampaignSave(CharacterSnapshots.of(player), currentStage);
    }

    /** Riprende una partita da un salvataggio, ricostruendo il sopravvissuto. */
    public static CampaignRun resume(Campaign campaign, DefeatPenalty defeatPenalty, CampaignSave save) {
        Objects.requireNonNull(save, "save");
        return new CampaignRun(campaign, defeatPenalty,
                CharacterSnapshots.restore(save.survivor()), save.currentStage());
    }
}
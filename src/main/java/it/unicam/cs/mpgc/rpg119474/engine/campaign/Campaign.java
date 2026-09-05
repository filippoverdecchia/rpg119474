package it.unicam.cs.mpgc.rpg119474.engine.campaign;

import java.util.List;
import java.util.Objects;

/**
 * Una campagna: una sequenza finita e ordinata di tappe, dalla prima all'ultima,
 * che il sopravvissuto affronta una dopo l'altra.
 * <p>
 * La classe si limita a descrivere il percorso e a rispondere su di esso; non
 * sa nulla di come si combatte ne' di chi lo stia percorrendo. L'avanzamento del
 * giocatore e' gestito altrove, cosi' la stessa campagna puo' essere affrontata
 * da personaggi diversi.
 */
public class Campaign {

    private final String title;
    private final List<CampaignStage> stages;

    public Campaign(String title, List<CampaignStage> stages) {
        this.title = Objects.requireNonNull(title, "title");
        Objects.requireNonNull(stages, "stages");
        if (stages.isEmpty()) {
            throw new IllegalArgumentException("una campagna deve avere almeno una tappa");
        }
        this.stages = List.copyOf(stages);
    }

    public String title() {
        return title;
    }

    /** Numero di tappe che compongono la campagna. */
    public int length() {
        return stages.size();
    }

    /** Tutte le tappe, in ordine. */
    public List<CampaignStage> stages() {
        return stages;
    }

    /**
     * Tappa con il numero indicato.
     *
     * @throws IllegalArgumentException se il numero non appartiene alla campagna
     */
    public CampaignStage stage(int number) {
        if (!contains(number)) {
            throw new IllegalArgumentException("Tappa inesistente: " + number);
        }
        return stages.get(number - 1);
    }

    /** {@code true} se il numero indicato corrisponde a una tappa della campagna. */
    public boolean contains(int number) {
        return number >= 1 && number <= stages.size();
    }

    /** {@code true} se la tappa indicata e' l'ultima, quella dello scontro finale. */
    public boolean isFinalStage(int number) {
        return number == stages.size();
    }
}
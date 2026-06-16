package it.unicam.cs.mpgc.rpg119474.core.dice;

import java.util.List;

/** Esito immutabile di un tiro di dadi. */
public record RollResult(int faces, List<Integer> rolls) {

    public RollResult {
        if (faces < 1) {
            throw new IllegalArgumentException("faces deve essere >= 1");
        }
        rolls = List.copyOf(rolls);
    }

    public int total() {
        return rolls.stream().mapToInt(Integer::intValue).sum();
    }
}

package it.unicam.cs.mpgc.rpg119474.core.dice;

import java.util.ArrayList;
import java.util.List;

/** Tiro di {@code count} dadi da {@code faces} facce (es. 2d6). */
public record Dice(int count, int faces) {

    public Dice {
        if (count < 1) {
            throw new IllegalArgumentException("count deve essere >= 1");
        }
        if (faces < 2) {
            throw new IllegalArgumentException("faces deve essere >= 2");
        }
    }

    public RollResult roll(RandomSource rng) {
        List<Integer> results = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            results.add(rng.nextInt(faces) + 1);
        }
        return new RollResult(faces, results);
    }

    public static Dice of(int count, int faces) {
        return new Dice(count, faces);
    }
}

package it.unicam.cs.mpgc.rpg119474.core.dice;

import java.util.Random;

/**
 * Sorgente di casualita' astratta: permette di iniettare un generatore
 * deterministico nei test (Dependency Inversion) senza che il dominio dipenda
 * direttamente da {@link java.util.Random}.
 */
@FunctionalInterface
public interface RandomSource {

    /** Intero pseudo-casuale in [0, bound). */
    int nextInt(int bound);

    /** Sorgente deterministica basata su un seme (utile nei test). */
    static RandomSource seeded(long seed) {
        Random random = new Random(seed);
        return random::nextInt;
    }

    /** Sorgente non deterministica di default. */
    static RandomSource systemDefault() {
        Random random = new Random();
        return random::nextInt;
    }
}

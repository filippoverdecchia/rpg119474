package it.unicam.cs.mpgc.rpg119474.core.ability;

import it.unicam.cs.mpgc.rpg119474.core.stats.DamageType;

/** Calcola il danno effettivo dopo la riduzione dell'armatura, in base al tipo di danno. */
public final class DamageCalculator {

    private DamageCalculator() {
        // Classe di utilita': non istanziabile.
    }

    /** Danno effettivo (minimo 1) dopo aver applicato la difesa secondo il tipo. */
    public static int afterDefense(int rawDamage, int defense, DamageType type) {
        int effective = switch (type) {
            case MISCHIA, BALISTICO -> rawDamage - defense;          // armatura piena
            case ENERGIA, RADIAZIONI -> rawDamage - defense / 2;     // bucano meta' armatura
            case VELENO -> rawDamage;                                // ignora l'armatura
        };
        return Math.max(1, effective);
    }
}

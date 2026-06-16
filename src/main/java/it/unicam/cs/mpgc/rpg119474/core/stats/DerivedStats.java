package it.unicam.cs.mpgc.rpg119474.core.stats;

import it.unicam.cs.mpgc.rpg119474.core.attribute.Attributes;

/**
 * Statistiche di combattimento derivate dagli attributi primari.
 * Il calcolo e' una funzione pura ({@link #from(Attributes)}): nessuno stato,
 * facile da testare, e ogni attributo ha un effetto chiaro.
 */
public record DerivedStats(int maxHealth, int defense, int initiative,
                           int maxActionPoints, int critChance) {

    public static DerivedStats from(Attributes a) {
        return new DerivedStats(
                40 + a.resistenza() * 10,          // Resistenza -> punti vita
                a.resistenza() * 2,                // Resistenza -> difesa
                a.agilita(),                       // Agilita'   -> ordine dei turni
                6 + a.agilita() / 2,               // Agilita'   -> Punti Azione
                Math.min(60, 5 + a.fortuna() * 4)); // Fortuna   -> probabilita' di critico (max 60%)
    }
}

package it.unicam.cs.mpgc.rpg119474.core.character;

/**
 * Curva di esperienza: XP totale necessaria per raggiungere un dato livello.
 * Essendo iniettabile, la regola di progressione si puo' cambiare senza
 * modificare il personaggio (Open/Closed + Dependency Inversion).
 */
@FunctionalInterface
public interface ExperienceCurve {

    int experienceForLevel(int level);

    /** Curva quadratica standard: 0 al livello 1, poi 100 * (level-1)^2. */
    static ExperienceCurve standard() {
        return level -> level <= 1 ? 0 : 100 * (level - 1) * (level - 1);
    }
}

package it.unicam.cs.mpgc.rpg119474.engine.campaign;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.engine.factory.EnemyFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Campagne predefinite del gioco (Factory pattern).
 * <p>
 * Una campagna e' descritta da dati: aggiungere una tappa, cambiarne l'ordine o
 * scrivere un percorso alternativo significa comporre un altro elenco, senza
 * toccare il codice che la esegue.
 */
public final class Campaigns {

    private Campaigns() {
        // Classe di utilita': non istanziabile.
    }

    /**
     * La campagna principale: otto tappe che si spingono sempre piu' a fondo nella
     * Zona, con avversari via via piu' duri fino allo scontro finale.
     */
    public static Campaign wasteland() {
        List<Stage> percorso = List.of(
                new Stage("I margini della Zona", EnemyFactory::scavenger),
                new Stage("La discarica", EnemyFactory::mutantDog),
                new Stage("Il posto di blocco", EnemyFactory::raider),
                new Stage("L'avamposto della milizia", EnemyFactory::militiaman),
                new Stage("Le rovine radioattive", EnemyFactory::ghoul),
                new Stage("I sotterranei", EnemyFactory::mutatedBrute),
                new Stage("La fortezza dei predoni", EnemyFactory::raiderBoss),
                new Stage("Il cuore della Zona", EnemyFactory::wasteWarlord));

        List<CampaignStage> stages = new ArrayList<>(percorso.size());
        for (int i = 0; i < percorso.size(); i++) {
            Stage stage = percorso.get(i);
            stages.add(new CampaignStage(i + 1, stage.title(), stage.enemy()));
        }
        return new Campaign("La Lunga Marcia", stages);
    }

    /** Descrizione di una tappa prima che le venga assegnato il numero d'ordine. */
    private record Stage(String title, Supplier<Enemy> enemy) {
    }
}
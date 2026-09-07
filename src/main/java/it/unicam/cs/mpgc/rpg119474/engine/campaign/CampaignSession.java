package it.unicam.cs.mpgc.rpg119474.engine.campaign;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.engine.DefaultGameService;
import it.unicam.cs.mpgc.rpg119474.engine.GameService;
import it.unicam.cs.mpgc.rpg119474.engine.ai.EnemyStrategy;
import it.unicam.cs.mpgc.rpg119474.engine.combat.CombatEvent;
import it.unicam.cs.mpgc.rpg119474.engine.event.Observer;
import it.unicam.cs.mpgc.rpg119474.engine.progression.ProgressionService;
import it.unicam.cs.mpgc.rpg119474.engine.progression.VictoryOutcome;

import java.util.Objects;
import java.util.Optional;

/**
 * Facciata di una partita in campagna: mette in comunicazione il percorso
 * ({@link CampaignRun}) e i singoli scontri ({@link GameService}).
 * <p>
 * Esiste perche' senza di essa il compito di far combaciare le due cose -
 * generare l'avversario della tappa, aprire il duello, riscuotere la ricompensa,
 * registrare l'esito e avanzare - finirebbe nello strato di presentazione, che
 * tornerebbe cosi' a contenere regole di gioco. Qui invece resta nell'engine,
 * dove puo' essere verificata senza avviare l'interfaccia grafica.
 * <p>
 * Le regole che governano la partita (progressione, penalita', comportamento
 * dell'avversario, casualita') sono tutte iniettate: una campagna piu' severa o
 * un'intelligenza artificiale diversa non richiedono modifiche a questa classe.
 */
public class CampaignSession {

    private final Campaign campaign;
    private final DefeatPenalty defeatPenalty;
    private final ProgressionService progressionService;
    private final EnemyStrategy enemyStrategy;
    private final RandomSource rng;

    private CampaignRun run;
    private GameService game;
    private Enemy enemy;
    private StageOutcome lastOutcome;
    private VictoryOutcome lastReward;

    public CampaignSession(Campaign campaign, DefeatPenalty defeatPenalty,
                           ProgressionService progressionService, EnemyStrategy enemyStrategy,
                           RandomSource rng) {
        this.campaign = Objects.requireNonNull(campaign, "campaign");
        this.defeatPenalty = Objects.requireNonNull(defeatPenalty, "defeatPenalty");
        this.progressionService = Objects.requireNonNull(progressionService, "progressionService");
        this.enemyStrategy = Objects.requireNonNull(enemyStrategy, "enemyStrategy");
        this.rng = Objects.requireNonNull(rng, "rng");
    }

    /** Comincia una nuova partita con il sopravvissuto indicato. */
    public void begin(PlayerCharacter player) {
        this.run = new CampaignRun(campaign, defeatPenalty, player);
        this.game = null;
        this.enemy = null;
        this.lastOutcome = null;
        this.lastReward = null;
    }

    /** Riprende una partita salvata. */
    public void resume(CampaignSave save) {
        this.run = CampaignRun.resume(campaign, defeatPenalty, save);
        this.game = null;
        this.enemy = null;
        this.lastOutcome = null;
        this.lastReward = null;
    }

    /**
     * Prepara lo scontro della tappa corrente generando l'avversario, senza ancora
     * cominciarlo.
     * <p>
     * La preparazione e' distinta dall'avvio perche' chi mostra lo scontro deve
     * poter conoscere l'avversario per allestire la schermata, e solo dopo mettersi
     * in ascolto: se il duello partisse subito, i primi eventi andrebbero perduti.
     *
     * @return l'avversario che si affrontera'
     */
    public Enemy prepareStage() {
        requireStarted();
        this.enemy = run.currentStage().spawnEnemy();
        this.lastOutcome = null;
        this.lastReward = null;
        this.game = new DefaultGameService(rng, progressionService);
        return enemy;
    }

    /**
     * Avvia lo scontro preparato: l'osservatore ricevera' tutti gli eventi di
     * combattimento, dal primo all'ultimo.
     *
     * @throws IllegalStateException se la tappa non e' stata preparata
     */
    public void startStage(Observer<CombatEvent> observer) {
        requireStarted();
        if (game == null || enemy == null) {
            throw new IllegalStateException("La tappa non e' stata preparata");
        }
        game.startDuel(run.player(), enemy, enemyStrategy, observer);
    }

    /**
     * Chiude la tappa: riscuote la ricompensa se lo scontro e' stato vinto, poi ne
     * registra l'esito avanzando lungo il percorso oppure applicando la penalita'.
     * <p>
     * La ricompensa viene assegnata qui e non su richiesta di chi mostra il
     * risultato: e' una conseguenza della vittoria, non un dettaglio della
     * presentazione, e dimenticarsi di chiederla non deve poterla far perdere.
     * <p>
     * Chiamarla piu' volte per lo stesso scontro non ha effetti ulteriori: l'esito
     * viene calcolato una sola volta e poi restituito cosi' com'e'.
     *
     * @throws IllegalStateException se lo scontro non e' ancora terminato
     */
    public StageOutcome endStage() {
        requireStarted();
        if (lastOutcome != null) {
            return lastOutcome;
        }
        if (game == null || !game.isOver()) {
            throw new IllegalStateException("Lo scontro non e' ancora terminato");
        }
        if (playerWon()) {
            lastReward = game.resolveVictory().orElse(null);
            lastOutcome = run.recordVictory();
        } else {
            lastOutcome = run.recordDefeat();
        }
        return lastOutcome;
    }

    /** {@code true} se lo scontro e' finito ed e' stato vinto dal giocatore. */
    private boolean playerWon() {
        return game != null && game.isOver()
                && game.winner().map(winner -> winner == run.player()).orElse(false);
    }

    /**
     * Ricompensa dell'ultima tappa conclusa: esperienza, eventuale passaggio di
     * livello e bottino raccolto. Vuota finche' la tappa non e' stata chiusa con
     * {@link #endStage()}, o se lo scontro e' stato perso.
     */
    public Optional<VictoryOutcome> reward() {
        return Optional.ofNullable(lastReward);
    }

    public Campaign campaign() {
        return campaign;
    }

    public PlayerCharacter player() {
        requireStarted();
        return run.player();
    }

    /** Avversario dello scontro in corso. */
    public Enemy enemy() {
        return enemy;
    }

    /** Servizio con cui la presentazione comanda lo scontro in corso. */
    public GameService game() {
        return game;
    }

    /** Tappa da affrontare ora. */
    public CampaignStage currentStage() {
        requireStarted();
        return run.currentStage();
    }

    public boolean isCampaignComplete() {
        requireStarted();
        return run.isComplete();
    }

    /** Fotografia salvabile della partita. */
    public CampaignSave toSave() {
        requireStarted();
        return run.toSave();
    }

    private void requireStarted() {
        if (run == null) {
            throw new IllegalStateException("Nessuna partita avviata");
        }
    }
}
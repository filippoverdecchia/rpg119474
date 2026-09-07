package it.unicam.cs.mpgc.rpg119474.ui;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.engine.ai.EnemyStrategies;
import it.unicam.cs.mpgc.rpg119474.engine.campaign.CampaignSave;
import it.unicam.cs.mpgc.rpg119474.engine.campaign.CampaignSession;
import it.unicam.cs.mpgc.rpg119474.engine.campaign.CampaignStage;
import it.unicam.cs.mpgc.rpg119474.engine.campaign.Campaigns;
import it.unicam.cs.mpgc.rpg119474.engine.campaign.StageOutcome;
import it.unicam.cs.mpgc.rpg119474.engine.campaign.SuppliesLostPenalty;
import it.unicam.cs.mpgc.rpg119474.engine.factory.CharacterFactory;
import it.unicam.cs.mpgc.rpg119474.engine.loot.DefaultLootService;
import it.unicam.cs.mpgc.rpg119474.engine.progression.DefaultProgressionService;
import it.unicam.cs.mpgc.rpg119474.persistence.JsonRepository;
import it.unicam.cs.mpgc.rpg119474.persistence.Repository;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;

/**
 * Applicazione JavaFX: sceglie quale schermata mostrare e mette in comunicazione
 * la partita con i salvataggi.
 * <p>
 * Non contiene regole di gioco: lo svolgimento della campagna e' affidato alla
 * {@link CampaignSession} e la conservazione dei dati all'astrazione
 * {@link Repository}, cosi' che cambiare formato di salvataggio o regole di
 * gioco non richieda di intervenire qui.
 */
public class GameApplication extends Application {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 620;
    private static final Path SAVE_DIRECTORY = Path.of("saves");

    /** Quota di punti vita recuperata vincendo una tappa: in campagna il riposo e' breve. */
    private static final int CAMPAIGN_RECOVERY_PERCENT = 30;

    private final RandomSource rng = RandomSource.systemDefault();
    private final Repository<CampaignSave> repository =
            new JsonRepository<>(SAVE_DIRECTORY, CampaignSave.class);
    private final CampaignSession session = new CampaignSession(
            Campaigns.wasteland(),
            new SuppliesLostPenalty(),
            new DefaultProgressionService(new DefaultLootService(rng), CAMPAIGN_RECOVERY_PERCENT),
            EnemyStrategies.aggressive(),
            rng);

    private Stage window;

    @Override
    public void start(Stage window) {
        this.window = window;
        window.setTitle("Wasteland");
        showSetup();
        window.show();
    }

    private void showSetup() {
        SetupView setup = new SetupView(session.campaign().title(),
                this::startNewCampaign, repository.listIds(), this::resumeCampaign);
        show(setup.getRoot());
    }

    private void startNewCampaign(String name, SurvivorClass survivorClass) {
        session.begin(CharacterFactory.createSurvivor(name, survivorClass));
        openCurrentStage();
    }

    private void resumeCampaign(String saveId) {
        repository.load(saveId).ifPresent(save -> {
            session.resume(save);
            openCurrentStage();
        });
    }

    /** Mostra la tappa da affrontare, o la schermata finale se la campagna e' conclusa. */
    private void openCurrentStage() {
        if (session.isCampaignComplete()) {
            showCampaignEnd();
            return;
        }
        CampaignStage stage = session.currentStage();
        Enemy enemy = session.prepareStage();
        CombatActions actions = new CombatActions(
                this::saveProgress, this::concludeStage, this::openCurrentStage, this::showSetup);
        CombatView view = new CombatView(session.game(), session.player(), enemy,
                CombatEventFormatter.describe(stage, session.campaign().length()), actions);
        session.startStage(view::onEvent);
        view.refresh();
        show(view.getRoot());
    }

    private void showCampaignEnd() {
        CampaignEndView end = new CampaignEndView(session.campaign().title(),
                session.player(), this::showSetup);
        show(end.getRoot());
    }

    /** Chiude la tappa, salva il progresso e restituisce il resoconto da mostrare. */
    private String concludeStage() {
        StageOutcome outcome = session.endStage();
        saveProgress();
        StringBuilder report = new StringBuilder();
        session.reward().ifPresent(reward ->
                report.append(CombatEventFormatter.describe(reward)).append('\n'));
        return report.append(CombatEventFormatter.describe(outcome)).toString();
    }

    private void saveProgress() {
        repository.save(saveId(session.player()), session.toSave());
    }

    private void show(Parent content) {
        window.setScene(new Scene(content, WIDTH, HEIGHT));
    }

    private static String saveId(PlayerCharacter player) {
        return player.name().trim().replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
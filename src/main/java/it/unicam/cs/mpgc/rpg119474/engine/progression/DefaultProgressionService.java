package it.unicam.cs.mpgc.rpg119474.engine.progression;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;

import java.util.Objects;

/**
 * Progressione predefinita: il vincitore incassa l'esperienza del nemico
 * sconfitto, applica gli eventuali passaggi di livello e recupera i punti vita
 * prima dello scontro successivo.
 */
public class DefaultProgressionService implements ProgressionService {

    @Override
    public VictoryOutcome awardVictory(PlayerCharacter player, Enemy defeated) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(defeated, "defeated");

        int levelBefore = player.level();
        int reward = defeated.experienceReward();
        player.gainExperience(reward);
        player.heal(player.maxHealth());
        return new VictoryOutcome(reward, levelBefore, player.level());
    }
}
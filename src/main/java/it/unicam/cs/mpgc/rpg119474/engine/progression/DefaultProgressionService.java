package it.unicam.cs.mpgc.rpg119474.engine.progression;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.engine.loot.LootService;

import java.util.List;
import java.util.Objects;

/**
 * Progressione predefinita: il vincitore incassa l'esperienza del nemico
 * sconfitto, applica gli eventuali passaggi di livello, recupera i punti vita e
 * raccoglie nell'inventario il bottino deciso dal {@link LootService}.
 * <p>
 * La regola del bottino e' iniettata, quindi sostituibile senza toccare questa
 * classe (Dependency Inversion).
 */
public class DefaultProgressionService implements ProgressionService {

    private final LootService lootService;

    public DefaultProgressionService(LootService lootService) {
        this.lootService = Objects.requireNonNull(lootService, "lootService");
    }

    @Override
    public VictoryOutcome awardVictory(PlayerCharacter player, Enemy defeated) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(defeated, "defeated");

        int levelBefore = player.level();
        int reward = defeated.experienceReward();
        player.gainExperience(reward);
        player.heal(player.maxHealth());

        List<Item> loot = lootService.rollLoot(defeated);
        loot.forEach(player.inventory()::add);

        return new VictoryOutcome(reward, levelBefore, player.level(), loot);
    }
}
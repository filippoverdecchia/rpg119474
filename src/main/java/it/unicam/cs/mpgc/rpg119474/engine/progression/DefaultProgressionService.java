package it.unicam.cs.mpgc.rpg119474.engine.progression;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.engine.loot.LootService;

import java.util.List;
import java.util.Objects;

/**
 * Progressione predefinita: il vincitore incassa l'esperienza del nemico
 * sconfitto, applica gli eventuali passaggi di livello, recupera una quota dei
 * punti vita e raccoglie nell'inventario il bottino deciso dal {@link LootService}.
 * <p>
 * Quanto si recupera e' un parametro perche' cambia il senso del gioco: negli
 * scontri isolati conviene ripartire in forze, mentre in una campagna il
 * recupero e' parziale e le scorte diventano una risorsa da amministrare.
 * Insieme alla regola del bottino, anch'essa iniettata, questa classe resta
 * valida senza modifiche in entrambe le modalita' (Open/Closed).
 */
public class DefaultProgressionService implements ProgressionService {

    /** Recupero completo: e' il comportamento degli scontri singoli. */
    private static final int FULL_RECOVERY = 100;

    private final LootService lootService;
    private final int healthRecoveryPercent;

    public DefaultProgressionService(LootService lootService) {
        this(lootService, FULL_RECOVERY);
    }

    public DefaultProgressionService(LootService lootService, int healthRecoveryPercent) {
        this.lootService = Objects.requireNonNull(lootService, "lootService");
        if (healthRecoveryPercent < 0 || healthRecoveryPercent > 100) {
            throw new IllegalArgumentException("healthRecoveryPercent deve essere tra 0 e 100");
        }
        this.healthRecoveryPercent = healthRecoveryPercent;
    }

    @Override
    public VictoryOutcome awardVictory(PlayerCharacter player, Enemy defeated) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(defeated, "defeated");

        int levelBefore = player.level();
        int reward = defeated.experienceReward();
        player.gainExperience(reward);
        player.heal(player.maxHealth() * healthRecoveryPercent / 100);

        List<Item> loot = lootService.rollLoot(defeated);
        loot.forEach(player.inventory()::add);

        return new VictoryOutcome(reward, levelBefore, player.level(), loot);
    }
}
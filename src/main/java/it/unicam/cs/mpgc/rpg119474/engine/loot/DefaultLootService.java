package it.unicam.cs.mpgc.rpg119474.engine.loot;

import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.dice.RandomSource;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.engine.factory.ItemFactory;

import java.util.List;
import java.util.Objects;

/**
 * Bottino deciso da una tabella: per ogni riga applicabile al nemico si tira la
 * probabilita' di caduta. Aggiungere un oggetto al bottino significa aggiungere
 * una riga alla tabella, senza modificare la logica di estrazione (Open/Closed).
 */
public class DefaultLootService implements LootService {

    /** Tabella predefinita: piu' il nemico e' duro, piu' oggetti puo' lasciare. */
    private static final List<LootEntry> DEFAULT_TABLE = List.of(
            new LootEntry(ItemFactory::medikit, 60, 0),
            new LootEntry(ItemFactory::leatherArmor, 35, 50),
            new LootEntry(ItemFactory::combatArmor, 25, 90),
            new LootEntry(ItemFactory::plasmaPistol, 20, 150));

    private final RandomSource rng;
    private final List<LootEntry> table;

    public DefaultLootService(RandomSource rng) {
        this(rng, DEFAULT_TABLE);
    }

    public DefaultLootService(RandomSource rng, List<LootEntry> table) {
        this.rng = Objects.requireNonNull(rng, "rng");
        this.table = List.copyOf(Objects.requireNonNull(table, "table"));
    }

    @Override
    public List<Item> rollLoot(Enemy defeated) {
        Objects.requireNonNull(defeated, "defeated");
        return table.stream()
                .filter(entry -> defeated.experienceReward() >= entry.minimumReward())
                .filter(entry -> rng.nextInt(100) < entry.percent())
                .map(entry -> entry.item().get())
                .toList();
    }
}
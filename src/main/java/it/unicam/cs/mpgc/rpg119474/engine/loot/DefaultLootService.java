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

    /**
     * Tabella predefinita, ordinata per difficolta' crescente del nemico: dai
     * predoni dei margini si recuperano medicine e protezioni leggere, mentre le
     * armi migliori e le corazze pesanti si trovano solo addosso agli avversari
     * piu' temibili.
     * <p>
     * Nessuna soglia arriva fino all'avversario finale: un oggetto ottenibile
     * soltanto dall'ultimo nemico non potrebbe mai essere usato, perche' con
     * quella vittoria la campagna si chiude.
     */
    private static final List<LootEntry> DEFAULT_TABLE = List.of(
            new LootEntry(ItemFactory::medikit, 55, 0),
            new LootEntry(ItemFactory::scavengedRifle, 30, 50),
            new LootEntry(ItemFactory::leatherArmor, 35, 50),
            new LootEntry(ItemFactory::spikedMace, 30, 60),
            new LootEntry(ItemFactory::combatArmor, 30, 75),
            new LootEntry(ItemFactory::regenSerum, 30, 90),
            new LootEntry(ItemFactory::assaultRifle, 30, 90),
            new LootEntry(ItemFactory::plasmaPistol, 30, 90),
            new LootEntry(ItemFactory::exoskeleton, 25, 90),
            new LootEntry(ItemFactory::inductionCannon, 20, 110));

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
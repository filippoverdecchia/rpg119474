package it.unicam.cs.mpgc.rpg119474.ui;

import it.unicam.cs.mpgc.rpg119474.core.item.Item;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Un gruppo di oggetti identici nello zaino, con il numero di esemplari posseduti.
 * <p>
 * Serve solo a mostrarli: dieci medikit sono dieci voci indistinguibili in un
 * elenco, mentre una sola voce con il conteggio si legge a colpo d'occhio.
 * L'inventario continua a contenere i singoli oggetti; il raggruppamento vive
 * nella presentazione, dove nasce il problema.
 *
 * @param item  un esemplare rappresentativo del gruppo
 * @param count quanti esemplari sono posseduti
 */
public record ItemStack<T extends Item>(T item, int count) {

    public ItemStack {
        Objects.requireNonNull(item, "item");
        if (count < 1) {
            throw new IllegalArgumentException("count deve essere >= 1");
        }
    }

    /** Etichetta da mostrare: il nome, seguito dal conteggio se ce n'e' piu' di uno. */
    public String label() {
        return count == 1 ? item.name() : item.name() + " x" + count;
    }

    /** Raggruppa per nome, conservando l'ordine di ritrovamento. */
    public static <T extends Item> List<ItemStack<T>> group(Stream<T> items) {
        Map<String, ItemStack<T>> byName = new LinkedHashMap<>();
        items.forEach(item -> byName.merge(item.name(), new ItemStack<>(item, 1),
                (existing, found) -> new ItemStack<>(existing.item(), existing.count() + 1)));
        return List.copyOf(byName.values());
    }
}
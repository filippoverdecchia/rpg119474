package it.unicam.cs.mpgc.rpg119474.core.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Contenitore generico di oggetti. Il vincolo {@code T extends Item} garantisce
 * che contenga solo oggetti di gioco, pur permettendo inventari specializzati
 * (es. {@code Inventory<Weapon>}).
 */
public class Inventory<T extends Item> {

    private final List<T> items = new ArrayList<>();

    public void add(T item) {
        items.add(Objects.requireNonNull(item, "item"));
    }

    public boolean remove(T item) {
        return items.remove(item);
    }

    public Optional<T> findFirst(Predicate<? super T> predicate) {
        return items.stream().filter(predicate).findFirst();
    }

    public List<T> asList() {
        return Collections.unmodifiableList(items);
    }

    public Stream<T> stream() {
        return items.stream();
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}

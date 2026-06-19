package it.unicam.cs.mpgc.rpg119474.persistence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Implementazione di {@link Repository} basata sulla serializzazione Java: ogni
 * entita' e' salvata come file {@code <id>.dat} in una cartella. Il vincolo
 * {@code T extends Serializable} garantisce che l'entita' sia serializzabile.
 *
 * @param <T> tipo dell'entita' (serializzabile)
 */
public class FileRepository<T extends Serializable> implements Repository<T> {

    private static final String EXTENSION = ".dat";

    private final Path directory;

    public FileRepository(Path directory) {
        this.directory = Objects.requireNonNull(directory, "directory");
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new PersistenceException("Impossibile creare la cartella dei salvataggi", e);
        }
    }

    @Override
    public void save(String id, T entity) {
        Objects.requireNonNull(entity, "entity");
        Path file = directory.resolve(id + EXTENSION);
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(entity);
        } catch (IOException e) {
            throw new PersistenceException("Errore nel salvataggio di " + id, e);
        }
    }

    @Override
    public Optional<T> load(String id) {
        Path file = directory.resolve(id + EXTENSION);
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            @SuppressWarnings("unchecked")
            T entity = (T) in.readObject();
            return Optional.of(entity);
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenceException("Errore nel caricamento di " + id, e);
        }
    }

    @Override
    public List<String> listIds() {
        try (Stream<Path> files = Files.list(directory)) {
            return files
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.endsWith(EXTENSION))
                    .map(name -> name.substring(0, name.length() - EXTENSION.length()))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new PersistenceException("Errore nell'elenco dei salvataggi", e);
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            return Files.deleteIfExists(directory.resolve(id + EXTENSION));
        } catch (IOException e) {
            throw new PersistenceException("Errore nell'eliminazione di " + id, e);
        }
    }
}
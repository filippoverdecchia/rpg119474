package it.unicam.cs.mpgc.rpg119474.persistence;

import it.unicam.cs.mpgc.rpg119474.core.item.Item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Implementazione di {@link Repository} che salva le entita' in formato
 * <strong>JSON</strong> tramite la libreria Gson, un file per entita'.
 * <p>
 * A differenza di {@link FileRepository}, che usa la serializzazione binaria di
 * Java, il salvataggio e' leggibile e ispezionabile, non dipende dalla forma
 * esatta delle classi Java e resta valido anche fuori dall'applicazione. Le due
 * classi realizzano la stessa interfaccia e sono interscambiabili: e' la ragione
 * per cui la persistenza e' stata messa dietro un'astrazione.
 *
 * @param <T> tipo dell'entita' gestita
 */
public class JsonRepository<T> implements Repository<T> {

    private static final String EXTENSION = ".json";

    private final Path directory;
    private final Class<T> type;
    private final Gson gson;

    public JsonRepository(Path directory, Class<T> type) {
        this(directory, type, defaultGson());
    }

    public JsonRepository(Path directory, Class<T> type, Gson gson) {
        this.directory = Objects.requireNonNull(directory, "directory");
        this.type = Objects.requireNonNull(type, "type");
        this.gson = Objects.requireNonNull(gson, "gson");
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new PersistenceException("Impossibile creare la cartella dei salvataggi", e);
        }
    }

    /** Gson configurato per il dominio del gioco: indentazione leggibile e gerarchia degli oggetti. */
    private static Gson defaultGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Item.class, new ItemJsonAdapter())
                .create();
    }

    @Override
    public void save(String id, T entity) {
        Objects.requireNonNull(entity, "entity");
        Path file = directory.resolve(id + EXTENSION);
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(entity, type, writer);
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
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return Optional.ofNullable(gson.fromJson(reader, type));
        } catch (IOException | JsonParseException e) {
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
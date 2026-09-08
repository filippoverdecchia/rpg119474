package it.unicam.cs.mpgc.rpg119474.persistence;

import it.unicam.cs.mpgc.rpg119474.core.character.CharacterSnapshot;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.core.character.CharacterSnapshots;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileRepositoryTest {

    @Test
    void savesAndLoadsACharacterSnapshot(@TempDir Path directory) {
        Repository<CharacterSnapshot> repository = new FileRepository<>(directory);
        PlayerCharacter hero = new PlayerCharacter("Eroe", SurvivorClass.BRUTO);
        hero.gainExperience(100); // livello 2

        repository.save("eroe", CharacterSnapshots.of(hero));
        CharacterSnapshot loaded = repository.load("eroe").orElseThrow();
        PlayerCharacter restored = CharacterSnapshots.restore(loaded);

        assertEquals("Eroe", restored.name());
        assertEquals(2, restored.level());
        assertEquals(100, restored.experience());
        assertTrue(repository.listIds().contains("eroe"));
    }

    @Test
    void loadReturnsEmptyWhenMissing(@TempDir Path directory) {
        Repository<CharacterSnapshot> repository = new FileRepository<>(directory);
        assertTrue(repository.load("inesistente").isEmpty());
    }
}

package it.unicam.cs.mpgc.rpg119474.persistence;

import it.unicam.cs.mpgc.rpg119474.core.character.CharacterSnapshot;
import it.unicam.cs.mpgc.rpg119474.core.character.CharacterSnapshots;
import it.unicam.cs.mpgc.rpg119474.core.character.PlayerCharacter;
import it.unicam.cs.mpgc.rpg119474.core.character.SurvivorClass;
import it.unicam.cs.mpgc.rpg119474.engine.factory.CharacterFactory;
import it.unicam.cs.mpgc.rpg119474.engine.factory.ItemFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonRepositoryTest {

    private static Repository<CharacterSnapshot> repository(Path directory) {
        return new JsonRepository<>(directory, CharacterSnapshot.class);
    }

    @Test
    void savesAndReloadsTheWholeCharacter(@TempDir Path directory) {
        Repository<CharacterSnapshot> repository = repository(directory);
        PlayerCharacter hero = CharacterFactory.createSurvivor("Vagabondo", SurvivorClass.CECCHINO);
        hero.gainExperience(150);
        hero.inventory().add(ItemFactory.medikit());
        hero.inventory().add(ItemFactory.combatArmor());
        hero.inventory().add(ItemFactory.leatherArmor());
        hero.equipFromInventory(ItemFactory.leatherArmor());
        hero.takeDamage(15);

        repository.save("vagabondo", CharacterSnapshots.of(hero));
        PlayerCharacter reloaded =
                CharacterSnapshots.restore(repository.load("vagabondo").orElseThrow());

        assertEquals(hero.name(), reloaded.name());
        assertEquals(hero.survivorClass(), reloaded.survivorClass());
        assertEquals(hero.level(), reloaded.level());
        assertEquals(hero.experience(), reloaded.experience());
        assertEquals(hero.currentHealth(), reloaded.currentHealth());
        assertEquals(hero.weapon(), reloaded.weapon());
        assertEquals(hero.armor(), reloaded.armor());
        assertEquals(hero.inventory().asList(), reloaded.inventory().asList(),
                "l'inventario contiene tipi diversi: il discriminante deve preservarli");
    }

    @Test
    void handlesACharacterWithoutEquipmentOrInventory(@TempDir Path directory) {
        Repository<CharacterSnapshot> repository = repository(directory);
        PlayerCharacter hero = new PlayerCharacter("Nudo", SurvivorClass.MEDICO);

        repository.save("nudo", CharacterSnapshots.of(hero));
        PlayerCharacter reloaded =
                CharacterSnapshots.restore(repository.load("nudo").orElseThrow());

        assertTrue(reloaded.weapon().isEmpty());
        assertTrue(reloaded.armor().isEmpty());
        assertTrue(reloaded.inventory().isEmpty());
    }

    @Test
    void producesAReadableJsonDocument(@TempDir Path directory) throws IOException {
        Repository<CharacterSnapshot> repository = repository(directory);
        PlayerCharacter hero = new PlayerCharacter("Eroe", SurvivorClass.BRUTO);
        hero.inventory().add(ItemFactory.medikit());
        repository.save("eroe", CharacterSnapshots.of(hero));

        String json = Files.readString(directory.resolve("eroe.json"));

        assertTrue(json.contains("\"name\""));
        assertTrue(json.contains("Eroe"));
        assertTrue(json.contains("BRUTO"));
        assertTrue(json.contains("\"type\""), "gli oggetti dell'inventario portano il discriminante");
    }

    @Test
    void listsAndDeletesTheSaves(@TempDir Path directory) {
        Repository<CharacterSnapshot> repository = repository(directory);
        repository.save("uno", CharacterSnapshots.of(new PlayerCharacter("Uno", SurvivorClass.BRUTO)));
        repository.save("due", CharacterSnapshots.of(new PlayerCharacter("Due", SurvivorClass.MEDICO)));

        assertEquals(List.of("due", "uno"), repository.listIds(), "gli id sono ordinati");
        assertTrue(repository.delete("uno"));
        assertEquals(List.of("due"), repository.listIds());
        assertFalse(repository.delete("inesistente"));
    }

    @Test
    void loadingAMissingCharacterReturnsEmpty(@TempDir Path directory) {
        assertTrue(repository(directory).load("nessuno").isEmpty());
    }
}
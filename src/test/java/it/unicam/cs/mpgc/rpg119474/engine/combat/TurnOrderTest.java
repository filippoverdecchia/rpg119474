package it.unicam.cs.mpgc.rpg119474.engine.combat;

import it.unicam.cs.mpgc.rpg119474.core.attribute.Attributes;
import it.unicam.cs.mpgc.rpg119474.core.character.Enemy;
import it.unicam.cs.mpgc.rpg119474.core.character.GameCharacter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TurnOrderTest {

    private static Enemy fighter(String name, int agilita) {
        return new Enemy(name, new Attributes(5, 5, 5, agilita, 5), 5, List.of(), 10);
    }

    @Test
    void theFastestFighterActsFirst() {
        GameCharacter slow = fighter("Lento", 2);
        GameCharacter fast = fighter("Veloce", 9);

        TurnOrder order = new TurnOrder(List.of(slow, fast));

        assertSame(fast, order.current(), "l'iniziativa piu' alta agisce per prima");
    }

    @Test
    void turnsAreCyclic() {
        GameCharacter first = fighter("Veloce", 9);
        GameCharacter second = fighter("Lento", 2);
        TurnOrder order = new TurnOrder(List.of(first, second));

        assertSame(second, order.next());
        assertSame(first, order.next(), "dopo l'ultimo si torna al primo");
    }

    @Test
    void tiesKeepTheEntryOrder() {
        GameCharacter player = fighter("Giocatore", 5);
        GameCharacter enemy = fighter("Nemico", 5);

        TurnOrder order = new TurnOrder(List.of(player, enemy));

        assertEquals(List.of(player, enemy), order.ordered(),
                "a parita' di iniziativa l'ordinamento e' stabile");
    }

    @Test
    void rejectsAnEmptyLineUp() {
        assertThrows(IllegalArgumentException.class, () -> new TurnOrder(List.of()));
    }
}
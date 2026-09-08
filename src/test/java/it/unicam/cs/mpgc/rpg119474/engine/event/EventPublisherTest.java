package it.unicam.cs.mpgc.rpg119474.engine.event;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventPublisherTest {

    @Test
    void everySubscriberReceivesThePublishedEvent() {
        EventPublisher<String> publisher = new EventPublisher<>();
        List<String> first = new ArrayList<>();
        List<String> second = new ArrayList<>();
        publisher.subscribe(first::add);
        publisher.subscribe(second::add);

        publisher.publish("colpo");

        assertEquals(List.of("colpo"), first);
        assertEquals(List.of("colpo"), second);
    }

    @Test
    void anUnsubscribedObserverReceivesNothingMore() {
        EventPublisher<String> publisher = new EventPublisher<>();
        List<String> received = new ArrayList<>();
        Observer<String> observer = received::add;
        publisher.subscribe(observer);
        publisher.publish("primo");

        publisher.unsubscribe(observer);
        publisher.publish("secondo");

        assertEquals(List.of("primo"), received);
    }

    @Test
    void publishingWithNoSubscribersIsHarmless() {
        assertDoesNotThrow(() -> new EventPublisher<String>().publish("nessuno ascolta"));
    }

    @Test
    void anObserverMayUnsubscribeWhileBeingNotified() {
        EventPublisher<String> publisher = new EventPublisher<>();
        List<String> received = new ArrayList<>();
        Observer<String>[] selfRemoving = newObserverHolder();
        selfRemoving[0] = event -> {
            received.add(event);
            publisher.unsubscribe(selfRemoving[0]);
        };
        publisher.subscribe(selfRemoving[0]);
        publisher.subscribe(received::add);

        assertDoesNotThrow(() -> publisher.publish("evento"),
                "la notifica scorre una copia della lista proprio per permetterlo");
        assertEquals(2, received.size());
    }

    @Test
    void aNullObserverIsRejected() {
        EventPublisher<String> publisher = new EventPublisher<>();

        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }

    @SuppressWarnings("unchecked")
    private static Observer<String>[] newObserverHolder() {
        return new Observer[1];
    }
}
package com.specification.reactive.reactivestreams.hotandcold;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class HotColdReactiveStreamTest {

    @Test
    public void coldPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        stringFlux.subscribe((element) -> System.out.println("Subscriber 1: " + element)); // Emits the value from the beginning
        Thread.sleep(3000);

        stringFlux.subscribe((element) -> System.out.println("Subscriber 2: " + element)); // Emits the value from the beginning
        Thread.sleep(4000);
    }

    @Test
    public void hotPublisherTest() throws InterruptedException {

        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();
        connectableFlux.subscribe((element) -> System.out.println("Subscriber 1: " + element)); // Does not emit the values from the beginning
        Thread.sleep(3000);

        connectableFlux.subscribe((element) -> System.out.println("Subscriber 2: " + element)); // Does not emit the values from the beginning
        Thread.sleep(4000);

    }
}

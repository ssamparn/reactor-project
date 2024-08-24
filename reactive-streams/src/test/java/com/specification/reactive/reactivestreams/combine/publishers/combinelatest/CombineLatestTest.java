package com.specification.reactive.reactivestreams.combine.publishers.combinelatest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

/**
 * combineLatest(): This is a static method of Flux.
 * combineLatest() will generate the events provided by the combination of the most recently emitted items.
 * */
public class CombineLatestTest {

    @Test
    public void combine_latest_test() {
        Flux<String> combinedLatestFlux = Flux.combineLatest(getString(), getNumber(), (s, i) -> s + i);

        StepVerifier.create(combinedLatestFlux)
                .expectSubscription()
                .expectNext("B1")
                .expectNext("C1")
                .expectNext("D1")
                .expectNext("D2")
                .expectNext("D3")
                .expectNext("D4")
                .expectNext("D5")
                .verifyComplete();
    }

    private static Flux<String> getString() {
        return Flux.just("A", "B", "C", "D")
                .delayElements(Duration.ofMillis(200));
    }

    private static Flux<Integer> getNumber() {
        return Flux.just(1, 2, 3, 4, 5)
                .delayElements(Duration.ofMillis(500));
    }
}

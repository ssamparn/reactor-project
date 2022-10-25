package com.specification.reactive.reactivestreams.tranformtest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class  FluxMonoCombineTest {

    @Test
    public void testMergeFlux() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C");
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona");

        Flux<String> mergedFlux = Flux.merge(alphabetFlux, nameFlux);
        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("Adam", "Jenny", "Mona")
                .verifyComplete();
    }

    @Test
    public void testMergeFlux_WithDelayElements() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.merge(alphabetFlux, nameFlux);
        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void testMergeFlux_ConcatWithoutDelayElements() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C");
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona");

        Flux<String> mergedFlux = Flux.concat(alphabetFlux, nameFlux);
        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("Adam", "Jenny", "Mona")
                .verifyComplete();
    }

    @Test
    public void testMergeFlux_WithConcatDelayElements() {

        Flux<String> alphabetFlux = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.concat(alphabetFlux, nameFlux);
        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("Adam", "Jenny", "Mona")
                .verifyComplete();
    }

    @Test
    public void testMergeFlux_WithZipWithoutDelayElements() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C");
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona");

        Flux<String> mergedFlux = Flux.zip(alphabetFlux, nameFlux, (firstFluxElement, secondFluxElement) -> {
            return firstFluxElement.concat(secondFluxElement);
        });

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("AAdam", "BJenny", "CMona")
                .verifyComplete();
    }

    @Test
    public void testMergeFlux_DelayElements_WithVirtualTime() {
        VirtualTimeScheduler.getOrSet();

        Flux<String> alphabetFlux = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.concat(alphabetFlux, nameFlux);

        StepVerifier.withVirtualTime(() -> mergedFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNextCount(6)
                .verifyComplete();
    }
}

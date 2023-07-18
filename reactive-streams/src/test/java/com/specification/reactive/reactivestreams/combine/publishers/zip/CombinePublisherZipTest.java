package com.specification.reactive.reactivestreams.combine.publishers.zip;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class CombinePublisherZipTest {

    @Test
    public void flux_publisher_simple_zip_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C");
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona");

        Flux<String> mergedFlux = Flux.zip(alphabetFlux, nameFlux, (alphabet, name) -> alphabet + "-" + name)
                .log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A-Adam", "B-Jenny", "C-Mona")
                .verifyComplete();
    }

    @Test
    public void flux_publisher_zip_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C");
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona");
        Flux<String> numFlux = Flux.just("1", "2", "3");
        Flux<String> lengthFlux = Flux.just("4", "5", "4");

        Flux<String> mergedFlux = Flux.zip(alphabetFlux, nameFlux, numFlux, lengthFlux)
                .map(tuple -> tuple.getT3() + "-" + tuple.getT1() + "-" + tuple.getT2() + "-" + tuple.getT4());

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("1-A-Adam-4")
                .expectNext("2-B-Jenny-5")
                .expectNext("3-C-Mona-4")
                .verifyComplete();
    }

    @Test
    public void flux_publisher_simple_zipWith_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C");
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona");

        Flux<String> mergedFlux = alphabetFlux.zipWith(nameFlux, (alphabet, name) -> alphabet + "-" + name)
                .log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A-Adam", "B-Jenny", "C-Mona")
                .verifyComplete();
    }

    @Test
    public void mono_publisher_simple_zipWith_test() {
        Mono<String> alphabetMono = Mono.just("A");
        Mono<String> nameMono = Mono.just("Adam");

        Mono<String> mergedFlux = alphabetMono.zipWith(nameMono, (alphabet, name) -> alphabet + "-" + name)
                .log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A-Adam")
                .verifyComplete();
    }
}

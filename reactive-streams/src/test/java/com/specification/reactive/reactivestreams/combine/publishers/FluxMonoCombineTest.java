package com.specification.reactive.reactivestreams.combine.publishers;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class  FluxMonoCombineTest {

    @Test
    public void flux_publisher_merge_test() {
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
    public void flux_publisher_merge_with_delayElements_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(200));
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona")
                .delayElements(Duration.ofMillis(125));

        Flux<String> mergedFlux = Flux.merge(alphabetFlux, nameFlux);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void flux_publisher_mergeWith_delayElements_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(200));
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona")
                .delayElements(Duration.ofMillis(125));

        Flux<String> mergedFlux = alphabetFlux.mergeWith(nameFlux);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void mono_publisher_merge_with_test() {
        Mono<String> alphabetMono = Mono.just("A")
                .delayElement(Duration.ofMillis(200));
        Mono<String> nameMono = Mono.just("Adam")
                .delayElement(Duration.ofMillis(100));

        Flux<String> mergedFlux = alphabetMono.mergeWith(nameMono);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("Adam")
                .expectNext("A")
                .verifyComplete();
    }

    @Test
    public void flux_publisher_mergeSquential_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(80));
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona")
                .delayElements(Duration.ofMillis(60));

        Flux<String> mergedFlux = Flux.mergeSequential(alphabetFlux, nameFlux);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("Adam", "Jenny", "Mona")
                .verifyComplete();
    }

    @Test
    public void flux_publisher_concat_test() {
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
    public void flux_publisher_concatWith_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C");
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona");

        Flux<String> mergedFlux = alphabetFlux.concatWith(nameFlux);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("Adam", "Jenny", "Mona")
                .verifyComplete();
    }

    @Test
    public void mono_publisher_concatWith_test() {
        Mono<String> alphabetMono = Mono.just("A");
        Mono<String> nameMono = Mono.just("Adam");

        Flux<String> mergedFlux = alphabetMono.concatWith(nameMono);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A")
                .expectNext("Adam")
                .verifyComplete();
    }

    @Test
    public void flux_publisher_concat_with_delayElements_test() {

        Flux<String> alphabetFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofSeconds(1));
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona")
                .delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.concat(alphabetFlux, nameFlux);
        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("Adam", "Jenny", "Mona")
                .verifyComplete();
    }

    @Test
    public void flux_publisher_merge_with_DelayElements_withVirtualTime_test() {
        VirtualTimeScheduler.getOrSet();

        Flux<String> alphabetFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofSeconds(1));
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona")
                .delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.concat(alphabetFlux, nameFlux);

        StepVerifier.withVirtualTime(mergedFlux::log)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNextCount(6)
                .verifyComplete();
    }

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

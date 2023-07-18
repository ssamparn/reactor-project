package com.specification.reactive.reactivestreams.combine.publishers.concat;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class CombinePublisherConcatWithTest {

//    concat and concatWith() are lazy subscription
//    The concatenation is achieved by sequentially subscribing to the first source then waiting for it to complete before subscribing to the next,
//    and so on until the last source completes. Any error interrupts the sequence immediately and is forwarded downstream.

    @Test
    public void combinePublisherConcatTest() {

        Flux<String> alphabetFlux = Flux.just("A", "B", "C", "D");
        Flux<String> nameFlux = Flux.just("Adam", "Bob", "Cathie", "Darwin"); // Will be emitted lazily after alphabetFlux.
        Flux<String> countryFlux = Flux.just("America", "Britain", "Canada", "Denmark"); // Will be emitted lazily after alphabetFlux and nameFlux

        Flux.concat(alphabetFlux, nameFlux, countryFlux) // All the events will be emitted sequentially one after another
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void combinePublisherConcatWithTest() {

        Flux<String> alphabetFlux = Flux.just("A", "B", "C", "D");
        Flux<String> nameFlux = Flux.just("Adam", "Bob", "Cathie", "Darwin"); // Will be emitted lazily after alphabetFlux.
        Flux<String> countryFlux = Flux.just("America", "Britain", "Canada", "Denmark"); // Will be emitted lazily after alphabetFlux and nameFlux

        alphabetFlux
                .concatWith(nameFlux)
                .concatWith(countryFlux) // All the events will be emitted sequentially one after another
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void combinePublisherConcatWithErrorTest() {

        Flux<String> alphabetFlux = Flux.just("A", "B", "C", "D");
        Flux<String> nameFlux = Flux.just("Adam", "Bob", "Cathie", "Darwin"); // Will be emitted lazily after alphabetFlux.
        Flux<String> countryFlux = Flux.just("America", "Britain", "Canada", "Denmark"); // Will be emitted lazily after alphabetFlux and nameFlux
        Flux<String> errorFlux = Flux.error(new RuntimeException("oops"));

        alphabetFlux
                .concatWith(errorFlux) // error got introduced in the pipeline
                .concatWith(nameFlux) // Here as the error occured after the alphabet events gets emitted, it will stop the pipeline
                .concatWith(countryFlux) // So name and country flux will not be emitted. To overcome the above situation, use concatDelayError()
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void combinePublisherConcatDelayErrorTest() {

        Flux<String> alphabetFlux = Flux.just("A", "B", "C", "D");
        Flux<String> nameFlux = Flux.just("Adam", "Bob", "Cathie", "Darwin"); // Will be emitted lazily after alphabetFlux.
        Flux<String> countryFlux = Flux.just("America", "Britain", "Canada", "Denmark"); // Will be emitted lazily after alphabetFlux and nameFlux
        Flux<String> errorFlux = Flux.error(new RuntimeException("oops"));

        Flux.concatDelayError(
                alphabetFlux,
                errorFlux,      // error got introduced in the pipeline
                nameFlux,       // Here as the error occurred after the alphabet events gets emitted, it will stop the pipeline
                countryFlux
        ).subscribe(RsUtil.subscriber()); // As we have used concatDelayError, name and country flux will be emitted.
        // Error will be emitted at the very end.
    }

    @Test
    public void mono_publisher_concatWith_test() {
        Mono<String> alphabetMono1 = Mono.just("A");
        Mono<String> nameMono1 = Mono.just("Adam");
        Mono<String> alphabetMono2 = Mono.just("B");
        Mono<String> nameMono2 = Mono.just("Bram");

        Flux<String> mergedFlux = alphabetMono1
                .concatWith(nameMono1)
                .concatWith(alphabetMono2)
                .concatWith(nameMono2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A")
                .expectNext("Adam")
                .expectNext("B")
                .expectNext("Bram")
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
    public void flux_publisher_concatWith_delayElements_test() {

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
}

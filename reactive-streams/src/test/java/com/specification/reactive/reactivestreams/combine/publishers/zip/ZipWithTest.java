package com.specification.reactive.reactivestreams.combine.publishers.zip;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class ZipWithTest {

    /**
     * zipWith(): The zipWith() executes the same method that zip does, but only with two publishers.
     *
     * Mono.zipWith() use cases:
     *  1. Combining Results from Two Asynchronous Operations. When you have two independent asynchronous operations, and you want to combine their results, Mono.zipWith() can be handy.
     *  2. Error Handling: You can use Mono.zipWith() to combine a successful result with an error handling mechanism. If one Mono fails, you can still proceed with the other.
     *  3. Parallel Processing: When you need to perform multiple operations in parallel and combine their results, Mono.zipWith() is useful.
     *  4. Combining Different Types: You can combine Mono instances of different types into a single Mono that emits a composite object.
     *  5. Conditional Execution: You can use Mono.zipWith() to conditionally execute operations based on the results of other Mono instances.
     * */

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

    /**
     * Mono.zip(Mono<Boolean>, Mono<Boolean>, Boolean::logicalAnd):
     * Combines the two Mono<Boolean> values.
     * Applies Boolean::logicalAnd to their results.
     * Emits a single Boolean value (true only if both are true)
     * */
    @Test
    public void conditional_execution() {
        Mono<Boolean> isTrue = Mono.just(true);
        Mono<Boolean> isFalse = Mono.just(false);

        Mono<String> failureResult = Mono.zip(isTrue, isFalse, Boolean::logicalAnd)
                .flatMap(canExecute -> {
                    if (canExecute) {
                        return restApiCall();
                    } else {
                        return Mono.just("Condition not met, skipping API call.");
                    }
                });

        StepVerifier.create(failureResult)
                .expectSubscription()
                .expectNext("Condition not met, skipping API call.")
                .verifyComplete();


        Mono<String> successResult = Mono.zip(isTrue, isTrue, Boolean::logicalAnd)
                .flatMap(canExecute -> {
                    if (canExecute) {
                        return restApiCall();
                    } else {
                        return Mono.just("Condition not met, skipping API call.");
                    }
                });

        StepVerifier.create(successResult)
                .expectSubscription()
                .expectNext("Rest Api call executed!")
                .verifyComplete();
    }

    private Mono<String> restApiCall() {
        // Simulate a REST API call
        return Mono.just("Rest Api call executed!");
    }
}

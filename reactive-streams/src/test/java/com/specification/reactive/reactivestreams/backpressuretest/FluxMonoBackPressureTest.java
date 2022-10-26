package com.specification.reactive.reactivestreams.backpressuretest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxMonoBackPressureTest {

    @Test
    public void flux_backPressure_test() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .thenRequest(2)
                .thenRequest(1)
                .thenRequest(3)
                .thenCancel()
                .verify();
    }

    @Test
    public void flux_backPressure_implementation_test() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        finiteFlux.subscribe((element) -> System.out.println("Element is: " + element),
                (e) -> System.err.println("Exception is: " + e),
                () -> System.out.println("On Completed"),
                subscription -> subscription.request(2));
    }

    @Test
    public void backPressure_cancelImplementation_test() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        finiteFlux.subscribe((element) -> System.out.println("Element is: " + element),
                (e) -> System.err.println("Exception is: " + e),
                () -> System.out.println("On Completed"),
                subscription -> subscription.cancel());
    }

    @Test
    public void customized_backPressure_test() {
        Flux<Integer> finiteFlux = Flux.range(1, 200).log();
        finiteFlux.subscribe(new BaseSubscriber<>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Value received: " + value);
                if (value.equals(101)) {
                    cancel();
                }
            }
        });
    }
}

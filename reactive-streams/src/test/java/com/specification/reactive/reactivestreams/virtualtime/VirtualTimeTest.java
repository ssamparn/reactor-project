package com.specification.reactive.reactivestreams.virtualtime;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {

    @Test
    void flux_publisher_virtualTime_test() {
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1)).take(3);

        StepVerifier.create(longFlux.log())
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    void flux_publisher_with_virtualTime_test() {
        VirtualTimeScheduler.getOrSet();

        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1)).take(3);

        StepVerifier.withVirtualTime(longFlux::log)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }
}

package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.subscriber.DefaultSubscriber;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class AsOperatorTest {

    /**
     * as(): The as() method in Project Reactor is used to wrap the current Publisher into a new Publisher type, without modifying the underlying stream.
     * It is used to:
     *   1. Convert a Flux (a stream of 0 to N elements) to a Mono (a stream of 0 or 1 element).
     *   2. Convert a Mono to a Flux.
     *   3. Convert both Mono and Flux into another reactive type using a custom transformer function.
     *   3. Adapt a Publisher to a different reactive library (e.g., RxJava).
     *   4. Convert a Publisher to a non-reactive stream (e.g., an Iterable).
     * */

    @Test
    public void asOperatorTest() {
        Flux.range(1, 10)
                .as(Flux::from) // Wrap original flux to another flux.
                .subscribe(new DefaultSubscriber<>("Flux Subscriber"));

        Mono.just(1)
                .as(Flux::from) // Wrap original mono to another flux.
                .subscribe(new DefaultSubscriber<>("Mono Subscriber"));
    }
}

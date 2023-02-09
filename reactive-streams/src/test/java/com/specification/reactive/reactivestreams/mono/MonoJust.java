package com.specification.reactive.reactivestreams.mono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoJust {

    @Test
    public void monoJustTest() {
        // Publisher
        Mono<Integer> integerMono = Mono.just(1);

        // Nothing happens until unless you subscribe to the publisher
        System.out.println(integerMono);

        // Subscribe to the publisher
        integerMono.subscribe(integer -> System.out.println("Received: " + integer));
    }
}

package com.specification.reactive.reactivestreams.mono;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

@Slf4j
public class MonoJust {

    @Test
    public void mono_just_test() {
        // Publisher
        Mono<Integer> integerMono = Mono.just(1);

        // Nothing happens until unless you subscribe to the publisher
        System.out.println(integerMono);

        // Subscribe to the publisher
        integerMono.subscribe(integer -> log.info("Received: {}", integer));
    }
}

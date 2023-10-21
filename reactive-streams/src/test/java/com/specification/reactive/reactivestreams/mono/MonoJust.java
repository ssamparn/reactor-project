package com.specification.reactive.reactivestreams.mono;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class MonoJust {

    /**
     * Mono is a special type of Publisher.
     * A Mono object represents a single or empty value.
     * This means it can only emit one value at most for the onNext() request and then terminates with the onComplete() signal.
     * In case of failure, it only emits a single onError() signal.
     * */

    @Test
    public void given_mono_publisher_when_subscribe_then_return_single_value() {
        Mono<String> helloMono = Mono.just("Hello");

        StepVerifier.create(helloMono)
                .expectNext("Hello")
                .expectComplete()
                .verify();
    }

    @Test
    public void mono_just_test() {
        // Publisher
        Mono<Integer> integerMono = Mono.just(1);

        // Nothing happens until unless you subscribe to the publisher.
        // This works in a similar way like the terminal operation while evaluating streams. Stream will not be evaluated until unless the terminal operation is invoked.
        System.out.println(integerMono);

        // Subscribe to the publisher in order to access data from a publisher.
        // Once you subscribe, then only the publisher will emit events / data.
        integerMono.subscribe(integer -> log.info("Received: {}", integer));
    }


}

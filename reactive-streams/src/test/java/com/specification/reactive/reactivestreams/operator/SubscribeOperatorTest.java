package com.specification.reactive.reactivestreams.operator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class SubscribeOperatorTest {

    // We can use this overloaded version of subscribe method. It is same as passing subscribe(null, null, null)
    // Here we are providing all the callback handlers like doOnNext(), doOnComplete() and doOnError() instead of providing it in a Subscriber implementation.
    // e.g: new DefaultSubscriber<>()

    @Test
    public void subscribe_operator_test() {
        Flux.range(1, 5)
                .doOnNext(i -> log.info("received: {}", i))
                .doOnComplete(() -> log.info("complete"))
                .doOnError((err) -> log.error("error: {}", err.getMessage()))
                .subscribe();
    }
}
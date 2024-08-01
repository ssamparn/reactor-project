package com.specification.reactive.reactivestreams.publisher.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

@Slf4j
public class MonoSubscribe {

    @Test
    public void mono_subscribe_without_consumer_implementations_test() {
        // publisher
        Mono<String> stringMono = Mono.just("ball");

        // subscribe without consumer implementations
        stringMono.subscribe();
    }

    @Test
    public void mono_subscribe_with_simple_consumer_implementations_test() {
        // publisher
        Mono<String> stringMono = Mono.just("ball");

        // subscribe with consumer implementations
        stringMono.subscribe(i -> log.info("Received: {}", i));
    }

    @Test
    public void mono_subscribe_with_consumer_implementations_test() {
        // publisher
        Mono<String> stringMono = Mono.just("ball");

        // subscribe with consumer implementations
        stringMono.subscribe(
                RsUtil.onNext(),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }

    @Test
    public void mono_subscribe_with_default_subscriber_implementation_test() {
        // publisher
        Mono<String> stringMono = Mono.just("ball");

        // subscribe with consumer implementations
        stringMono.subscribe(RsUtil.subscriber("Ball Subscriber"));
    }


}

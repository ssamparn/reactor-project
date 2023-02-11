package com.specification.reactive.reactivestreams.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoSubscribeOnError {

    @Test
    public void mono_subscribeOnError_test() {
        // publisher
        Mono<Integer> stringMono = Mono.just("ball")
                .map(String::length)
                        .map(len -> len / 0);

        // 1: Subscribe
        stringMono.subscribe();

        // 2: Subscribe with Consumer Implementations
        stringMono.subscribe(
                RsUtil.onNext(),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }
}

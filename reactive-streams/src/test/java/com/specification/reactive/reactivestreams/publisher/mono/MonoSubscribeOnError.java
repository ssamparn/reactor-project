package com.specification.reactive.reactivestreams.publisher.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoSubscribeOnError {

    @Test
    public void mono_subscribe_on_error_test() {
        // publisher
        Mono<Integer> stringMono = Mono.just("ball")
                .map(String::length)
                .map(len -> len / 0);

        // subscribe with Consumer Implementations
        stringMono.subscribe(
                RsUtil.onNext(),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }
}

package com.specification.reactive.reactivestreams.mono;

import com.specification.reactive.reactivestreams.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoSubscribeOnError {

    @Test
    public void monoSubscribeOnErrorTest() {
        // publisher
        Mono<Integer> stringMono = Mono.just("ball")
                .map(String::length)
                        .map(len -> len / 0);

        // 1: Subscribe
        stringMono.subscribe();

        // 2: Subscribe with Consumer Implementations
        stringMono.subscribe(
                ReactiveSpecificationUtil.onNext(),
                ReactiveSpecificationUtil.onError(),
                ReactiveSpecificationUtil.onComplete()
        );
    }
}

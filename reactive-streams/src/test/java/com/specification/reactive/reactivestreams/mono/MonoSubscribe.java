package com.specification.reactive.reactivestreams.mono;

import com.specification.reactive.reactivestreams.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoSubscribe {

    @Test
    public void monoSubscribe() {
        // publisher
        Mono<String> stringMono = Mono.just("ball");

        // 1: Subscribe
        stringMono.subscribe();

        // 2: Subscribe with Consumer Implementations
        stringMono.subscribe(ReactiveSpecificationUtil.onNext(),
                ReactiveSpecificationUtil.onError(),
                ReactiveSpecificationUtil.onComplete()
        );
    }
}

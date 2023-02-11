package com.specification.reactive.reactivestreams.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoSubscribe {

    @Test
    public void mono_subscribe_test() {
        // publisher
        Mono<String> stringMono = Mono.just("ball");

        // 1: Subscribe
        stringMono.subscribe();

        // 2: Subscribe with Consumer Implementations
        stringMono.subscribe(RsUtil.onNext(),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }
}

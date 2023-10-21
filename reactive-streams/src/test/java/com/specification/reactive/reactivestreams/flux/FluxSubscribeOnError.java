package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxSubscribeOnError {

    @Test
    public void flux_subscribe_on_error_test() {
        // publisher
        Flux<Integer> integerFlux = Flux.range(1,5)
                .map(integer -> {
                    if (integer <= 3) return integer;
                    throw new RuntimeException("Got to 4");
                });

        // subscribe with Consumer Implementations
        integerFlux.subscribe(
                RsUtil.onNext(),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }
}

package com.specification.reactive.flux;

import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxSubscribeOnError {

    @Test
    public void fluxSubscribeOnErrorTest() {
        // publisher
        Flux<Integer> integerFlux = Flux.range(1,5)
                .map(integer -> {
                    if (integer <= 3) return integer;
                    throw new RuntimeException("Got to 4");
                });

        // 2: Subscribe with Consumer Implementations
        integerFlux.subscribe(
                ReactiveSpecificationUtil.onNext(),
                ReactiveSpecificationUtil.onError(),
                ReactiveSpecificationUtil.onComplete()
        );
    }
}

package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxRange {

    // Flux.range(): range() of Flux acts like a simple for loop.
    @Test
    public void flux_range_integer_Test() {
        Flux<Integer> integerFlux = Flux.range(1, 10);

        integerFlux.subscribe(
                RsUtil.onNext(),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }

    @Test
    public void flux_range_name_test() {
        Flux.range(1, 10)
                .log()
                .map(i -> i + " : " + RsUtil.faker().name().fullName()) // Subscriber 2
                .log()
                .subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete()); // Subscriber 1
    }
}

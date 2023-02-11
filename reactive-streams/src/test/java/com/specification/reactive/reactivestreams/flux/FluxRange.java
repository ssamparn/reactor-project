package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxRange {

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
        Flux<String> nameFlux = Flux.range(1, 10)
                .log()
                .map(integer -> integer + " : " + RsUtil.faker().name().fullName())
                .log();

        nameFlux.subscribe(
                RsUtil.onNext(),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }
}

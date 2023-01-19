package com.specification.reactive.flux;

import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxRange {

    @Test
    public void fluxRangeIntegerTest() {
        Flux<Integer> integerFlux = Flux.range(1, 10);

        integerFlux.subscribe(
                ReactiveSpecificationUtil.onNext(),
                ReactiveSpecificationUtil.onError(),
                ReactiveSpecificationUtil.onComplete()
        );
    }

    @Test
    public void fluxRangeNameTest() {
        Flux<String> nameFlux = Flux.range(1, 10)
                .log()
                .map(integer -> integer + " : " + ReactiveSpecificationUtil.faker().name().fullName())
                .log();

        nameFlux.subscribe(
                ReactiveSpecificationUtil.onNext(),
                ReactiveSpecificationUtil.onError(),
                ReactiveSpecificationUtil.onComplete()
        );
    }
}

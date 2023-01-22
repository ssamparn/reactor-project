package com.specification.reactive.flux;

import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FluxFromMonoPublisher {

    @Test
    public void fluxFromMonoPublisherTest() {
        Mono<String> stringMono = Mono.just("a");

        Flux<String> stringFlux = Flux.from(stringMono);

        stringFlux.subscribe(ReactiveSpecificationUtil.onNext());
    }

    @Test
    public void fluxToMonoPublisherTest() {
        Flux<Integer> integerFlux = Flux.range(1, 10);
        integerFlux
                .filter(item -> item > 3)
                .next()
                .subscribe(ReactiveSpecificationUtil.onNext());
    }


}

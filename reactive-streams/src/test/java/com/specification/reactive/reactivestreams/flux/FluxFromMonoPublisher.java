package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FluxFromMonoPublisher {

    @Test
    public void flux_from_monoPublisher_test() {
        Mono<String> stringMono = Mono.just("a");

        Flux<String> stringFlux = Flux.from(stringMono);

        stringFlux.subscribe(RsUtil.onNext());
    }

    @Test
    public void flux_to_monoPublisher_test() {
        Flux<Integer> integerFlux = Flux.range(1, 10);
        integerFlux
                .filter(item -> item > 3)
                .next()
                .subscribe(RsUtil.onNext());
    }


}

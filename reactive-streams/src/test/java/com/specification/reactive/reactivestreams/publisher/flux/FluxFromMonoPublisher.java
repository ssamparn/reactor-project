package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FluxFromMonoPublisher {

    @Test
    public void flux_from_mono_publisher_test() {
        Mono<String> stringMono = Mono.just("a");

        Flux<String> stringFlux = Flux.from(stringMono);

        stringFlux.subscribe(RsUtil.onNext());
    }

    @Test
    public void flux_to_mono_publisher_test() {
        Flux<Integer> integerFlux = Flux.range(1, 10);
        Mono<Integer> integerMono = integerFlux
                .filter(item -> item > 3)
                .next();  // next() will return a Mono<Integer> from a FLux<Integer>

        integerMono.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
    }
}

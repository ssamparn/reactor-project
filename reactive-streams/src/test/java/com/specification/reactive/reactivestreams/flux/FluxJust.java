package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxJust {

    @Test
    public void flux_just_test() {
        Flux<String> nameFlux = Flux.just("Sam", "Harry", "Bapun");

        nameFlux.subscribe(
                RsUtil.onNext(),
                RsUtil.onError(),
                RsUtil.onComplete()
        );

        StepVerifier.create(nameFlux)
                .expectNext("Sam")
                .expectNext("Harry")
                .expectNext("Bapun")
                .verifyComplete();
    }

    @Test
    public void empty_flux_test() {
        Flux<String> emptyFlux = Flux.empty();

        emptyFlux.subscribe(
                RsUtil.onNext(),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }

    @Test
    public void object_flux_test() {
        Flux<Object> objectFlux = Flux.just(1, 2, "Sam", "Harry", 3, "Bapun", "a", RsUtil.faker().name().fullName());

        objectFlux.subscribe(
                RsUtil.onNext(),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }

    @Test
    public void flux_foo_test() {
        Flux<String> fluxSequence = Flux.just("foo", "bar", "foobar");

        StepVerifier.create(fluxSequence)
                .expectNext("foo")
                .expectNext("bar")
                .expectNext("foobar")
                .verifyComplete();
    }
}

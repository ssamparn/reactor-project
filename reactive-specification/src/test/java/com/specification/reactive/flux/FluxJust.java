package com.specification.reactive.flux;

import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxJust {

    @Test
    public void fluxJustTest() {
        Flux<String> nameFlux = Flux.just("Sam", "Harry", "Bapun");

        nameFlux.subscribe(
                ReactiveSpecificationUtil.onNext(),
                ReactiveSpecificationUtil.onError(),
                ReactiveSpecificationUtil.onComplete()
        );

        StepVerifier.create(nameFlux)
                .expectNext("Sam")
                .expectNext("Harry")
                .expectNext("Bapun")
                .verifyComplete();
    }

    @Test
    public void emptyFluxTest() {
        Flux<String> emptyFlux = Flux.empty();

        emptyFlux.subscribe(
                ReactiveSpecificationUtil.onNext(),
                ReactiveSpecificationUtil.onError(),
                ReactiveSpecificationUtil.onComplete()
        );
    }

    @Test
    public void objectFluxTest() {
        Flux<Object> objectFlux = Flux.just(1, 2, "Sam", "Harry", 3, "Bapun", "a", ReactiveSpecificationUtil.faker().name().fullName());

        objectFlux.subscribe(
                ReactiveSpecificationUtil.onNext(),
                ReactiveSpecificationUtil.onError(),
                ReactiveSpecificationUtil.onComplete()
        );
    }

    @Test
    public void fluxFooTest() {
        Flux<String> fluxSequence = Flux.just("foo", "bar", "foobar");

        StepVerifier.create(fluxSequence)
                .expectNext("foo")
                .expectNext("bar")
                .expectNext("foobar")
                .verifyComplete();
    }
}

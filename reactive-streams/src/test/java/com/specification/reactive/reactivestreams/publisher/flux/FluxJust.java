package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxJust {

    /**
     * Flux is a standard Publisher that represents 0 to N asynchronous sequence values.
     * This means that it can emit 0 to many values, possibly infinite values for onNext() requests, and then terminates with either a completion or an error signal.
     * */

    /**
     * Mono vs. Flux:
     *   Mono and Flux are both implementations of the Publisher interface.
     *   In simple terms, we can say that when we’re doing something like a computation or making a request to a database or an external service, and expecting a maximum of one result, then we should use Mono.
     *   When we’re expecting multiple results from our computation, database, or external service call, then we should use Flux.
     *   Mono is more relatable to the Optional class in Java since it contains 0 or 1 value.
     *   Flux is more relatable to List / Stream since it can have N number of values.
     * */
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

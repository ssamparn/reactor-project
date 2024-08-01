package com.specification.reactive.reactivestreams.publisher.mono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class MonoTest {

    @Test
    public void mono_simple_test() {
        Mono<String> stringMono = Mono.just("Spring")
                .log();

        StepVerifier.create(stringMono)
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void mono_error_test() {
        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred"))
                .log())
                .expectError(RuntimeException.class)
                .verify();
    }
}

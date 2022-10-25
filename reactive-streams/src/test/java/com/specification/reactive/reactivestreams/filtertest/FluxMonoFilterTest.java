package com.specification.reactive.reactivestreams.filtertest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxMonoFilterTest {

    @Test
    public void filterTest() {
        List<String> stringList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

        Flux<String> stringFluxFromIterable = Flux.fromIterable(stringList)
                .filter(name -> name.startsWith("A")).log();

        StepVerifier.create(stringFluxFromIterable)
                .expectNext("Adam")
                .expectNext("Anna")
                .verifyComplete();
    }

    @Test
    public void filterTest_Length() {
        List<String> stringList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

        Flux<String> stringFluxFromIterable = Flux.fromIterable(stringList)
                .filter(name -> name.length() > 4).log();

        StepVerifier.create(stringFluxFromIterable)
                .expectNext("Jenny")
                .verifyComplete();
    }
}

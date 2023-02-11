package com.specification.reactive.reactivestreams.operator;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FilterOperatorTest {

    @Test
    public void flux_filter_test() {
        List<String> stringList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

        Flux<String> stringFluxFromIterable = Flux.fromIterable(stringList)
                .filter(name -> name.startsWith("A"))
                .map(name -> name.length() + "-" + name)
                .log();

        StepVerifier.create(stringFluxFromIterable)
                .expectNext("4-Adam")
                .expectNext("4-Anna")
                .verifyComplete();
    }

    @Test
    public void flux_filter_length_test() {
        List<String> stringList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

        Flux<String> stringFluxFromIterable = Flux.fromIterable(stringList)
                .filter(name -> name.length() > 4)
                .map(String::toUpperCase)
                .log();

        StepVerifier.create(stringFluxFromIterable)
                .expectNext("JENNY")
                .verifyComplete();
    }

    @Test
    public void flux_filter_add_length_test() {
        List<String> stringList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

        Flux<String> stringFluxFromIterable = Flux.fromIterable(stringList)
                .filter(name -> name.length() == 4)
                .map(name -> name.length() + "-" + name)
                .log();

        StepVerifier.create(stringFluxFromIterable)
                .expectNext("4-Adam")
                .expectNext("4-Anna")
                .expectNext("4-Jack")
                .verifyComplete();
    }
}

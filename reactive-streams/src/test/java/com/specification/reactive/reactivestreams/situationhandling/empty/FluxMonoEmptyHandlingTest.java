package com.specification.reactive.reactivestreams.situationhandling.empty;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.specification.reactive.reactivestreams.util.TestUtil.splitStringWithDelay;

public class FluxMonoEmptyHandlingTest {

    @Test
    public void flux_publisher_returns_empty_events_defaultIfEmpty_test() {

        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny", "Krissy");

        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .map(String::toUpperCase)
                .filter(element -> element.length() > 6)
                .flatMap(element -> Flux.fromIterable(splitStringWithDelay(element)))
                .defaultIfEmpty("default_value")
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("default_value")
                .verifyComplete();
    }

    @Test
    public void flux_publisher_returns_empty_events_switchIfEmpty_test() {

        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny", "Krissy");
        Flux<String> defaultFlux = Flux.just("default_value");

        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .map(String::toUpperCase)
                .filter(element -> element.length() > 6)
                .flatMap(element -> Flux.fromIterable(splitStringWithDelay(element)))
                .switchIfEmpty(defaultFlux)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("default_value")
                .verifyComplete();
    }

}

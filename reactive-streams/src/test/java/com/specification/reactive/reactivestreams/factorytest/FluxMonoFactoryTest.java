package com.specification.reactive.reactivestreams.factorytest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FluxMonoFactoryTest {

    @Test
    public void createFluxFromIterable() {

        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");
        Flux<String> stringFlux = Flux.fromIterable(nameList);

        StepVerifier.create(stringFlux.log())
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .verifyComplete();
    }

    @Test
    public void createFluxFromArray() {
        String[] stringArray = new String[] {"Adam", "Anna",  "Jack", "Jenny"};
        Flux<String> stringFluxFromArray = Flux.fromArray(stringArray);

        StepVerifier.create(stringFluxFromArray.log())
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .verifyComplete();

    }

    @Test
    public void createFluxFromJavaStream() {
        Stream<String> stringStream = Arrays.asList("Adam", "Anna", "Jack", "Jenny").stream();

        Flux<String> stringFluxFromStream = Flux.fromStream(stringStream);

        StepVerifier.create(stringFluxFromStream.log())
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .verifyComplete();
    }


    @Test
    public void createFluxUsingRange() {
        Flux<Integer> integerFluxFromRange = Flux.range(1, 5);

        StepVerifier.create(integerFluxFromRange.log())
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    public void createMonoUsingJustOrEmpty() {
        Mono<Object> objectMono = Mono.justOrEmpty(null);

        StepVerifier.create(objectMono.log())
                .verifyComplete();
    }

    @Test
    public void createMonoUsingSupplier() {
        Supplier<String> stringSupplier = () -> "Adam";
        System.out.println("Invoking Functional method from Supplier: " +stringSupplier.get());

        Mono<String> stringMonoFromSupplier = Mono.fromSupplier(stringSupplier);

        StepVerifier.create(stringMonoFromSupplier.log())
                .expectNext("Adam")
                .verifyComplete();
    }

}

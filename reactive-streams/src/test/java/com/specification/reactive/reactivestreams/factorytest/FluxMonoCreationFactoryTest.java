package com.specification.reactive.reactivestreams.factorytest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FluxMonoCreationFactoryTest {

    @Test
    public void createFlux_from_Iterable_test() {

        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");
        Flux<String> nameFlux = Flux.fromIterable(nameList);

        StepVerifier.create(nameFlux.log())
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .verifyComplete();
    }

    @Test
    public void createFlux_from_Array_test() {
        String[] stringArray = new String[] {"Adam", "Anna",  "Jack", "Jenny"};
        Flux<String> stringFluxFromArray = Flux.fromArray(stringArray);

        StepVerifier.create(stringFluxFromArray.log())
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .verifyComplete();
    }

    @Test
    public void createFlux_from_JavaStream_test() {
        Stream<String> stringStream = Arrays.asList("Adam", "Anna", "Jack", "Jenny").stream();

        Flux<String> stringFluxFromStream = Flux.fromStream(stringStream);

        StepVerifier.create(stringFluxFromStream.log())
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .verifyComplete();
    }


    @Test
    public void createFlux_using_Range_test() {
        Flux<Integer> integerFluxFromRange = Flux.range(1, 5);

        StepVerifier.create(integerFluxFromRange.log())
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    public void createMono_using_justOrEmpty_test() {
        Mono<Object> objectMono = Mono.justOrEmpty(null);

        StepVerifier.create(objectMono.log())
                .verifyComplete();
    }

    @Test
    public void createMono_using_Supplier_test() {
        Supplier<String> stringSupplier = () -> "Adam";
        System.out.println("Invoking Functional method from Supplier: " + stringSupplier.get());

        Mono<String> stringMonoFromSupplier = Mono.fromSupplier(stringSupplier);

        StepVerifier.create(stringMonoFromSupplier.log())
                .expectNext("Adam")
                .verifyComplete();
    }

}

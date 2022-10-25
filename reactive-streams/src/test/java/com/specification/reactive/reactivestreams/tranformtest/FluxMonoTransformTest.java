package com.specification.reactive.reactivestreams.tranformtest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxMonoTransformTest {

    @Test
    public void transformUsingMap() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .map(name -> name.toUpperCase())
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();

    }

    @Test
    public void transformUsingMap_GetLength() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

        Flux<Integer> stringFlux = Flux.fromIterable(nameList)
                .map(name -> name.length())
                .log();

        StepVerifier.create(stringFlux)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_GetLength_Repeat() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

        Flux<Integer> stringFlux = Flux.fromIterable(nameList)
                .map(name -> name.length())
                .repeat(1)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_GetLength_Filter_Map() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .filter(name -> name.length() > 4)
                .map(name -> name.toUpperCase())
                .repeat(1)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("JENNY")
                .expectNext("JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny", "Max", "Krish");

        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .flatMap(element -> Flux.fromIterable(convertToList(element)))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_UsingParallel() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny", "Max", "Krish");

        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .window(3)
                .flatMap(element -> element.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(element -> Flux.fromIterable(element))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_UsingParallel_MaintainOrder_UsingConcatMap() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny", "Max", "Krish");

        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .window(3)
                .concatMap(element -> element.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(element -> Flux.fromIterable(element))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_UsingParallel_MaintainOrder_UsingFaltMapSequential() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny", "Max", "Krish");

        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .window(3)
                .flatMapSequential(element -> element.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(element -> Flux.fromIterable(element))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String element) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(element, "newValue");
    }

}

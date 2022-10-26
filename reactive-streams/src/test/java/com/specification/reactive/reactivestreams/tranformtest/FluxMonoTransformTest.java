package com.specification.reactive.reactivestreams.tranformtest;

import com.specification.reactive.reactivestreams.util.TestUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static com.specification.reactive.reactivestreams.util.TestUtil.convertToList;
import static com.specification.reactive.reactivestreams.util.TestUtil.splitStringWithDelay;
import static reactor.core.scheduler.Schedulers.parallel;

public class FluxMonoTransformTest {

    @Test
    public void monoTransformUsingFlatMap_EventsTest() {

        Mono<List<String>> stringMonoList = Mono.just("Alex")
                .map(String::toUpperCase)
                .flatMap(element -> Mono.just(List.of(element.split(""))))
                .log();

        StepVerifier.create(stringMonoList)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    public void monoTransformUsingFlatMapMany_EventsTest() {

        Flux<String> stringFlux = Mono.just("Alex")
                .map(String::toUpperCase)
                .flatMapMany(element -> Flux.fromIterable(splitStringWithDelay(element)))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    public void transformUsingMap() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .map(String::toUpperCase)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_GetLength() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

        Flux<Integer> stringFlux = Flux.fromIterable(nameList)
                .map(String::length)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_GetLength_Repeat() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

        Flux<Integer> stringFlux = Flux.fromIterable(nameList)
                .map(String::length)
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
                .map(String::toUpperCase)
                .repeat(1)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("JENNY")
                .expectNext("JENNY")
                .verifyComplete();
    }

    @Test
    public void namesFluxImmutabilityTest() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

        Flux<String> stringFlux = Flux.fromIterable(nameList);

        stringFlux
                .map(String::toUpperCase);

        StepVerifier.create(stringFlux)
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_EventsTest() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny", "Max", "Krish");

        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .flatMap(element -> Flux.fromIterable(convertToList(element)))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Adam")
                .expectNext("newValue")
                .expectNext("Anna")
                .expectNext("newValue")
                .expectNext("Jack")
                .expectNext("newValue")
                .expectNext("Jenny")
                .expectNext("newValue")
                .expectNext("Max")
                .expectNext("newValue")
                .expectNext("Krish")
                .expectNext("newValue")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_EventsCountTest() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny", "Max", "Krish");

        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .flatMap(element -> Flux.fromIterable(convertToList(element)))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_AsyncTest() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny", "Max");

        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .map(String::toUpperCase)
                .flatMap(element -> Flux.fromIterable(splitStringWithDelay(element)))
                .delayElements(Duration.ofMillis(new Random().nextInt(20)))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("A", "D", "A", "M", "A", "N", "N", "A", "J", "A", "C", "K", "J", "E", "N", "N", "Y", "M", "A", "X")
                .verifyComplete();
    }

    @Test
    public void transformUsingConcatMap_Test() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny", "Max");

        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .concatMap(element -> Flux.fromIterable(splitStringWithDelay(element)))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(20)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_UsingParallel() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny", "Max", "Krish");

        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .window(3)
                .flatMap(element -> element.map(TestUtil::convertToList).subscribeOn(parallel()))
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
                .concatMap(element -> element.map(TestUtil::convertToList).subscribeOn(parallel()))
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
                .flatMapSequential(element -> element.map(TestUtil::convertToList).subscribeOn(parallel()))
                .flatMap(element -> Flux.fromIterable(element))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformPublisherUsingTransform_EventsTest() {

        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(element -> element.length() > 4 && element.startsWith("K"))
                .flatMap(element -> Flux.fromIterable(splitStringWithDelay(element)));

        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny", "Krissy");

        Flux<String> stringFlux = Flux.fromIterable(nameList)
                .transform(filterMap)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("K", "R", "I", "S", "S", "Y")
                .verifyComplete();
    }




}

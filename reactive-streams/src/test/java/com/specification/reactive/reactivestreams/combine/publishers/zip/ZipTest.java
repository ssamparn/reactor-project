package com.specification.reactive.reactivestreams.combine.publishers.zip;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ZipTest {

//    The static method zip() agglutinates multiple sources together.
//    i.e. it waits for all the sources to emit one element and combines these elements into an output value (constructed by the provided combinator function).
//    The operator will continue doing so until any of the sources completes.
//    For example if there are 3 publishers, A, B and C and each of them have different number of events,
//    then the zipped flux will have events as the minimum of the events emitted by the 3 publishers.
//    i.e: countOf(zippedFlux) = minOf(events of A, events of B, events of C)

    @Test
    public void zip_car_count_test() {
        Flux.zip(getCarBody(), getCarEngine(), getCarTyres())
                .count()
                .subscribe(RsUtil.subscriber("Create Cars"));
        // Here only 2 cars can be created
    }

    @Test
    public void zip_car_map_test() {
        Flux.zip(getCarBody(), getCarEngine(), getCarTyres())
                .map(tuple -> tuple.getT1().concat("BMW") + " - " + tuple.getT2().concat("BMW Group") + " - " + tuple.getT3().concat("Apollo"))
                .subscribe(RsUtil.subscriber("Create Cars"));
    }

    private static Flux<String> getCarBody() {
        return Flux.range(1, 5)
                .map(i -> "Body of: ");
    }

    private static Flux<String> getCarEngine() {
        return Flux.range(1, 2)
                .map(i -> "Engine from: ");
    }

    private static Flux<String> getCarTyres() {
        return Flux.range(1, 10)
                .map(i -> "Tyres are of: ");
    }

    @Test
    public void flux_publisher_simple_zip_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C");
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona");

        Flux<String> mergedFlux = Flux.zip(alphabetFlux, nameFlux, (alphabet, name) -> alphabet + "-" + name)
                .log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A-Adam", "B-Jenny", "C-Mona")
                .verifyComplete();
    }

    @Test
    public void flux_publisher_zip_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C");
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona", "Aparna", "Sashank");

        Flux<String> mergedFlux = Flux.zip(alphabetFlux, nameFlux, (alphabet, name) -> alphabet + "-" + name)
                .log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A-Adam", "B-Jenny", "C-Mona") // Here only 3 items will be emitted as that is the minimum of the 2 publishers.
                .verifyComplete();
    }

    // Here is an example in which zipped flux is using a combinator function.
    @Test
    public void flux_publisher_zip_with_combinator_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C");
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona");
        Flux<String> numFlux = Flux.just("1", "2", "3");
        Flux<String> lengthFlux = Flux.just("4", "5", "4");
        Flux<String> cityFlux = Flux.just(RsUtil.faker().address().cityName(), RsUtil.faker().address().cityName());

        Flux<String> mergedFlux = Flux.zip(alphabetFlux, nameFlux, numFlux, lengthFlux, cityFlux)
                .map(tuple -> tuple.getT3() + "-" + tuple.getT1() + "-" + tuple.getT2() + "-" + tuple.getT4());

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("1-A-Adam-4")
                .expectNext("2-B-Jenny-5") // Only 2 events will be emitted.
                .verifyComplete();
    }

    // zipWith(): The zipWith() executes the same method that zip does, but only with two Publishers:

    @Test
    public void flux_publisher_simple_zipWith_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C");
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona");

        Flux<String> mergedFlux = alphabetFlux.zipWith(nameFlux, (alphabet, name) -> alphabet + "-" + name)
                .log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A-Adam", "B-Jenny", "C-Mona")
                .verifyComplete();
    }

    @Test
    public void mono_publisher_simple_zipWith_test() {
        Mono<String> alphabetMono = Mono.just("A");
        Mono<String> nameMono = Mono.just("Adam");

        Mono<String> mergedFlux = alphabetMono.zipWith(nameMono, (alphabet, name) -> alphabet + "-" + name)
                .log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A-Adam")
                .verifyComplete();
    }
}

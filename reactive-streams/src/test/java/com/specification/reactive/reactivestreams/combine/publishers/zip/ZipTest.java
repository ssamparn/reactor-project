package com.specification.reactive.reactivestreams.combine.publishers.zip;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

/* *
 * zip(): The static method zip() agglutinates multiple publishers together.
 * It waits for all the publishers to emit events and combines these elements into an output value (constructed by the provided combinator function).
 * The operator will continue doing so until any of the publishers completes. Until & unless one of the publisher does not emit any item, the execution will stop.
 * That means if one of the publisher emits an empty signal without emitting any events, the subscriber will receive a complete signal even if other publishers emit items.
 * So it's an ALL or NOTHING operation & all publishers will have to emit at least an item for it to work.
 *
 * For example if there are 3 publishers, A, B and C and each of them have different number of events,
 * then the zipped flux will have events as the minimum of the events emitted by the 3 publishers.
 * i.e: countOf(zippedFlux) = minOf(events of A, events of B, events of C)
 *
 * Difference between merge() and zip():
 * merge() operator on the other hand it's not like zip(). Which ever publisher emit events, subscriber will receive it. And in merge(), there is no combinator function. So no assembly required.
 *
 * Similarity between merge() and zip():
 *  - Like merge(), zip() will also subscribe to all the publishers at the same time.
 * */

@Slf4j
public class ZipTest {

    @Test
    public void zip_car_count_test() {
        Flux.zip(getCarBody(), getCarEngine(), getCarTyres())
                .count()
                .subscribe(RsUtil.subscriber("Create Cars"));
        // Here only 2 cars can be created, as there are only 2 engines available.

        RsUtil.sleepSeconds(2);
    }

    record Car (String carBody, String carEngine, String carTyres) {

    }

    @Test
    public void zip_car_map_test() {
        Flux.zip(getCarBody(), getCarEngine(), getCarTyres())
                .map(tuple -> new Car(tuple.getT1().concat(" is from BMW"), tuple.getT2().concat(" is of BMW Group") , tuple.getT3().concat(" are from Apollo")))
                .subscribe(RsUtil.subscriber("Create Cars"));
        // Here only 2 cars can be created, as there are only 2 engines available.
        RsUtil.sleepSeconds(2);
    }

    private static Flux<String> getCarBody() {
        return Flux.range(1, 5)
                .map(i -> "Body of car " + i)
                .delayElements(Duration.ofMillis(100));
    }

    private static Flux<String> getCarEngine() {
        return Flux.range(1, 2)
                .map(i -> "Engine of car " + i)
                .delayElements(Duration.ofMillis(200));
    }

    private static Flux<String> getCarTyres() {
        return Flux.range(1, 10)
                .map(i -> "Tyres of car " + i)
                .delayElements(Duration.ofMillis(150));
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
}

package com.specification.reactive.reactivestreams.combine.publishers.merge;

import com.specification.reactive.reactivestreams.service.AmericanFlightService;
import com.specification.reactive.reactivestreams.service.EmiratesFlightService;
import com.specification.reactive.reactivestreams.service.FlightService;
import com.specification.reactive.reactivestreams.service.QatarFlightService;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class MergeTest {

    // The merge function executes the merging of the data from Publisher sequences
    // contained in an array into an interleaved merged sequence:
    // concat and concatWith() are lazy subscription as opposed to merge in which the sources are subscribed eagerly.

    private FlightService qatarFlightService = new QatarFlightService();
    private FlightService emiratesFlightService = new EmiratesFlightService();
    private FlightService americanFlightService = new AmericanFlightService();

    @Test
    public void mergeFlightsTest() {
        Flux.merge(
                qatarFlightService.getFlights(),
                emiratesFlightService.getFlights(),
                americanFlightService.getFlights()
        ).subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(10);
    }

    @Test
    public void concatFlightsTest() {
        Flux.concat(
                qatarFlightService.getFlights(),
                emiratesFlightService.getFlights(),
                americanFlightService.getFlights()
        ).subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(10);
    }

    // Just compare the flight results of both merge and concat operators.
    // In concat or concatWith(), the results will be sequential as concat subscribe to publishers lazily
    // whereas in merge the results depends on whichever publisher emits the item first.

    @Test
    public void flux_publisher_merge_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C");
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona");

        Flux<String> mergedFlux = Flux.merge(alphabetFlux, nameFlux);
        // Here the behavior of merge is same as concat or concatWith
        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("Adam", "Jenny", "Mona")
                .verifyComplete();
    }

    @Test
    public void flux_publisher_merge_with_delayElements_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(200));
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona")
                .delayElements(Duration.ofMillis(125));

        Flux<String> mergedFlux = Flux.merge(alphabetFlux, nameFlux);

        // This is an useful test to actually understand the behavior of merge.
        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("Adam")
                .expectNext("A")
                .expectNext("Jenny")
                .expectNext("Mona")
                .expectNext("B")
                .expectNext("C")
                .verifyComplete();
    }

    @Test
    public void flux_publisher_mergeWith_delayElements_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(200));
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona")
                .delayElements(Duration.ofMillis(125));

        Flux<String> mergedFlux = alphabetFlux.mergeWith(nameFlux);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("Adam")
                .expectNext("A")
                .expectNext("Jenny")
                .expectNext("Mona")
                .expectNext("B")
                .expectNext("C")
                .verifyComplete();
    }

    @Test
    public void mono_publisher_merge_with_test() {
        Mono<String> alphabetMono = Mono.just("A")
                .delayElement(Duration.ofMillis(200));
        Mono<String> nameMono = Mono.just("Adam")
                .delayElement(Duration.ofMillis(100));

        Flux<String> mergedFlux = alphabetMono.mergeWith(nameMono);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("Adam")
                .expectNext("A")
                .verifyComplete();
    }


    // mergeSequential(): The mergeSequential method merges data from Publisher sequences provided in an array into an ordered merged sequence.
    // Unlike concat, sources are subscribed to eagerly.
    // Also, unlike merge, their emitted values are merged into the final sequence in subscription order of the publisher.

    @Test
    public void flux_publisher_mergeSquential_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(80));
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona")
                .delayElements(Duration.ofMillis(60));

        Flux<String> mergedFlux = Flux.mergeSequential(alphabetFlux, nameFlux);

        // Here delaying the emitted events does not have any effect on the sequence.
        // Only the subscription is eager.

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("Adam", "Jenny", "Mona")
                .verifyComplete();
    }

    @Test
    public void flux_publisher_merge_with_DelayElements_withVirtualTime_test() {
        VirtualTimeScheduler.getOrSet();

        Flux<String> alphabetFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofSeconds(1));
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona")
                .delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.merge(alphabetFlux, nameFlux);

        StepVerifier.withVirtualTime(mergedFlux::log)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNextCount(6)
                .verifyComplete();
    }

//    The mergeDelayError() merges data from Publisher sequences contained in an array into an interleaved merged sequence.
//    This is similar to concatDelayError(), only sources are subscribed to eagerly.
//    This variant of the static merge method will delay any error until after the rest of the merge backlog has been processed.

    @Test
    public void flux_publisher_mergeDelayError_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(80));

        Flux<String> errorFlux = Flux.error(new RuntimeException("Runtime Exception"));

        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona")
                .delayElements(Duration.ofMillis(60));

        Flux<String> mergedFlux = Flux.mergeDelayError(1, alphabetFlux, errorFlux, nameFlux);

        // Here delaying the emitted events does not have any effect on the sequence.
        // Only the subscription is eager.

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("Adam")
                .expectNext("A")
                .expectNext("Jenny")
                .expectNext("B")
                .expectNext("Mona")
                .expectNext("C")
                .expectError()
                .verify();
    }

}

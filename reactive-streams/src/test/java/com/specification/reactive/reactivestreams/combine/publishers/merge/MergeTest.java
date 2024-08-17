package com.specification.reactive.reactivestreams.combine.publishers.merge;

import com.specification.reactive.reactivestreams.service.AmericanFlightService;
import com.specification.reactive.reactivestreams.service.EmiratesFlightService;
import com.specification.reactive.reactivestreams.service.FlightService;
import com.specification.reactive.reactivestreams.service.QatarFlightService;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

/* *
 * merge(): Imagine we have 3 publishers. P1, P2, P3. Each emits events of type T. Flux<T>. We also have a subscriber S wants to subscribe to all 3 publishers.
 * Instead of subscribing to all these 3 publishers one after another (like we saw in startWith(): where subscription happens bottom to top & concatWith(): where subscription happens top to bottom),
 * We can merge or combine all 3 publishers into one single publisher, and the subscriber can subscribe to the merged publisher. When subscriber subscribes to this merged publisher, all 3 publishers are subscribed at the same time.
 * In which order the subscriber will receive events is not guaranteed. The publisher which emits events first, subscriber will receive it. If subscriber wants to cancel, all 3 publisher will receive the cancellation signal.
 *
 * V Imp Note: 1. The merge function executes the merging of the data from Publisher sequences contained in an array into an interleaved merged sequence
 *             2. concat and concatWith() are lazy subscription as opposed to merge in which the sources are subscribed eagerly.
 * */

@Slf4j
public class MergeTest {

    private FlightService qatarFlightService = new QatarFlightService();
    private FlightService emiratesFlightService = new EmiratesFlightService();
    private FlightService americanFlightService = new AmericanFlightService();

    @Test
    public void mergePublishersSimpleTest() {
        Flux.merge(intProducer(), intProducer2(), intProducer3()) // the order of placing publishers does not matter as all the publishers are subscribed at the same time.
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(1);
    }

    private static Flux<Integer> intProducer() {
        return Flux.just(1, 2, 3, 4, 5)
                .doOnSubscribe(i -> log.info("From Publisher 1 : {}", i))
                .delayElements(Duration.ofMillis(10));
    }

    private static Flux<Integer> intProducer2() {
        return Flux.just(10, 20, 30, 40, 50)
                .doOnSubscribe(i -> log.info("From Publisher 2 : {}", i))
                .delayElements(Duration.ofMillis(10));
    }

    private static Flux<Integer> intProducer3() {
        return Flux.just(100, 200, 300, 400, 500)
                .doOnSubscribe(i -> log.info("From Publisher 3 : {}", i))
                .delayElements(Duration.ofMillis(10));
    }

    /**
     * Just compare the flight search results of merge(), concat() and startWith() operators.
     * In concat(), concatWith() or startWith() the results will be sequential as they subscribe to publishers lazily whereas in merge the results depends on whichever publisher emits the events first.
     * */

    @Test
    public void mergeFlightsTest() {
        // Order of event emission will not be maintained.
        Flux.merge(
                qatarFlightService.getFlights(),
                emiratesFlightService.getFlights(),
                americanFlightService.getFlights()
        ).subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(5);
    }

    /**
     * Behavior of Flux.merge() is exactly same as Publisher.mergeWith()
     * */
    @Test
    public void mergeWithFlightsTest() {
        // Order of event emission will not be maintained.
        qatarFlightService.getFlights()
                .mergeWith(emiratesFlightService.getFlights())
                .mergeWith(americanFlightService.getFlights())
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(5);
    }

    @Test
    public void concatFlightsTest() {
        // Order of event emission will be maintained. So subscriber will first receive Qatar Flights, then Emirates flights and then American flights.
        Flux.concat(
                qatarFlightService.getFlights(),
                emiratesFlightService.getFlights(),
                americanFlightService.getFlights()
        ).subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(7);
    }

    @Test
    public void startWithFlightsTest() {
        // Order of event emission will be maintained. So subscriber will first receive American Flights, then Emirates flights and then Qatar flights.
        qatarFlightService.getFlights()
                .startWith(emiratesFlightService.getFlights())
                .startWith(americanFlightService.getFlights())
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(7);
    }

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

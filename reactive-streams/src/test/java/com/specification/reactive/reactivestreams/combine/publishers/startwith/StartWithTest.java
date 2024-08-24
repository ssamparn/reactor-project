package com.specification.reactive.reactivestreams.combine.publishers.startwith;

import com.specification.reactive.reactivestreams.service.NameProducer;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/* *
 * startWith(): startsWith() calls multiple subscribers in a specific order (bottom to top).
 * Let's imagine we have 2 publishers returning type T. That means two Flux<T>.
 * Using startWith() we can connect these 2 publishers as one single publisher and expose as one single publisher. Now subscriber can subscribe to these 2 publishers as if they are one.
 * From subscriber point of view, it is subscribing to a publisher Flux<T>.
 *
 *     Flux<T> startWith(Iterable<? extends T> iterable):  Prepend the given Iterable before this Flux sequence.
 *     Flux<T> startWith(Publisher<? extends T> publisher):  Prepend the given Publisher sequence to this Flux sequence.
 *     Flux<T> startWith(T... values):  Prepend the given values before this Flux sequence.
 *
 *  More Explanation:
 *
 *     P1: Flux<T>
 *          +
 *     P2: Flux<T>
 *
 *     P3 = P1.startsWith(P2). Events will start emitting from P2 publisher first. After P2 emits all the events, P1 will start emitting (if required).
 *
 * V Imp Note: Publisher preceding startWith() will emit events in a separate thread pool.
 * */

@Slf4j
public class StartWithTest {

    @Test
    public void startWithSimpleTest() {
        // Flux<T> startWith(T... values): It will prepend the given values before this Flux sequence.
        intProducer()
                .startWith(-2, -1, 0)
                .subscribe(RsUtil.subscriber());
        RsUtil.sleepSeconds(1);
    }

    @Test
    public void startWithTest() {
        Flux<Integer> integerFlux1 = Flux.just(1, 2, 3, 4)
                .doOnNext(i -> log.info("From Publisher 1 : {}", i));

        Flux<Integer> integerFlux2 = Flux.just(5, 6, 7, 8, 9, 10)
                .doOnNext(i -> log.info("From Publisher 2 : {}", i));

        integerFlux1.startWith(integerFlux2)
                .take(5)
                .subscribe(RsUtil.subscriber());

        // here events will not be emitted from Publisher 1 as subscriber needs 5 events and Publisher 2 has more than 5 events.
    }

    @Test
    public void startWithAnotherTest() {
        Flux<Integer> integerFlux1 = Flux.just(1, 2, 3, 4)
                .doOnNext(i -> log.info("From Publisher 1 : {}", i));

        Flux<Integer> integerFlux2 = Flux.just(5, 6, 7, 8, 9, 10)
                .doOnNext(i -> log.info("From Publisher 2 : {}", i));

        integerFlux2.startWith(integerFlux1)
                .take(5)
                .subscribe(RsUtil.subscriber());
        // here all 4 events be emitted from Publisher 1 and 1 event will be emitted from Publisher 2.
    }

    @Test
    public void multipleStartWithTest() {
        intProducer()
                .startWith(intProducer2())
                .startWith(10000)
                .subscribe(RsUtil.subscriber());
        RsUtil.sleepSeconds(1);
    }

    private static Flux<Integer> intProducer() {
        return Flux.just(1, 2, 3, 4, 5, 6, 7)
                .doOnSubscribe(i -> log.info("From Publisher 1 : {}", i))
                .delayElements(Duration.ofMillis(10));
    }

    private static Flux<Integer> intProducer2() {
        return Flux.just(10, 20, 30, 40, 50, 60, 70)
                .doOnSubscribe(i -> log.info("From Publisher 2 : {}", i))
                .delayElements(Duration.ofMillis(10));
    }

    /**
     * Flux.startWith(): Real life use case:
     * Let's subscribe to a service which provide names with a duration of 500ms.
     * And subscribe to this service with multiple subscribers.
     * */
    @Test
    public void combinePublisherStartWithTest() {
        NameProducer nameProducer = new NameProducer();

        // Subscriber - 1
        nameProducer
                .generateNames()
                .take(3)
                .subscribe(RsUtil.subscriber("Subscriber-1"));

        // Subscriber - 2
        nameProducer
                .generateNames()
                .take(4) // Here the first 3 items will be received from the cache and rest 1 item will be generated fresh
                .subscribe(RsUtil.subscriber("Subscriber-2"));

        // Subscriber - 3
        nameProducer
                .generateNames()
                .take(5) // Here the first 4 items will be received from the cache and rest 1 item will be generated fresh
                .subscribe(RsUtil.subscriber("Subscriber-3"));


        // Subscriber - 4
        nameProducer
                .generateNames()
                .filter(name -> name.startsWith("B")) // If any of the previously emitted item starts with Z, then it will be retrieved from the cache otherwise new name will be generated fresh until it encounters a name starts with "B
                .take(1)
                .subscribe(RsUtil.subscriber("Subscriber-4"));
    }
}

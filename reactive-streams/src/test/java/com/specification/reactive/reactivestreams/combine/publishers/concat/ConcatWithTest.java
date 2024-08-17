package com.specification.reactive.reactivestreams.combine.publishers.concat;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

/* *
 * concat() & concatWith(): concatWith() is exactly the opposite of startWith(). It calls multiple subscribers in a specific order (top to bottom).
 * Let's imagine we have 2 publishers returning type T. That means two Flux<T>.
 * Using concat() or concatWith() we can connect these 2 publishers as one single publisher and expose as one single publisher. Now subscriber can subscribe to these 2 publishers as if they are one.
 * From subscriber point of view, it is subscribing to a publisher Flux<T>.
 *
 *     Flux<T> concatWith(Publisher<? extends T> other):  Concatenate emissions of this Flux with the provided Publisher (no interleave).
 *     Flux<T> concatWithValues(T... values):  Concatenates the values to the end of the Flux.
 *     static <T> Flux<T> concat(Iterable<? extends Publisher<? extends T>> sources):  Concatenate all sources provided in an Iterable, forwarding elements emitted by the sources downstream.
 *     static <T> Flux<T> concat(Publisher<? extends Publisher<? extends T>> sources):  Concatenate all sources emitted as an onNext signal from a parent Publisher, forwarding elements emitted by the sources downstream.
 *     static <T> Flux<T> concat(Publisher<? extends Publisher<? extends T>> sources, int prefetch):  Concatenate all sources emitted as an onNext signal from a parent Publisher, forwarding elements emitted by the sources downstream.
 *     static <T> Flux<T> concat(Publisher<? extends T>... sources):  Concatenate all sources provided as a vararg, forwarding elements emitted by the sources downstream.
 *
 *  More Explanation:
 *
 *     P1: Flux<T>
 *          +
 *     P2: Flux<T>
 *
 *     P3 = P1.concatWith(P2). Events will start emitting from P1 publisher first. After P1 emits all the events, P2 will start emitting (if required).
 *
 * Note 1: concat() and concatWith() are lazy subscriptions.
 * Note 2: The concatenation is achieved by sequentially subscribing to the first source then waiting for it to complete before subscribing to the next
 *         and so on until the last source completes. Any error interrupts the sequence immediately and is forwarded downstream to the subscriber.
 * */
@Slf4j
public class ConcatWithTest {

    @Test
    public void concatWithSimpleTest() {
        intProducer()
                .concatWithValues(-2, -1, 0)
                .subscribe(RsUtil.subscriber());
        RsUtil.sleepSeconds(1);
    }

    @Test
    public void multipleConcatWithTest() {
        intProducer()
                .concatWith(intProducer2())
                .concatWithValues(10000)
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

    @Test
    public void concatWithTest() {
        Flux<Integer> integerFlux1 = Flux.just(1, 2, 3, 4)
                .doOnNext(i -> log.info("From Publisher 1 : {}", i));

        Flux<Integer> integerFlux2 = Flux.just(5, 6, 7, 8, 9, 10)
                .doOnNext(i -> log.info("From Publisher 2 : {}", i));

        integerFlux1.concatWith(integerFlux2)
                .take(5)
                .subscribe(RsUtil.subscriber());

        // here 4 events will be emitted from Publisher 1 and since subscriber needs 5 elements, Publisher 2 will emit remaining 1 event.
    }

    @Test
    public void concatWithAnotherTest() {
        Flux<Integer> integerFlux1 = Flux.just(1, 2, 3, 4)
                .doOnNext(i -> log.info("From Publisher 1 : {}", i));

        Flux<Integer> integerFlux2 = Flux.just(5, 6, 7, 8, 9, 10)
                .doOnNext(i -> log.info("From Publisher 2 : {}", i));

        integerFlux2.concatWith(integerFlux1)
                .take(5)
                .subscribe(RsUtil.subscriber());
        // here all 5 events will be emitted from Publisher 2 and Publisher 1 will not be subscribed.
    }

    @Test
    public void combinePublisherConcatTest() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C", "D");
        Flux<String> nameFlux = Flux.just("Adam", "Bob", "Cathie", "Darwin"); // Will be emitted lazily after alphabetFlux.
        Flux<String> countryFlux = Flux.just("America", "Britain", "Canada", "Denmark"); // Will be emitted lazily after alphabetFlux and nameFlux

        Flux.concat(alphabetFlux, nameFlux, countryFlux) // All the events will be emitted sequentially one after another
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void combinePublisherConcatWithTest() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C", "D");
        Flux<String> nameFlux = Flux.just("Adam", "Bob", "Cathie", "Darwin"); // Will be emitted lazily after alphabetFlux.
        Flux<String> countryFlux = Flux.just("America", "Britain", "Canada", "Denmark"); // Will be emitted lazily after alphabetFlux and nameFlux

        alphabetFlux
                .concatWith(nameFlux)
                .concatWith(countryFlux) // All the events will be emitted sequentially one after another
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void combinePublisherConcatWithErrorTest() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C", "D");
        Flux<String> nameFlux = Flux.just("Adam", "Bob", "Cathie", "Darwin"); // Will be emitted lazily after alphabetFlux.
        Flux<String> countryFlux = Flux.just("America", "Britain", "Canada", "Denmark"); // Will be emitted lazily after alphabetFlux and nameFlux
        Flux<String> errorFlux = Flux.error(new RuntimeException("oops"));

        alphabetFlux
                .concatWith(errorFlux) // error got introduced in the pipeline
                .concatWith(nameFlux) // Here as the error occurred after the alphabet events gets emitted, it will stop the pipeline
                .concatWith(countryFlux) // So name and country flux will not be emitted. To overcome the above situation, use concatDelayError()
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void combinePublisherConcatDelayErrorTest() {

        Flux<String> alphabetFlux = Flux.just("A", "B", "C", "D");
        Flux<String> nameFlux = Flux.just("Adam", "Bob", "Cathie", "Darwin"); // Will be emitted lazily after alphabetFlux.
        Flux<String> countryFlux = Flux.just("America", "Britain", "Canada", "Denmark"); // Will be emitted lazily after alphabetFlux and nameFlux
        Flux<String> errorFlux = Flux.error(new RuntimeException("oops"));

        Flux.concatDelayError(
                alphabetFlux,
                errorFlux,      // error got introduced in the pipeline
                nameFlux,       // Here as the error occurred after the alphabet events gets emitted, it will stop the pipeline
                countryFlux
        ).subscribe(RsUtil.subscriber()); // As we have used concatDelayError, name and country flux will be emitted.
        // Error will be emitted at the very end.
    }

    @Test
    public void mono_publisher_concatWith_test() {
        Mono<String> alphabetMono1 = Mono.just("A");
        Mono<String> nameMono1 = Mono.just("Adam");
        Mono<String> alphabetMono2 = Mono.just("B");
        Mono<String> nameMono2 = Mono.just("Bram");

        Flux<String> mergedFlux = alphabetMono1
                .concatWith(nameMono1)
                .concatWith(alphabetMono2)
                .concatWith(nameMono2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A")
                .expectNext("Adam")
                .expectNext("B")
                .expectNext("Bram")
                .verifyComplete();
    }

    @Test
    public void flux_publisher_concat_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C");
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona");

        Flux<String> mergedFlux = Flux.concat(alphabetFlux, nameFlux);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("Adam", "Jenny", "Mona")
                .verifyComplete();
    }

    @Test
    public void flux_publisher_concatWith_test() {
        Flux<String> alphabetFlux = Flux.just("A", "B", "C");
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona");

        Flux<String> mergedFlux = alphabetFlux.concatWith(nameFlux);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("Adam", "Jenny", "Mona")
                .verifyComplete();
    }

    @Test
    public void flux_publisher_concatWith_delayElements_test() {

        Flux<String> alphabetFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofSeconds(1));
        Flux<String> nameFlux = Flux.just("Adam", "Jenny", "Mona")
                .delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.concat(alphabetFlux, nameFlux);
        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("Adam", "Jenny", "Mona")
                .verifyComplete();
    }
}

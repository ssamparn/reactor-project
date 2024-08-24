package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/* *
 * then(), thenEmpty() and thenMany():
 *
 *   All the thenXXX methods on Mono have one semantic in common. They ignore the source onNext() signals and react on completion or error signals (onComplete and onError), continuing the sequence at this point with various options.
 *   As a consequence, this can change the generic type of the returned Mono.
 *      1. then() will just replay the source terminal signal, resulting in a Mono<Void> to indicate that this never signals any onNext().
 *      2. thenEmpty() not only returns a Mono<Void>, but it takes a Mono<Void> as a parameter. It represents a concatenation of the source completion signal then the second, empty Mono completion signal. In other words, it completes when A then B have both completed sequentially, and doesn't emit data.
 *      3. thenMany() waits for the source to complete then plays all the signals from its Publisher<R> parameter, resulting in a Flux<R> that will "pause" until the source completes, then emit the many elements from the provided publisher before replaying its completion signal as well.
 * */

/* *
 * then(): Use then() when you are not interested in the result of a producer / chain or multiple asynchronous calls to execute one by one!
 * e.g: You are inserting a bunch of records into DB. You just need to know if the operation is successful or not. Not the intermediate results!
 * */
@Slf4j
public class ThenOperatorTest {

    @Test
    public void thenOperatorTest() {
        // We are not interested whether each row of records saved or not. We just care about the terminal signal onError() or onComplete().
        // then() returns a Mono<Void>. It will just give onComplete() or onError() signal.
        saveRecords(List.of("a", "b", "c"))
                .then()
                .subscribe(RsUtil.subscriber());
        RsUtil.sleepSeconds(2);
    }

    /**
     * then() is also useful when we want to execute multiple publishers in a specific order
     * */
    @Test
    public void thenOperatorWithMultiplePublisherTest() {
        // In the same above example, let's consider a scenario in which we have a send a notification after the all the records are saved
        List<String> records = List.of("a", "b", "c");

        saveRecords(records)
                .then(this.sendNotification(records)) // this step of then(sendNotification) will only start when saveRecords() completes successfully.
                .subscribe(RsUtil.subscriber());
        RsUtil.sleepSeconds(2);
    }

    private static Flux<String> saveRecords(List<String> records) {
        return Flux.fromIterable(records)
                .map(r -> "saved: " + r)
                .delayElements(Duration.ofMillis(500));
    }

    private Mono<Void> sendNotification(List<String> records) {
        return Mono.fromRunnable(() -> log.info("all these {} records saved successfully", records));
    }

    /**
     * then() is also used to execute a subsequent operation after completion of the current Mono or Flux publisher.
     * This returns a new Mono that emits a onComplete() signal when both the source Mono or Flux publishers completes.
     * */
    @Test
    public void thenOperatorExecuteSubsequentOperation() {
        Mono.just("Hello")
                .then(Mono.just("World")) // Mono.just("World") will only be executed when Mono.just("Hello") gets completed
                .subscribe(RsUtil.onNext());
    }

    /**
     * thenEmpty(): thenEmpty() is similar to the then() method, but there is a little bit of difference.
     * thenEmpty() ignores the previous result of Mono or Flux publisher, and can return empty value only.
     * */
    @Test
    public void thenEmptyOperatorTest() {
        Mono.just("Hello")
                .thenEmpty(Mono.empty())
                .subscribe(RsUtil.onNext());
    }

    /**
     * thenMany(): thenMany() is also similar to the then() method, but it is used with a Flux instead of Mono.
     * thenMany() waits for both the source Flux and the other Publisher to complete and then emits elements from the Flux publisher.
     * */
    @Test
    public void thenManyOperatorTest() {
        Flux.just(1, 2, 3)
                .thenMany(Flux.just("A", "B", "C")) // Flux.just("A", "B", "C") will only be executed when Flux.just(1, 2, 3) gets completed
                .subscribe(RsUtil.onNext());
    }

}

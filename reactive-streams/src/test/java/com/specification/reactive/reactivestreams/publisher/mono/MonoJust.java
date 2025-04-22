package com.specification.reactive.reactivestreams.publisher.mono;

import com.specification.reactive.reactivestreams.custom.subscriber.SubscriberImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class MonoJust {

    /* *
     * Mono is a special type of Publisher.
     * A Mono object represents a single(1) or empty(0) value.
     * This means it can only emit one value at most for the onNext() request and then terminates with the onComplete() signal.
     * In case of failure, it only emits a single onError() signal.
     * That means, Mono emits 0 or 1 item, followed by an onComplete() / onError() signal.
     * Note: Mono can emit 0 item as well, as it is not mandatory for a publisher to emit item.
     * */

    /* *
     * Use of just() in Mono: Why would someone create a publisher with just().
     * If you need a hot publisher.
     * Mono.just() returns a hot publisher whereas Mono.fromSupplier() returns a cold publisher.
     *
     * In reactive programming, anything can be a publisher and subscriber. We have to visualize well.
     * e.g: To read or query the database, database is the publisher & we (client application) is the subscriber.
     *      but to insert or update record into the database, database is the subscriber and client app will be the publisher.
     * You will have to visualize like this. In R2DBC, we have a save(). It accepts a Publisher. e.g: save (Publisher<T> data).
     * We can not pass data here directly. We have to pass a publisher. Hence, we are passing a Mono<T> here.
     * save (Mono.just("some-data"))
     * So when you have the data already in the memory, and for some reason you will have to create a publisher quickly using that data,
     * you can simply use the Mono.just() to make it as a publisher.
     * */

    @Test
    public void given_mono_publisher_when_subscribe_then_return_single_value() {
        Mono<String> helloMono = Mono.just("Hello");

        StepVerifier.create(helloMono)
                .expectNext("Hello")
                .expectComplete()
                .verify();
    }

    @Test
    public void mono_just_test() {
        // Reactive Publisher
        Mono<Integer> integerMono = Mono.just(1);

        // Nothing happens until unless you subscribe to the publisher.
        // This works in a similar way like the terminal operation while evaluating streams.
        // Stream will not be evaluated until unless the terminal operation is invoked.
        System.out.println(integerMono);

        // Subscribe to the publisher in order to access data from a publisher.
        // Once you subscribe, then only the publisher will emit events / data.
        integerMono.subscribe(integer -> log.info("Received: {}", integer));
    }

    @Test
    public void given_mono_publisher_when_subscribed_then_return_single_value() {
        Mono<String> stringMono = Mono.just("sashank");

        SubscriberImpl subscriber = new SubscriberImpl();

        stringMono.subscribe(subscriber);

        subscriber.getSubscription().request(10); // onComplete signal will be emitted even if we requested for multiple items.
    }
}

package com.specification.reactive.reactivestreams.sinks;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;
import reactor.util.concurrent.Queues;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class SinksTest {

    // With Sink we can emit signals manually. As and when we want.
    // Once created, a sink can act as both a publisher and subscriber.
    // It's a processor. Project Reactor had a processor but that is deprecated.
    // Sinks are better alternative to the processor.
    // A sink can be exposed as a flux or mono (publisher).

    /**
     * Sink Types
     * <pre>
     | Type            |    Bahavior    |    Pub:Sub Model |
     |-----------------|----------------|---------------------
     | one             |     Mono       |         1:N      |
     | many-unicast    |     Flux       |         1:1      |
     | many-multicast  |     Flux       |         1:N      |
     | many-replay     |     Flux       |   1:N (with replay of all values to late subscribers)|
     *</pre>
     */

    // Let's create a mono sink .
    // A mono sink can emit 1 item / empty / error event.
    @Test
    public void sink_one_try_emit_value_test() {

        Sinks.One<Object> unoSink = Sinks.one();
        unoSink.tryEmitValue(1);   // Here sink acts as a subscriber

        Mono<Object> unoSinkMono = unoSink.asMono(); // Here sink acts as a publisher

        StepVerifier.create(unoSinkMono)   // Since its a mono, as soon as we subscribe, it also sent an onComplete() signal.
                .expectSubscription()
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    public void sink_one_try_emit_empty_test() {

        Sinks.One<Object> emptySink = Sinks.one();
        emptySink.tryEmitEmpty();

        Mono<Object> emptySinkMono = emptySink.asMono();

        StepVerifier.create(emptySinkMono)
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    public void sink_one_try_emit_error_test() {

        Sinks.One<Object> errorSink = Sinks.one();
        errorSink.tryEmitError(new RuntimeException("sink error"));

        Mono<Object> errorSinkMono = errorSink.asMono();

        StepVerifier.create(errorSinkMono)
                .expectSubscription()
                .expectError()
                .verify();
    }

    @Test
    public void sink_one_emit_value_test() {

        Sinks.One<Object> sink = Sinks.one();
        sink.emitValue("hi", (signalType, emitResult) -> { // This works as an error handler. This works like while trying to emit hi, in case of error then you can retry based on a boolean value
            log.info("Signal Type: {}", signalType.name());
            log.info("Emit result: {}", emitResult.name());
            return false; // returning false means do not retry in case of error.
        });

        Mono<Object> sinkMono = sink.asMono();

        StepVerifier.create(sinkMono)
                .expectSubscription()
                .expectNext("hi")
                .verifyComplete();
    }

    @Test
    public void sink_one_emit_value_with_error_test() {

        Sinks.One<Object> sink = Sinks.one();
        Mono<Object> sinkMono = sink.asMono();

        sink.emitValue("hi", (signalType, emitResult) -> { // This works as an error handler. This works like while trying to emit hi, in case of error then you can retry based on a boolean value
            log.info("Signal Type: {}", signalType.name());
            log.info("Emit result: {}", emitResult.name());
            return false;
        });

        sink.emitValue("hello", (signalType, emitResult) -> { // Lets try to emit one more value from mono which is not possible as per the reactor specification. Just to demo an error scenario.
            log.info("Signal Type: {}", signalType.name());
            log.info("Emit result: {}", emitResult.name());
//            return true; // returning true means it will keep on retry in case of error. It will go in an infinite loop.
            return false;
        });

        sinkMono.subscribe(RsUtil.subscriber());
    }

    // V.Imp Note: With Sink of Type one, the behavior is of Mono and pub:sub is 1:N
    @Test
    public void sink_one_emit_value_with_multiple_subscribers_test() {
        Sinks.One<Object> sink = Sinks.one();
        Mono<Object> sinkMono = sink.asMono();

        sinkMono.subscribe(RsUtil.subscriber("Sashank"));
        sinkMono.subscribe(RsUtil.subscriber("Aparna"));
        // Both Aparna and Sashank will receive Hello. So you can have n number of subscribers and each of them will get events.

        sink.tryEmitValue("Hello");
    }

    // V.Imp Note: With Sink of Type many unicast, the behavior is of Flux and pub:sub is 1:1

    @Test
    void sink_unicast_try_emit_next_test() {
        // Handle through which we would push items
        Sinks.Many<String> integerSink = Sinks.many().unicast().onBackpressureBuffer(); // Since we are going to publish multiple values, we are going to handle the back pressure.

        // Handle through which subscribers will receive items
        Flux<String> integerFlux = integerSink.asFlux();

        integerFlux.subscribe(RsUtil.subscriber("Sam"));

        integerSink.tryEmitNext("Hi");
        integerSink.tryEmitNext("How are you ? ");
    }

    @Test
    void sink_unicast_try_emit_next_multiple_subscriber_test() {
        // Handle through which we would push items
        Sinks.Many<String> integerSink = Sinks.many().unicast().onBackpressureBuffer(); // Since we are going to publish multiple values, we are going to handle the back pressure.

        // Handle through which subscribers will receive items
        Flux<String> integerFlux = integerSink.asFlux();

        integerFlux.subscribe(RsUtil.subscriber("Sashank"));
        integerFlux.subscribe(RsUtil.subscriber("Aparna")); // When a 2nd subscriber will try to subscribe, it will throw an error.

        integerSink.tryEmitNext("Hi");
        integerSink.tryEmitNext("How are you ?");
    }

    @Test
    void sink_unicast_emit_next_test() {
        // Handle through which we would push items
        Sinks.Many<Integer> integerSink = Sinks.many().unicast().onBackpressureBuffer(); // Since we are going to publish multiple values, we are going to handle the back pressure.

        integerSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        integerSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux1 = integerSink.asFlux();
        integerFlux1.subscribe(System.out::println);

        Flux<Integer> integerFlux2 = integerSink.asFlux();
        integerFlux2.subscribe(System.out::println); // Here you should get an error as a second subscriber is trying to subscribe.

        integerSink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    // unicast() is extremely useful when you want just one subscriber to subscribe the publisher.

    // Let's see the thread safety of sinks.

    @Test
    public void sink_thread_safety_test() {
        Sinks.Many<Object> sink = Sinks.many().unicast().onBackpressureBuffer();

        Flux<Object> integerFlux = sink.asFlux();

        ArrayList<Object> list = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            final int j = i;
            CompletableFuture.runAsync(() -> {
                sink.tryEmitNext(j);
            });
        }
        integerFlux.subscribe(list::add);

        RsUtil.sleepSeconds(5);
        System.out.println(list.size()); // Here the result will not be 1000. Why??
        // Reason: If we use sinks like this, they will not be thread safe.
    }

    @Test
    public void sink_thread_safety_why_test() {
        Sinks.Many<Object> sink = Sinks.many().unicast().onBackpressureBuffer();

        Flux<Object> integerFlux = sink.asFlux();

        ArrayList<Object> list = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            final int j = i;
            CompletableFuture.runAsync(() -> {
                sink.emitNext(j, ((signalType, emitResult) -> true));
            });
        }
        integerFlux.subscribe(list::add);

        RsUtil.sleepSeconds(5);
        System.out.println(list.size()); // Here the result will be 1000.

        // Conclusion: Sinks are thread safe as long as we use proper error handling and retry mechanism.
    }

    @Test
    void sink_multicast_try_emit_next_test() {
        // Handle through which we would push items
        Sinks.Many<String> integerSink = Sinks.many().multicast().onBackpressureBuffer(); // Since we are going to publish multiple values, we are going to handle the back pressure.

        // Handle through which subscribers will receive items
        Flux<String> integerFlux = integerSink.asFlux();

        integerFlux.subscribe(RsUtil.subscriber("Sashank"));
        integerFlux.subscribe(RsUtil.subscriber("Aparna")); // With multicast() we can have n number of subscribers
                                                                  // and every one of them should be able to get value.
        integerSink.tryEmitNext("Hi");
        integerSink.tryEmitNext("How are you ?");
    }

    @Test
    void sink_multicast_buffer_test() {
        // Handle through which we would push items
        Sinks.Many<String> integerSink = Sinks.many().multicast().onBackpressureBuffer(); // Since we are going to publish multiple values, we are going to handle the back pressure.

        // Handle through which subscribers will receive items
        Flux<String> integerFlux = integerSink.asFlux();

        integerFlux.subscribe(RsUtil.subscriber("Sashank"));

        integerSink.tryEmitNext("Hi");
        integerSink.tryEmitNext("How are you ");

        integerFlux.subscribe(RsUtil.subscriber("Aparna")); // Here sashank will get all 3 items whereas aparna will get only 1 item.

        integerSink.tryEmitNext("?");
    }

    @Test
    void sink_multicast_direct_all_or_nothing_test() {
        // Handle through which we would push items
        Sinks.Many<String> integerSink = Sinks.many().multicast().directAllOrNothing(); // Since we are going to publish multiple values, we are going to handle the back pressure.

        // Handle through which subscribers will receive items
        Flux<String> integerFlux = integerSink.asFlux();

        integerSink.tryEmitNext("Hi");
        integerSink.tryEmitNext("How are you ");

        integerFlux.subscribe(RsUtil.subscriber("Sashank"));
        integerFlux.subscribe(RsUtil.subscriber("Aparna"));

        integerSink.tryEmitNext("?");

        integerFlux.subscribe(RsUtil.subscriber("Nalini"));

        integerSink.tryEmitNext("new msg");
    }

    @Test
    void sink_multicast_direct_all_or_nothing_with_slow_publisher_test() {

        // Handle through which we would push items
        Sinks.Many<Integer> integerSink = Sinks.many().multicast().directAllOrNothing(); // Since we are going to publish multiple values, we are going to handle the back pressure.

        // Handle through which subscribers will receive items
        Flux<Integer> integerFlux = integerSink.asFlux();

        integerFlux.subscribe(RsUtil.subscriber("Sushant"));
        integerFlux.delayElements(Duration.ofMillis(200)).subscribe(RsUtil.subscriber("Sashank")); // Here Sushants performance will be affected as Sashanks performance is slow. Use directBestEffort in this case

        for (int i = 0; i < 100; i++) {
            integerSink.tryEmitNext(i);
        }

        RsUtil.sleepSeconds(10);
    }

    @Test
    void sink_multicast_direct_direct_best_effort_with_slow_publisher_test() {

        // Handle through which we would push items
        Sinks.Many<Integer> integerSink = Sinks.many().multicast().directBestEffort(); // Since we are going to publish multiple values, we are going to handle the back pressure.

        // Handle through which subscribers will receive items
        Flux<Integer> integerFlux = integerSink.asFlux();

        integerFlux.subscribe(RsUtil.subscriber("Sushant"));
        integerFlux.delayElements(Duration.ofMillis(200)).subscribe(RsUtil.subscriber("Sashank")); // Here Sushants performance will not be affected because Sashanks performance is slow. Use directBestEffort in this case

        for (int i = 0; i < 100; i++) {
            integerSink.tryEmitNext(i);
        }

        RsUtil.sleepSeconds(10);
    }

    @Test
    void sink_multicast_test() {
        Sinks.Many<Integer> integerSink = Sinks.many().multicast().onBackpressureBuffer();

        integerSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        integerSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux1 = integerSink.asFlux();
        integerFlux1.subscribe(System.out::println);

        Flux<Integer> integerFlux2 = integerSink.asFlux();
        integerFlux2.subscribe(System.out::println);

        integerSink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    // replay() provides a caching behavior

    @Test
    void sink_replay_test() {
        // Handle through which we would push items
        Sinks.Many<String> integerSink = Sinks.many().replay().all();

        // Handle through which subscribers will receive items
        Flux<String> integerFlux = integerSink.asFlux();

        integerSink.tryEmitNext("Hi");
        integerSink.tryEmitNext("How are you ");

        integerFlux.subscribe(RsUtil.subscriber("Sashank"));
        integerFlux.subscribe(RsUtil.subscriber("Aparna"));

        integerSink.tryEmitNext("?");

        integerFlux.subscribe(RsUtil.subscriber("Nalini"));

        integerSink.tryEmitNext("new msg");

        // As we discussed replay().all() will behave as if entire event stream is cached.
        // So all the subscribers will get all the items.
    }

    @Test
    void sink_replay_latest_test() {
        // Handle through which we would push items
        Sinks.Many<String> integerSink = Sinks.many().replay().latest();

        // Handle through which subscribers will receive items
        Flux<String> integerFlux = integerSink.asFlux();

        integerSink.tryEmitNext("Hi");
        integerSink.tryEmitNext("How are you ");

        integerFlux.subscribe(RsUtil.subscriber("Sashank"));
        integerFlux.subscribe(RsUtil.subscriber("Aparna"));

        integerSink.tryEmitNext("?");

        integerFlux.subscribe(RsUtil.subscriber("Nalini"));

        integerSink.tryEmitNext("new msg");

        // As we discussed replay().latest() will cache the latest event in the queue.
        // So all the subscribers will get the latest emitted item(s).
    }

    @Test
    void sink_replay_all_test() {
        Sinks.Many<Integer> integerSink = Sinks.many().replay().all();

        integerSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        integerSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux1 = integerSink.asFlux();
        integerFlux1.subscribe(System.out::println);

        Flux<Integer> integerFlux2 = integerSink.asFlux();
        integerFlux2.subscribe(System.out::println);

        integerSink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }
}

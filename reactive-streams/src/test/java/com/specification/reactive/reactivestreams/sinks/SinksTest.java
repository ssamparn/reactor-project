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

/* *
 * Normally we create publishers with Mono.fromSupplier() or Flux.generate(), Flux.create(),  Flux.range() etc.
 * All these options will work only when we have a subscriber. Either we consume whatever reactor netty provided to us,
 * or we create a publisher like this. Mostly we spend time on building reactive pipelines with various operators.
 * So what we need is we need some tool to emit items manually. When I say manually, whatever we want, that is no loop, no range, no interval.
 * Simply without any complexity, we should be able to emit data even if we do not have subscribers, and we should be able to share it with the multiple threads
 * if we want. So come Sinks.
 *
 * Sinks: With Sink we can emit signals manually. As and when we want.
 * Once created, a sink can act as both a publisher and subscriber.
 * It's a processor. Project Reactor had a processor but that is deprecated.
 * Sinks are better alternative to the processor.
 * A sink can be exposed as a Flux or Mono (publisher).
 *
 * The way in which it works is that we will be creating these sinks using some factory methods.
 * Once it's created, it can act like a both publisher and subscriber. So via one end we can put some data & via another end we can receive it.
 * One thread can put some data, another thread can receive it, one class can put data, another class can receive it, and so on.
 * Sinks can also act like a good integration points.
 * For example, in traditional programming, when we have a multiple service, classes like this, one class might be accessing another class methods actually to achieve some business functionality.
 * Hey, send the email notification or send the SMS notification and so on.
 * But using sinks, one class can emit data without accessing other class methods like this.
 * And these classes will simply subscribe and react.
 * */
@Slf4j
public class SinksTest {

    /* *
     * Let's create a mono sink.
     * A mono sink can emit 1 item / empty / error event.
     * */
    @Test
    public void sink_one_try_emit_value_test() {
        Sinks.One<Object> unoSink = Sinks.one(); // creation of a mono sink

        Mono<Object> unoSinkMono = unoSink.asMono(); // Here sink acts as a publisher

        unoSink.tryEmitValue(1);   // Here sink acts as a subscriber
        StepVerifier.create(unoSinkMono)   // Since it's a mono, as soon as we subscribe, it also sent an onComplete() signal.
                .expectSubscription()
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    public void sink_one_try_emit_empty_test() {
        Sinks.One<Object> emptySink = Sinks.one();

        Mono<Object> emptySinkMono = emptySink.asMono();

        emptySink.tryEmitEmpty();

        StepVerifier.create(emptySinkMono)
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    public void sink_one_try_emit_error_test() {
        Sinks.One<Object> errorSink = Sinks.one();

        Mono<Object> errorSinkMono = errorSink.asMono();

        errorSink.tryEmitError(new RuntimeException("sink error"));

        StepVerifier.create(errorSinkMono)
                .expectSubscription()
                .expectError()
                .verify();
    }

    /* *
     * Sinks acts as a Hot publisher.
     * */
    @Test
    public void sink_one_with_multiple_subscriber_test() {
        Sinks.One<Object> unoSink = Sinks.one(); // creation of a mono sink

        Mono<Object> unoSinkMono = unoSink.asMono(); // Here sink acts as a publisher

        unoSink.tryEmitValue("Hi");   // Here sink acts as a subscriber

        unoSinkMono.subscribe(RsUtil.subscriber("Sam"));
        unoSinkMono.subscribe(RsUtil.subscriber("Mike")); // Both Sam & Mike will receive "Hi".
    }

    @Test
    public void sink_one_emit_value_test() {
        Sinks.One<Object> sink = Sinks.one();

        Mono<Object> sinkMono = sink.asMono();

        sink.emitValue("hi", (signalType, emitResult) -> { // This works as an error handler. This works like while trying to emit hi, in case of error then you can retry based on a boolean value
            log.info("Signal Type: {}", signalType.name());
            log.info("Emit result: {}", emitResult.name());
            return false; // returning false means do not retry in case of error.
        });

        sinkMono.subscribe(RsUtil.subscriber());
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

        sink.emitValue("hello", (signalType, emitResult) -> { // Let's try to emit one more value from mono which is not possible as per the reactor specification. Just to demo an error scenario.
            log.info("Signal Type: {}", signalType.name());
            log.info("Emit result: {}", emitResult.name());
//            return true; // returning true means it will keep on retry in case of error. It will go in an infinite loop.
            return false;
        });

        sinkMono.subscribe(RsUtil.subscriber());
    }

    /* *
     * Sink Types
     * <pre>
     | Type            |    Publisher Type    |    Pub:Sub Model   |                          Behavior                              |
     |-----------------|----------------------|--------------------------------------------------------------------------------------
     | one             |     Mono             |         1:N        | We can have multiple subscribers for a single publisher        |
     | many-unicast    |     Flux             |         1:1        | We can have a max 1 subscriber. Single subscriber can join late if required. Messages will be stored in memory |
     | many-multicast  |     Flux             |         1:N        | Late subscribers (N) can not see the already emitted events. e.g: A movie theatre or a live cricket stream |
     | many-replay     |     Flux             |         1:N        | Will replay of all events to late subscribers(N) |
     *</pre>
     */

    /* *
     * V.Imp Note: With Sink of Type one, the publisher is of Mono and pub:sub is 1:N.
     * That means we can have multiple (N) subscribers for a single publisher.
     * */
    @Test
    public void sink_one_emit_value_with_multiple_subscribers_test() {
        Sinks.One<Object> sink = Sinks.one();
        Mono<Object> sinkMono = sink.asMono();

        sinkMono.subscribe(RsUtil.subscriber("Sashank"));
        sinkMono.subscribe(RsUtil.subscriber("Monalisa"));
        // Both Sashank and Monalisa will receive Hello. So you can have n number of subscribers and each of them will get events.

        sink.tryEmitValue("Hello");

        // Aparna joined late, but will still receive event
        sinkMono.subscribe(RsUtil.subscriber("Aparna"));
    }

    /* *
     * V.Imp Note: With Sink of Type many-unicast, the behavior is of Flux and pub:sub is 1:1
     * */
    @Test
    void sink_unicast_try_emit_next_test() {
        // Handle through which we would push items
        // It's an unbounded queue.
        Sinks.Many<String> integerSink = Sinks.many().unicast().onBackpressureBuffer(); // Since we are going to publish multiple values, we are going to handle the back pressure.

        // Handle through which subscribers will receive items
        Flux<String> integerFlux = integerSink.asFlux();

        integerSink.tryEmitNext("Hi");
        integerSink.tryEmitNext("How are you");
        integerSink.tryEmitNext("?");

        // sam joined late after the events are emitted. all the events are stored in memory.
        integerFlux.subscribe(RsUtil.subscriber("Sam"));
    }

    @Test
    void sink_unicast_try_emit_next_multiple_subscriber_test() {
        // Handle through which we would push items
        // It's an unbounded queue.
        Sinks.Many<String> integerSink = Sinks.many().unicast().onBackpressureBuffer(); // Since we are going to publish multiple values, we are going to handle the back pressure.

        // Handle through which subscribers will receive items
        Flux<String> integerFlux = integerSink.asFlux();

        integerFlux.subscribe(RsUtil.subscriber("Sashank"));
        integerFlux.subscribe(RsUtil.subscriber("Aparna")); // When a 2nd subscriber will try to subscribe, it will throw an error.

        integerSink.tryEmitNext("Hi");
        integerSink.tryEmitNext("How are you ?");
    }

    /* *
     * Let's see the thread safety of sinks.
     * */
    @Test
    public void sink_thread_safety_test() {
        // Handle through which we would push items
        // It's an unbounded queue.
        Sinks.Many<Object> sink = Sinks.many().unicast().onBackpressureBuffer();

        // Handle through which subscribers will receive items
        Flux<Object> integerFlux = sink.asFlux();

        // We are taking arraylist to demo as arraylist is not thread safe. So intentionally using this.
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
        // Reason: If we use sinks like this, they will not be thread safe. But reactor documentation says that sinks are thread safe. Are they lying? No
    }

    /* *
     * Use tryEmitNext() if you just want to push events into the sink, but the delivery is not guaranteed.
     * Use emitNext() if you want guaranteed delivery.
     * */

    @Test
    public void sink_thread_safety_why_test() {
        Sinks.Many<Object> sink = Sinks.many().unicast().onBackpressureBuffer();

        Flux<Object> integerFlux = sink.asFlux();

        ArrayList<Object> list = new ArrayList<>();

        // We are taking arraylist to demo as arraylist is not thread safe. So intentionally using this.
        for (int i = 0; i < 1000; i++) {
            final int j = i;
            CompletableFuture.runAsync(() -> {
                sink.emitNext(j, ((signalType, emitResult) -> Sinks.EmitResult.FAIL_NON_SERIALIZED.equals(emitResult)));
            });
        }
        integerFlux.subscribe(list::add);

        RsUtil.sleepSeconds(5);
        System.out.println(list.size()); // Here the result will be 1000.

        // Conclusion: Sinks are thread safe as long as we use proper error handling and retry mechanism.
    }

    /* *
     * unicast() is extremely useful when you want just one subscriber to subscribe the publisher
     * */
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

    @Test
    void sink_multicast_try_emit_next_test() {
        // Handle through which we would push items
        // It's not an unbounded queue. It's a bounded queue.
        Sinks.Many<String> integerSink = Sinks.many().multicast().onBackpressureBuffer(); // Since we are going to publish multiple values, we are going to handle the back pressure.

        // Handle through which subscribers will receive items
        Flux<String> integerFlux = integerSink.asFlux();

        integerFlux.subscribe(RsUtil.subscriber("Sashank"));
        integerFlux.subscribe(RsUtil.subscriber("Monalisa")); // With multicast() we can have N number of subscribers,
                                                                  // and every one of them should be able to get the events as long as the subscribers joined early before the events actually emitted.
        integerSink.tryEmitNext("Hi");
        integerSink.tryEmitNext("How are you");
        integerSink.tryEmitNext(" ?");

        // late subscribers will not receive any events.
        integerFlux.subscribe(RsUtil.subscriber("Aparna"));
    }

    @Test
    void sink_multicast_buffer_test() {
        // Handle through which we would push items
        Sinks.Many<String> integerSink = Sinks.many().multicast().onBackpressureBuffer(); // Since we are going to publish multiple values, we are going to handle the back pressure.

        // Handle through which subscribers will receive items
        Flux<String> integerFlux = integerSink.asFlux();

        integerFlux.subscribe(RsUtil.subscriber("Sashank"));
        integerFlux.subscribe(RsUtil.subscriber("Aparna"));

        integerSink.tryEmitNext("Hi");
        integerSink.tryEmitNext("How are you ");

        integerFlux.subscribe(RsUtil.subscriber("Monalisa"));
         // Here Sashank & Aparna will get all 3 items whereas Monalisa will get only 1 item.

        integerSink.tryEmitNext("?");
    }

    /* *
     * multicast in reactor has one interesting behavior called warm up behavior.
     * */
    @Test
    void sink_multicast_warmup_behavior_test() {
        // Handle through which we would push items
        Sinks.Many<String> integerSink = Sinks.many().multicast().onBackpressureBuffer(); // Since we are going to publish multiple values, we are going to handle the back pressure.

        // Handle through which subscribers will receive items
        Flux<String> integerFlux = integerSink.asFlux();

        integerSink.tryEmitNext("Hi");
        integerSink.tryEmitNext("How are you");
        integerSink.tryEmitNext(" ?");

        /* *
         * The way in which the multicast works is that when you try to emit some messages, if there are no subscribers, these messages
         * will be going to this queue. But remember that it's a bounded queue. So based on the size, whatever we have given, so only those messages will be going to the queue.
         * Once the queue is full, all the messages will be dropped anyway. So those many messages will be saved inside the queue.
         * And when someone joins in this case Sashank, this multicast will be like, oh, you just joined. Okay. So we had some messages. Take it.
         * So it will just give it to the very first subscriber and the late subscriber like Monalisa & Aparna they will not be seeing those events.
         * But any new message everyone will see. That's guaranteed. But at the time of emitting these messages, since there were no subscribers.
         * So they go to this buffer, and it's given to the first subscriber joined.
         * */

        integerFlux.subscribe(RsUtil.subscriber("Sashank"));
        integerFlux.subscribe(RsUtil.subscriber("Aparna"));
        integerFlux.subscribe(RsUtil.subscriber("Monalisa"));
        // Here only Sashank will get all 3 events whereas Aparna & Monalisa will not receive any event. However, any new events after that everyone will receive.

        integerSink.tryEmitNext("Hello");
    }

    /* *
     * sinks().many().multicast().directBestEffort(): If one subscriber is slow, then make the best effort to emit events to fast subscriber.
     * So it's like I will put my best effort to emit events for everyone.
     * */
    @Test
    void sink_multicast_direct_direct_best_effort_with_slow_publisher_test() {
        // Handle through which we would push items
        Sinks.Many<Integer> integerSink = Sinks.many().multicast().directBestEffort(); // Since we are going to publish multiple values, we are going to handle the back pressure.

        // Handle through which subscribers will receive items
        Flux<Integer> integerFlux = integerSink.asFlux();

        // Sushant is a fast subscriber
        integerFlux.subscribe(RsUtil.subscriber("Sushant"));

        // Sashank is a slow subscriber
        integerFlux.delayElements(Duration.ofMillis(20)).subscribe(RsUtil.subscriber("Sashank"));
        // Here Sushants performance will not be affected because Sashanks performance is slow. Use directBestEffort in this case.
        // Sushant will receive all the events.

        for (int i = 0; i < 100; i++) {
            integerSink.tryEmitNext(i);
        }

        RsUtil.sleepSeconds(10);
    }

    /* *
     * sinks().many().multicast().directAllOrNothing(): If one subscriber is slow, then don't emit events for any of the subscribers (even if few subscribers are fast).
     * So it's like either I am emitting for everyone or I am not emitting for anyone.
     * */
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

        RsUtil.sleepSeconds(5);
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
    void sink_many_replay_all_test() {
        // Handle through which we would push items
        // It's an unbounded queue. That is all the messages will be stored in an unbounded queue.
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
    void sink_many_replay_limit_test() {
        // Handle through which we would push items
        // It's an unbounded queue. That is all the messages will be stored in an unbounded queue.
        Sinks.Many<String> integerSink = Sinks.many().replay().limit(2);

        // Handle through which subscribers will receive items
        Flux<String> integerFlux = integerSink.asFlux();

        integerSink.tryEmitNext("Hi");
        integerSink.tryEmitNext("How are you ");
        integerSink.tryEmitNext("?");

        integerFlux.subscribe(RsUtil.subscriber("Sashank"));
        integerFlux.subscribe(RsUtil.subscriber("Aparna"));
        integerFlux.subscribe(RsUtil.subscriber("Nalini"));

        // As we discussed replay().limit(2) will behave as if last 2 events are cached.
        // So all the subscribers will get the last 2 items.
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

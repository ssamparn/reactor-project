package com.specification.reactive.reactivestreams.batching;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

/* *
 * Reactive programming is a style of programming which observes on the data streams, reacting to the changes and propagating them! The data stream will be closed when there is no more data for source to emit or when there is an un-handled exception!
 * The data stream could be unbounded / never ending stream! In the reactive pipeline, we could have some operations which need to be executed for every item. Sometimes, instead of executing the operations for every item one by one, we could collect the item periodically and execute the operations for all the collected items at once or we might want to perform some aggregate operations on the set.
 * This is where Reactor Buffer & Window options would be very helpful.
 * Reactor Buffer: to collect the items as a list within the Flux
 * Reactor Window: to emit a collection of items as a Flux within the Flux.
 * */

/* *
 * buffer():
 * Let's consider a publisher which emits events at a very rapid rate. e.g: user click event, users product view events etc.
 * Probably business wants these details for data analytics. Let's assume all these events are received by a kafka topic. Kafka driver will provide these events as a Flux<T>.
 * Out requirement is we want to simply insert these events into a database. But, if we are going to get these events at a rapid rate, we cannot insert one by one. It's not really efficient, right?
 * So what we could do here is we could collect these events based on certain interval like every five seconds, put it in a list then insert into a database as part of one single call, and so on.
 * */
@Slf4j
public class BufferOperatorTest {

    /* *
     * Collect items from a publisher with buffer().
     * We will not see any output as buffer() will wait for the publisher to complete or by default it will try to collect Interger.MAX_VALUE.
     * So to make it work, we have to make the publisher a finite publisher using take() operator.
     * */
    @Test
    public void collect_items_using_buffer() {
        eventStream()
                .buffer() // returns a Flux<List<String>>
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(5);
    }

    /* *
     * Collect items from a publisher with buffer() based on a given interval
     * */
    @Test
    public void collect_items_using_buffer_based_on_given_interval() {
        eventStream()
                .buffer(Duration.ofMillis(200)) // returns a Flux<List<String>>
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(2);
    }

    /* *
     * Collect items from a publisher with buffer() based on number of events emitted
     * */
    @Test
    public void collect_items_using_buffer_based_on_number_of_events_emitted() {
        eventStream()
                .buffer(3) // returns a Flux<List<String>>
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(2);
    }

    private static Flux<String> eventStream() {
        return Flux.interval(Duration.ofMillis(50))
                .take(50)
                .map(i -> "event-" + (i+1));
    }

    /* *
     * Collect items from a publisher with bufferTimeout() based on number of events emitted
     * */
    @Test
    public void collect_items_using_buffer_based_on_number_of_events_emitted_test() {
        eventStreamWithoutTake()
//                .buffer(3) // With buffer(3), after event 48 it would have waited for the next 3 events that is 49th, 50th event to be emitted, but because of Flux.never(), even though its emitted, it does not get grouped. So it can not build the last list. But we can handle this kind of scenario's using bufferTimeout
                .bufferTimeout(3, Duration.ofSeconds(1)) // Don't wait beyond 1 second to form a list of 3 events. i.e: every 3 items or maximum 1 second.
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(5);
    }

    private static Flux<String> eventStreamWithoutTake() {
        return Flux.interval(Duration.ofMillis(50))
                .take(50)
                .concatWith(Flux.never())
                .map(i -> "event-" + (i+1));
    }
}
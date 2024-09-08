package com.specification.reactive.reactivestreams.batching;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/* *
 * Reactive programming is a style of programming which observes on the data streams, reacting to the changes and propagating them! The data stream will be closed when there is no more data for source to emit or when there is an un-handled exception!
 * The data stream could be unbounded / never ending stream! In the reactive pipeline, we could have some operations which need to be executed for every item. Sometimes, instead of executing the operations for every item one by one, we could collect the item periodically and execute the operations for all the collected items at once or we might want to perform some aggregate operations on the set.
 * This is where Reactor Buffer & Window options would be very helpful.
 * Reactor Buffer: to collect the items as a list within the Flux
 * Reactor Window: to emit a collection of items as a Flux within the Flux.
 * */

/* *
 * window():
 * It looks more or less like buffer(). But there is a big difference.
 * As part of the buffer, we collect a finite number of events or events emitted in a fixed duration. We collect those events as a list, and give it to the subscriber.
 * But as part of the window(), we open one new flux. That is the major difference.
 * When we use buffer() we wait, but when using window() we do not wait. We just give the item to the subscriber as and when it arrives.
 * However, we keep on changing the subscriber after collecting a finite number of events or after events emitted in a fixed duration.
 * We can imagine an application, the application is writing a lot of logs. So you have log stream. Production server keeps on writing logs to a log file.
 * And normally what we will be opening a log file like every one hour or every one day. So you will not create one log file. All the entire one-year log will not go to a single file.
 * You open one log file like every day or every one hour.
 * So your log file is the subscriber in this case. So you open a one log file and whatever the event happens in that one-hour window or a day window, everything will be going to that file.
 * You do not do one single write. Instead, you write to a log file. Then next day when new window opens, then it will be going to a new file.
 * So this is how we have to imagine how window() works.
 * */
@Slf4j
public class WindowOperatorTest {

    /* *
     * Outer publisher will be subscribed once.
     * All inner publishers will be subscribed one by one.
     * */
    @Test
    public void window_operator_simple_test() {
        Flux.range(1,20)
                .window(5)
                .doOnNext(flux -> flux.collectList().subscribe(RsUtil.subscriber("Inner publisher subscription")))
                .subscribe(RsUtil.subscriber("Outer publisher subscription"));
    }

    /* *
     * window(5): Returns a Flux<Flux<String>> instead of Flux<List<String>> like buffer().
     * It means after every 5 events emitted, we will get a new Flux subscriber & we have to subscribe to the inner flux as well.
     * */
    @Test
    public void window_operator_test() {
        eventStream()
                .window(5) // after receiving every 5 events, there will be a new flux.
                .flatMap(WindowOperatorTest::processEvents) // internally subscribing to the inner flux.
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(5);
    }

    /* *
     * window(Duration): Returns a Flux<Flux<String>> instead of Flux<List<String>> like buffer().
     * It means every 1.2 seconds, we will get a new Flux subscriber & we have to subscribe to the inner flux as well.
     * */
    @Test
    public void window_operator_based_on_duration_test() {
        eventStream()
                .window(Duration.ofMillis(1200)) // after receiving every 5 events, there will be a new flux.
                .flatMap(WindowOperatorTest::processEvents) // internally subscribing to the inner flux.
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(5);
    }

    private static Flux<String> eventStream() {
        return Flux.interval(Duration.ofMillis(200))
                .map(i -> "event-" + (i+1));
    }

    private static Mono<Void> processEvents(Flux<String> stringFlux) {
        return stringFlux.doOnNext(e -> System.out.print("*"))
                .doOnComplete(System.out::println) // will be triggered after every 5 events received.
                .then();
    }
}
package com.specification.reactive.reactivestreams.hotandcold;

import com.specification.reactive.reactivestreams.service.NameProducer;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * Hot and Cold Publisher:
 *
 * Cold Publisher: Let's imagine we have a publisher P, and we have a subscriber S1 subscribing to that publisher. Now we can attach one more subscriber S2 to the same publisher P.
 *                 Here both subscriber S1 and S2 are independent of each other. Meaning if S1 cancels, it will have no impact on S2. Publisher P will still emit events for subscriber S2.
 *                 Publisher P emits items in 2 independent and completely different data streams for each subscriber.
 *                 e.g: Netflix. If 2 users (subscribers) starts watching a show in Netflix (publisher) at the same time, they can watch the content independently, without affecting each other's subscription.
 *                 This is a classic example of Cold Publisher.
 *
 * Hot Publisher: Before understanding what a hot publisher is, we are all aware of the reactive programming rule that, Nothing happens until you subscribe.
 *                Well this rule not always stands valid for a hot publisher. Sometimes you don't have to subscribe to a hot publisher in order for the publisher to emit items.
 *                And we can have only one single data producer for all the subscribers. In some cases, we don't even need a subscriber to emit items. It will start emitting items on its own.
 *                e.g: Television channel. Now a TV channel will keep on broadcasting the content (a live cricket match or a tv serial) irrespective of how many watchers (subscribers) are actually watching (subscribing) the content
 * */
@Slf4j
public class HotColdReactiveStreamTest {

    /**
     * Demo issue with Flux.create() with fluxSink.
     * We discussed in FluxCreateWithFluxSinkUseCases.class, that Flux.create() with FluxSink is designed to be used when we have a single subscriber.
     * */

    @Test
    public void flux_create_with_flux_sink_issue_with_multiple_subscribers_test() {
        // publisher implementation
        NameProducer nameProducer = new NameProducer();
        Flux<String> nameFlux = Flux.create(nameProducer);

        nameFlux.subscribe(RsUtil.subscriber("Subscriber 1"));
        nameFlux.subscribe(RsUtil.subscriber("Subscriber 2"));

        // subscriber implementation
        // This is a sample subscriber written in some other part of your application.
        for (int i = 1; i <= 10; i++) {
            nameProducer.emitName();
        }

        /* *
         * If we run this, then only Subscriber 2 will receive events. Subscriber 1 will not receive any events. What is going on?
         * We will see that 2 flux sinks will be instantiated. Because for each and every subscriber there is a flux sink. Here we have only one NameGenerator instance and we are planning to share it with multiple flux sink instances (multiple threads).
         * So the old flux sink (flux sink of Subscriber 1) got lost and new flux sink (flux sink of Subscriber 2) took over.
         *
         * That's why remember one flux sink is for one subscriber only. It will work for single subscriber only.
         *
         * But how to fix it? Let's do the solution at the very end test.
         * */
    }

    @Test
    public void flux_create_with_flux_sink_test() {
        Flux<Object> objectFlux = Flux.create(fluxSink -> {
            log.info("Starting");
            for (int i = 0; i < 10; i++) {
                fluxSink.next(i);
            }
            fluxSink.complete();
        });

        objectFlux.subscribe(RsUtil.subscriber("Subscriber 1"));

        objectFlux.subscribe(RsUtil.subscriber("Subscriber 2"));

        /* *
         * Note: Here "Starting" gets printed twice. Each subscriber 1 and 2 have their own instance of flux sink.
         * That's why both Subscriber 1 and Subscriber 2 are independent of each other. This is a classic example of Cold Publisher.
         * */
    }

    @Test
    public void flux_cold_publisher_simple_test() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofMillis(200));

        stringFlux.subscribe((element) -> System.out.println("Subscriber 1: " + element)); // Emits the value from the beginning
        Thread.sleep(1200);
        stringFlux.subscribe((element) -> System.out.println("Subscriber 2: " + element)); // Emits the value from the beginning
        Thread.sleep(1300);
    }

    /* *
     *  Sample example of 2 users Sam and Mike wants to watch movies in Netflix.
     *  The cold behavior is the default behavior of a publisher i.e: nothing happens until a subscriber subscribes to a Publisher.
     */
    @Test
    public void flux_cold_publisher_movie_test() {
        Flux<String> movieFlux = movieStream();

        RsUtil.sleepSeconds(1);
        movieFlux.subscribe(RsUtil.subscriber("Sam"));

        RsUtil.sleepSeconds(3);
        log.info("Mike is about to watch the movie");
        movieFlux.subscribe(RsUtil.subscriber("Mike"));

        RsUtil.sleepSeconds(11);
        // Sam will complete the movie before Mike does, as Sam started watching before Mike.
        // This is the cold behavior of a publisher. Now how do we make it a hot publisher? By using share() method on the movie stream flux.
    }

    /**
     * share(): Using share() we can get the hot behavior of a so-called cold publisher.
     * It is one of the way to convert a cold publisher to a hot publisher
     * */

    // Same example of 2 users Sam and Mike wants to watch movies in a Theatre.
    @Test
    public void flux_hot_publisher_share_movie_test() {
        Flux<String> movieFlux = movieStream().share();

        RsUtil.sleepSeconds(1);
        movieFlux.subscribe(RsUtil.subscriber("Sam"));

        RsUtil.sleepSeconds(3);
        log.info("Mike is about to watch the movie");
        movieFlux
                //.take(3)
                .subscribe(RsUtil.subscriber("Mike"));

        RsUtil.sleepSeconds(9);
        // Since mike joined in late, he missed the initial few movie scenes and both of them will complete the movie at the same time.
        // Now imagine a scenario in which mike does not want to watch the movie after watching 3 scenes, it will not affect sam. He will still continue watching the movie.
    }

    /**
     * Hot Publisher => 1 Data Producer for all the subscribers.
     * share(): share() is the alias for publish() + refCount(1).
     * It needs 1 minimum subscriber to emit data. It stops emitting items if it has 0 subscribers.
     * re-subscription: It starts again when there is a new subscriber.
     * To have minimum 2 subscribers, use publish().refCount(2).
     * With share() as we saw, we got the hot behavior of a publisher.
     * */

    // Same example of 2 users Sam and Mike wants to watch a movie in a Theatre.
    @Test
    public void flux_hot_publisher_publish_refCount_movie_test() {
        Flux<String> movieFlux = movieStream()
            .publish()
            .refCount(1); // publish() + refCount(1) is another way to convert a cold publisher to a hot publisher

        RsUtil.sleepSeconds(1);
        movieFlux.subscribe(RsUtil.subscriber("Sam"));

        RsUtil.sleepSeconds(3);
        log.info("Mike is about to watch the movie");
        movieFlux.subscribe(RsUtil.subscriber("Mike"));

        RsUtil.sleepSeconds(9);
    }

    /* *
     * share() = publish() + autoConnect(1) we got the hot behavior of a publisher. Almost same as publish().refCount(1)
     * And publish() + autoConnect(0): It will NOT wait for subscribers to emit items. Does NOT stop when subscribers cancel.
     * So it will start producing even for 0 subscribers once it is started. This is what make a real hot publisher.
     * */

    // Same example of 2 users Sam and Mike wants to watch movie scenes in a Theatre.
    @Test
    public void flux_hot_publisher_publish_autoConnect_movie_test() {
        Flux<String> movieFlux = movieStream()
                .publish()
                .autoConnect(1); // publish() + autoConnect(1) is another way to convert a cold publisher to a hot publisher

        RsUtil.sleepSeconds(1);
        movieFlux.subscribe(RsUtil.subscriber("Sam"));

        RsUtil.sleepSeconds(3);
        log.info("Mike is about to watch the movie");
        movieFlux.subscribe(RsUtil.subscriber("Mike"));

        RsUtil.sleepSeconds(9);
    }

    // Sample example of 2 users Sam and Mike wants to watch movie scenes in a Theatre.
    // share() = publish() + autoConnect(1) we got the hot behavior of a publisher.
    @Test
    public void flux_hot_publisher_publish_autoConnect_with_zero_subscriber_movie_test() {
        Flux<String> movieFlux = movieStream()
                .publish()
                .autoConnect(0); // publish() + autoConnect(0) is another way to convert a cold publisher to an actual hot publisher.
        // This is a deviation to the reactive specification "Nothing happens until you subscribe"

        RsUtil.sleepSeconds(3);
        log.info("Sam start watching the movie 3 seconds late");
        movieFlux.subscribe(RsUtil.subscriber("Sam"));

        RsUtil.sleepSeconds(4);
        log.info("Mike is about to watch the movie");
        movieFlux.subscribe(RsUtil.subscriber("Mike"));

        RsUtil.sleepSeconds(4);
    }

    @Test
    public void flux_hot_publisher_autoConnect_stock_test() {
        Flux<String> stockStream = stockStream()
                .publish()
                .autoConnect(0);

        RsUtil.sleepSeconds(3);
        log.info("Sam starts watching the market 3 seconds late");
        stockStream.subscribe(RsUtil.subscriber("Sam"));

        RsUtil.sleepSeconds(10);
        log.info("Mike is about to watch the market after 14 seconds delay");
        stockStream.subscribe(RsUtil.subscriber("mike"));
        RsUtil.sleepSeconds(5);

        // This will be a problem as they both start watching the market late and have not received events already emitted.
        // So we have to somehow change the behavior of this hot publisher to a slightly cold publisher by providing already missed events to the subscribers.
        // In this case use cache() which is replay(history) + autoConnect()
    }

    /**
     * cache() = replay(history parameter).autoConnect()
     */
    @Test
    public void flux_hot_publisher_cache_stock_test() {
        Flux<String> stockStream = stockStream()
                .cache(); // cache() is a way to keep the behavior of a cold publisher intact.

        RsUtil.sleepSeconds(3);
        log.info("Sam starts watching the market 3 seconds late");
        stockStream.subscribe(RsUtil.subscriber("Sam"));

        RsUtil.sleepSeconds(10);
        log.info("Mike is about to watch the market after 14 seconds delay");
        stockStream.subscribe(RsUtil.subscriber("mike"));
        RsUtil.sleepSeconds(5);
    }

    // movie threater
    private Flux<String> movieStream() {
        return Flux.generate(
                () -> {
                    log.info("received movie request");
                    return 1;
                },
                (state, synchronousSink) -> {
                    var scene = "movie scene " + state;
                    log.info("playing {}", scene);
                    synchronousSink.next(scene);
                    return state + 1;
                }
        )
                .take(20)
                .cast(String.class)
                .delayElements(Duration.ofMillis(500));
    }

    // stock index stream
    private Flux<String> stockStream() {
        return Flux.generate(
                        () -> {
                            log.info("received stock index request");
                            return 1;
                        },
                        (state, synchronousSink) -> {
                            synchronousSink.next("round: " + state + " rate: " + RsUtil.faker().random().nextInt(100, 999));
                            return state + 1;
                        }
                )
                .doOnNext(price -> log.info("Stock rate emitted: {}", price))
                .take(20)
                .cast(String.class)
                .delayElements(Duration.ofMillis(750));
    }

    /* *
     * Demo issue with Flux.create() with fluxSink.
     * We discussed in FluxCreateWithFluxSinkUseCases.class, that Flux.create() with FluxSink is designed to be used when we have a single subscriber.
     * But how to cater it to multiple subscribers? Let's fix it. Solution: Make it a Hot Publisher.
     * */

    @Test
    public void flux_create_with_flux_sink_multiple_subscribers_issue_fix_test() {
        // publisher implementation
        NameProducer nameProducer = new NameProducer();
        Flux<String> nameFlux = Flux.create(nameProducer)
                .share();

        nameFlux.subscribe(RsUtil.subscriber("Subscriber 1"));
        nameFlux.subscribe(RsUtil.subscriber("Subscriber 2"));

        // subscriber implementation
        // This is a sample subscriber written in some other part of your application.
        for (int i = 1; i <= 10; i++) {
            nameProducer.emitName();
        }

        // By using share(), we made it a hot publisher. Now just one instance of flux sink will be able to cater multiple subscribers.
    }
}

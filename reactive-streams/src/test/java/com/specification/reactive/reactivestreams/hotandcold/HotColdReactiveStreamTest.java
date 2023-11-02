package com.specification.reactive.reactivestreams.hotandcold;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class HotColdReactiveStreamTest {

    @Test
    public void flux_cold_publisher_test() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        stringFlux.subscribe((element) -> System.out.println("Subscriber 1: " + element)); // Emits the value from the beginning
        Thread.sleep(3000);

        stringFlux.subscribe((element) -> System.out.println("Subscriber 2: " + element)); // Emits the value from the beginning
        Thread.sleep(4000);
    }

    @Test
    public void flux_hot_publisher_test() throws InterruptedException {

        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();
        connectableFlux.subscribe((element) -> System.out.println("Subscriber 1: " + element)); // Does not emit the values from the beginning
        Thread.sleep(3000);

        connectableFlux.subscribe((element) -> System.out.println("Subscriber 2: " + element)); // Does not emit the values from the beginning
        Thread.sleep(4000);

    }

    // Sample example of 2 users Sam and Mike wants to watch movies in Netflix.
    // The cold behavior is the default behavior of a publisher i.e: nothing happens until a subscriber subscribes to a Publisher.
    @Test
    public void flux_cold_publisher_movie_test() {
        Flux<String> movieFlux = movieFlux().delayElements(Duration.ofMillis(1000));

        movieFlux.subscribe(RsUtil.subscriber("Sam"));
        RsUtil.sleepSeconds(6);

        movieFlux.subscribe(RsUtil.subscriber("Mike"));
        RsUtil.sleepSeconds(11);

        // Sam will complete the movie before Mike does, as Sam started watching before Mike.
    }


    /**
     * share(): Using share() we can get the hot behavior of a publisher.
     * It is one of the way to convert a cold publisher to a hot publisher
     * */

    // Sample example of 2 users Sam and Mike wants to watch movies in a Theatre.
    @Test
    public void flux_hot_publisher_share_movie_test() {
        Flux<String> movieFlux = movieFlux()
            .delayElements(Duration.ofMillis(1000))
            .share();

        movieFlux.subscribe(RsUtil.subscriber("Sam"));
        RsUtil.sleepSeconds(6);

        movieFlux.subscribe(RsUtil.subscriber("Mike"));
        RsUtil.sleepSeconds(11);
        // Both Sam and Mike will complete the movie at the same time
    }


    /**
     * share(): share() is the alias for publish() + refCount(1).
     * With share() as we saw, we got the hot behavior of a publisher.
     * */

    // Sample example of 2 users Sam and Mike wants to watch movie scenes in a Theatre.
    @Test
    public void flux_hot_publisher_publish_refCount_movie_test() {
        Flux<String> movieFlux = movieFlux()
            .delayElements(Duration.ofMillis(1000))
            .publish()
            .refCount(1); // publish() + refCount(1) is another way to convert a cold publisher to a hot publisher

        movieFlux.subscribe(RsUtil.subscriber("sam"));
        RsUtil.sleepSeconds(11);

        movieFlux.subscribe(RsUtil.subscriber("mike"));
        RsUtil.sleepSeconds(11);
    }

    // Sample example of 2 users Sam and Mike wants to watch movie scenes in a Theatre.
    // share() = publish() + autoConnect(1) we got the hot behavior of a publisher.
    @Test
    public void flux_hot_publisher_publish_autoConnect_movie_test() {
        Flux<String> movieFlux = movieFlux()
                .delayElements(Duration.ofMillis(1000))
                .publish()
                .autoConnect(1); // publish() + autoConnect(1) is another way to convert a cold publisher to a hot publisher

        movieFlux.subscribe(RsUtil.subscriber("Sam"));
        RsUtil.sleepSeconds(11);

        log.info("Mike is about to watch the movie");
        movieFlux.subscribe(RsUtil.subscriber("Mike"));
        RsUtil.sleepSeconds(5);
    }

    // Sample example of 2 users Sam and Mike wants to watch movie scenes in a Theatre.
    // share() = publish() + autoConnect(1) we got the hot behavior of a publisher.
    @Test
    public void flux_hot_publisher_publish_autoConnect_with_zero_subscriber_movie_test() {
        Flux<String> movieFlux = movieFlux()
                .delayElements(Duration.ofMillis(1000))
                .publish()
                .autoConnect(0); // publish() + autoConnect(0) is another way to convert a cold publisher to a hot publisher.
        // This is a deviation to the reactive specification "Nothing happens until you subscribe"

        RsUtil.sleepSeconds(4);
        log.info("Sam start watching the movie 4 seconds late");
        movieFlux.subscribe(RsUtil.subscriber("sam"));
        RsUtil.sleepSeconds(6);

        log.info("Mike is about to watch the movie");
        movieFlux.subscribe(RsUtil.subscriber("mike"));
        RsUtil.sleepSeconds(10);
    }

    // Sample example of 2 users Sam and Mike wants to watch movie scenes in a Theatre.
    // cache() = publish().replay(history parameter)
    @Test
    public void flux_hot_publisher_cache_movie_test() {
        Flux<String> movieFlux = movieFlux()
                .delayElements(Duration.ofMillis(1000))
                .cache(); // cache() is a way to keep the behavior of a cold publisher intact.

        RsUtil.sleepSeconds(2);
        log.info("Sam start watching the movie 2 seconds late");
        movieFlux.subscribe(RsUtil.subscriber("sam"));
        RsUtil.sleepSeconds(10);

        log.info("Mike is about to watch the movie");
        movieFlux.subscribe(RsUtil.subscriber("mike"));
        RsUtil.sleepSeconds(4);
    }

    private Flux<String> movieFlux() {
        return Flux.just(
                "1. The Pale Blue Eye",
                "2. The Gray Man",
                "3. White Noise",
                "4. Bullet Train",
                "5. After Ever Happy",
                "6. Godfather",
                "7. The Terminal",
                "8. Catch me if you can",
                "9. True Spirit",
                "10. Oblivion"
        );
    }
}

package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class FluxMultipleSubscriber {

    // multiple subscribers can subscribe to a publisher (both flux and mono).
    @Test
    public void flux_multiple_subscriber_test() {
        Flux<String> nameFlux = Flux.just("Sam", "Harry", "Bapun", "Samay");

        Flux<String> nameStartsWithSFlux = nameFlux.filter(name -> name.startsWith("S"));

        nameFlux.subscribe(
                name -> log.info("All names: Subscribed by subscriber 1 : {}", name),
                RsUtil.onError(),
                RsUtil.onComplete()
        );

        nameStartsWithSFlux.subscribe(
                name -> log.info("Names starts with S: Subscribed by subscriber 2 : {}", name),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }
}

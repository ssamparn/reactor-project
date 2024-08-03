package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxRange {

    // Flux.range(): range() of Flux acts like a simple for loop that we can use in reactive programming.
    @Test
    public void flux_range_integer_Test() {
        Flux<Integer> integerFlux = Flux.range(1, 10);

        integerFlux.subscribe(
                RsUtil.onNext(),
                RsUtil.onError(),
                RsUtil.onComplete()
        );
    }

    /* *
     * log() operator or any operator in reactive programming acts like a subscriber for the publisher above it and publisher for the subscriber below it.
     * It acts as a middle man.
     * */
    @Test
    public void flux_range_generate_random_firstname_test() {
        Flux.range(1, 10)
                .log("flux-range")
                .map(i -> i + " : " + RsUtil.faker().name().firstName()) // Subscriber 2 - Here the map() operator is the subscriber to log() (above) which acts as a publisher for this subscriber 2
                .log("flux-map")
                .subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete()); // Subscriber 1 - This subscriber is subscribing to the map() which acts as a publisher for this subscriber 1
    }
}

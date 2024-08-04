package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * Flux.interval() produces a FLux<Long> that is infinite and emits ticks from a clock.
 * Flux.interval() is really useful when we have to emit infinite items in a certain duration.
 * It emits items in a parallel thread pool. Main thread exits immediately.
 * */
public class FluxInterval {

    @Test
    public void flux_interval_test() {
        Flux.interval(Duration.ofMillis(50))
                .map(i-> RsUtil.faker().name().firstName())
                .subscribe(RsUtil.onNext());
        RsUtil.sleepSeconds(2);
    }

}

package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class FluxInterval {

    @Test
    public void flux_interval_test() {
        Flux.interval(Duration.ofSeconds(1))
                .subscribe(RsUtil.onNext());
        RsUtil.sleepSeconds(5);
    }

}

package com.specification.reactive.flux;

import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class FluxInterval {

    @Test
    public void fluxIntervalTest() {
        Flux.interval(Duration.ofSeconds(1))
                .subscribe(ReactiveSpecificationUtil.onNext());
        ReactiveSpecificationUtil.sleepSeconds(5);
    }

}

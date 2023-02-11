package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxGenerateWithSynchronousSink {

    @Test
    public void flux_generate_behaviour_test_1() {
        Flux.generate(synchronousSink -> {
            synchronousSink.next(RsUtil.faker().name().fullName());
            synchronousSink.next(RsUtil.faker().name().fullName());
            // This line will result in an error.
            // With synchronousSink, it can emit one item only.
            synchronousSink.complete();
        }).subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_generate_behaviour_test_2() {
        Flux.generate(synchronousSink -> {
            synchronousSink.next(RsUtil.faker().name().fullName());
            // With synchronousSink, it can emit one item only. But it's not a Mono as infinite instance(s) of synchronousSink gets created
        }).subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_generate_behaviour_test_3() {
        Flux.generate(synchronousSink -> {
            synchronousSink.next(RsUtil.faker().name().fullName());
            // With synchronousSink, it can emit one item only. But it's not a Mono as infinite instance(s) of synchronousSink gets created
        })
        .take(5)
        .subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_generate_behaviour_test_4() {
        Flux.generate(synchronousSink -> {
            synchronousSink.next(RsUtil.faker().name().fullName());
            synchronousSink.complete();
        })
        .take(5)
        .subscribe(RsUtil.subscriber("Flux Create"));
    }
}

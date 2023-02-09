package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxCreateWithFluxSink {

    @Test
    public void fluxCreateTest() {
        Flux.create(fluxSink -> {
            fluxSink.next(1);
            fluxSink.next(2);
            fluxSink.complete();
        }).subscribe(ReactiveSpecificationUtil.subscriber("Flux Create"));
    }

    @Test
    public void fluxSinkEmitAllCountryNamesTillCanadaTest() {
        Flux.create(fluxSink -> {
            String country;
            do {
                country = ReactiveSpecificationUtil.faker().country().name();
                fluxSink.next(country);
            } while (!country.equalsIgnoreCase("canada"));

            fluxSink.complete();

        }).subscribe(ReactiveSpecificationUtil.subscriber("Flux Country"));
    }
}

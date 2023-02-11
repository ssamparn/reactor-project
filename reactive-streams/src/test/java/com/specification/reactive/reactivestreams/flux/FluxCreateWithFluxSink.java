package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxCreateWithFluxSink {

    @Test
    public void flux_create_test_with_flux_sink_test_1() {
        Flux.create(fluxSink -> {      // only one instance of fluxSink gets created
            fluxSink.next(1);
            fluxSink.next(2);
            fluxSink.complete();
        }).subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_create_with_flux_sink_test_2() {
        Flux.create(synchronousSink -> {      // only one instance of fluxSink gets created
            synchronousSink.next(1);          // Don't get fooled by the variable name. It is actually a flux sink.
            synchronousSink.next(2);
            synchronousSink.complete();
        }).subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_sink_emit_allCountryNames_till_canada_test() {
        Flux.create(fluxSink -> {
            String country;
            do {
                country = RsUtil.faker().country().name();
                fluxSink.next(country);
            } while (!country.equalsIgnoreCase("canada"));

            fluxSink.complete();

        }).subscribe(RsUtil.subscriber("Flux Country"));
    }
}

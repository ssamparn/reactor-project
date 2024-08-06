package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/* *
 * To create a flux & emit items programmatically
 * */
public class FluxCreateWithFluxSink {

    /* *
    * Flux.create(): The create() method in Flux accepts a consumer of FluxSink and is used when we want to calculate multiple (0 to infinity) values that are not influenced by the application’s state.
    * This is because the underlying method of the Flux.create() method keeps calculating the elements before emitting, and emits items synchronously.
    * Besides, the downstream system (e.g. take() operator) determines how many elements it needs. Therefore, if the downstream system is unable to keep up, already emitted elements are either buffered or removed.
    *
    * Flux.create() does not check the downstream demand by default! It is by design.
    * By default, the emitted elements are buffered in the queue (maximum Interger.MAX_VALUE) until the downstream system request more elements
    * */
    @Test
    public void flux_create_test_with_flux_sink_test() {
        // publisher implementation
        Flux.create(fluxSink -> {      // only one instance of fluxSink gets created
            fluxSink.next(1);
            fluxSink.next(2);
            fluxSink.next("Sam");
            fluxSink.next(List.of("x", "y", "z"));
            fluxSink.next(Map.of("a", "apple", "b", "banana"));

            fluxSink.complete();
        }).subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_create_test_with_flux_sink_in_for_loop_test() {
        // publisher implementation
        Flux.create(fluxSink -> {      // only one instance of fluxSink gets created
            for (int i = 0; i < 10; i++) {
                fluxSink.next(RsUtil.faker().country().name());
            }
            fluxSink.complete();
        }).subscribe(RsUtil.subscriber("Flux Create"));
    }

    // we can use this logic to implement a scenario in which we have to emit events until certain condition is met.
    @Test
    public void flux_sink_emit_all_country_names_till_canada_test() {
        // publisher implementation
        Flux.create(fluxSink -> {
            String country;
            do {
                country = RsUtil.faker().country().name();
                fluxSink.next(country);
            } while (!country.equalsIgnoreCase("Canada"));

            fluxSink.complete();

        }).subscribe(RsUtil.subscriber("Flux Country"));
    }
}

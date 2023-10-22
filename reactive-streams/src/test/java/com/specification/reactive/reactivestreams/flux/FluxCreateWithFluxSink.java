package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxCreateWithFluxSink {

    /* *
    * Flux.create(): The create() method in Flux is used when we want to calculate multiple (0 to infinity) values that are not influenced by the application’s state.
    * This is because the underlying method of the Flux.create() method keeps calculating the elements.
    * Besides, the downstream system (e.g. take() operator) determines how many elements it needs. Therefore, if the downstream system is unable to keep up, already emitted elements are either buffered or removed.
    *
    * By default, the emitted elements are buffered until the downstream system request more elements
    * */
    @Test
    public void flux_create_test_with_flux_sink_test() {
        Flux.create(fluxSink -> {      // only one instance of flux sink gets created
            fluxSink.next(1);
            fluxSink.next(2);
            fluxSink.complete();
        }).subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_create_with_flux_synchronous_sink_test() {
        Flux.create(fluxSink -> {      // only one instance of flux synchronous sink gets created
            fluxSink.next(1);
            fluxSink.next(2);
            fluxSink.complete();
        }).subscribe(RsUtil.subscriber("Flux Create"));
    }

    @Test
    public void flux_sink_emit_all_country_names_till_canada_test() {
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

package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.stream.IntStream;

@Slf4j
public class FluxCreateWithFluxSinkIssueFix {

    @Test
    public void take_operator_with_flux_simple_test() {
        Flux.range(1, 10)
                .take(3)
                .subscribe(RsUtil.subscriber()); // will emit 1, 2, 3. so take() operator works as expected while Flux is created like this.
    }

    @Test
    public void take_operator_with_flux_sink_test() {
        Flux.create(fluxSink -> {
            int i = 0;
            do {
                log.info("Emitting: {}", i);
                fluxSink.next(i);
                i++;
            } while (i <= 10);
            fluxSink.complete();
        })
        .take(3) // here subscriber is not receiving any data after 3 data. But fluxSink keeps on emitting items even after 3 items. so we have to check for the cancellation of fluxSink before emitting data.
        .subscribe(RsUtil.subscriber());
    }

    @Test
    public void take_operator_with_flux_sink_cancelled_test() {
        Flux.create(fluxSink -> {
            int i = 0;
            do {
                log.info("Emitting: {}", i);
                fluxSink.next(i);
                i++;
            } while (i <= 10 && !fluxSink.isCancelled());
            fluxSink.complete();
        })
        .take(3)
        .subscribe(RsUtil.subscriber());
    }

    @Test
    public void flux_sink_requested_country_names_test() {
        Flux.create(fluxSink -> {
            String country;
            do {
                country = RsUtil.faker().country().name();
                log.info("Emitting : {}", country);
                fluxSink.next(country);
            } while (!country.equalsIgnoreCase("canada") && !fluxSink.isCancelled()); // without fluxSink.isCancelled() the fluxSink will keep on emitting items despite the take operator.
            fluxSink.complete();
        })
        .take(3)
        .subscribe(RsUtil.subscriber());
    }
}

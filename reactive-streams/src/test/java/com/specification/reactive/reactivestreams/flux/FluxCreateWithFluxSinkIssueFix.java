package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class FluxCreateWithFluxSinkIssueFix {

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

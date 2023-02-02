package com.specification.reactive.flux;

import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxCreateIssueFix {

    @Test
    public void fluxSinkRequestedCountryNamesTest() {
        Flux.create(fluxSink -> {
            String country;
            do {
                country = ReactiveSpecificationUtil.faker().country().name();
                System.out.println("Emitting : " + country);
                fluxSink.next(country);
            } while (!country.equalsIgnoreCase("canada") && !fluxSink.isCancelled());

            fluxSink.complete();
        })
        .take(3)
        .subscribe(ReactiveSpecificationUtil.subscriber());
    }
}

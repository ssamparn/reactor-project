package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.model.Flight;
import com.specification.reactive.reactivestreams.util.RsUtil;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static com.specification.reactive.reactivestreams.util.RsUtil.addDebugger;

public class QatarFlightService implements FlightService {
    private static final String AIRLINE = "Qatar";

    @Override
    public Flux<Flight> getFlights() {
        return Flux.range(1, RsUtil.faker().random().nextInt(1, 10))
                .delayElements(Duration.ofMillis(RsUtil.faker().random().nextInt(100, 400)))
                .map(i -> new Flight(AIRLINE + ":" + RsUtil.faker().random().nextInt(100, 9999), RsUtil.faker().random().nextInt(500, 10000)))
                .transform(addDebugger(AIRLINE));
    }
}

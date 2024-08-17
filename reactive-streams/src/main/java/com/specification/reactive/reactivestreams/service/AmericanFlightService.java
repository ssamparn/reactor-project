package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.model.Flight;
import com.specification.reactive.reactivestreams.util.RsUtil;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class AmericanFlightService implements FlightService {

    private static final String AIRLINE = "American Airlines";

    @Override
    public Flux<Flight> getFlights() {
        return Flux.range(1, RsUtil.faker().random().nextInt(1, 10))
                .delayElements(Duration.ofMillis(RsUtil.faker().random().nextInt(100, 400)))
                .map(i -> new Flight(AIRLINE + ":" + RsUtil.faker().random().nextInt(100, 9999), RsUtil.faker().random().nextInt(500, 10000)))
                .filter(b -> RsUtil.faker().random().nextBoolean());
    }
}

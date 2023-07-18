package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.util.RsUtil;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class QatarFlightService implements FlightService {
    @Override
    public Flux<String> getFlights() {
        return Flux.range(1, RsUtil.faker().random().nextInt(1, 5))
                .delayElements(Duration.ofSeconds(1))
                .map(i -> "Qatar : " + RsUtil.faker().random().nextInt(100, 9999))
                .filter(i -> RsUtil.faker().random().nextBoolean());
    }
}

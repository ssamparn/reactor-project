package com.specification.reactive.reactivestreams.service;

import reactor.core.publisher.Flux;

public interface FlightService {

    Flux<String> getFlights();
}



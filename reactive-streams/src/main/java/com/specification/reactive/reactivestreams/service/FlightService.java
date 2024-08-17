package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.model.Flight;
import reactor.core.publisher.Flux;

public interface FlightService {

    Flux<Flight> getFlights();
}



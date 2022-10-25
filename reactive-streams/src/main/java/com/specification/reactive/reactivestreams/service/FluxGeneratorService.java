package com.specification.reactive.reactivestreams.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Service
public class FluxGeneratorService {

    public Flux<String> createNamesFlux() {
        List<String> nameList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");
        Flux<String> nameFlux = Flux.fromIterable(nameList);

        return nameFlux;
    }
}

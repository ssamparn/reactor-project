package com.specification.reactive.reactivestreams.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class FluxGeneratorServiceTest {

    @InjectMocks
    private FluxGeneratorService fluxGeneratorService;

    @Test
    void createNames_flux_events_test() {
        fluxGeneratorService.createNamesFlux().log()
                .subscribe(name -> System.out.println("Names: " + name));
    }

    @Test
    void createNames_flux_events_count_test() {
        Flux<String> namesFlux = fluxGeneratorService.createNamesFlux();

        StepVerifier.create(namesFlux)
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .expectNextCount(0)
                .verifyComplete();
    }
}

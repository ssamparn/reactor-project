package com.specification.reactive.reactivestreams.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FluxGeneratorServiceTest {

    @InjectMocks
    private FluxGeneratorService fluxGeneratorService;

    @Test
    void createNamesFluxTest() {
        fluxGeneratorService.createNamesFlux().log()
                .subscribe(name -> System.out.println("Names: " + name));
    }
}

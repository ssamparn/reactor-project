package com.specification.reactive.reactivestreams.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MonoGeneratorServiceTest {

    @InjectMocks
    private MonoGeneratorService monoGeneratorService;

    @Test
    void createNameMonoTest() {
        monoGeneratorService.createNameMono().log()
                .subscribe(name -> System.out.println("Names: " + name));
    }
}

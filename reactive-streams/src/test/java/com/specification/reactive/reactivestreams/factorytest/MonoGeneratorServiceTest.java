package com.specification.reactive.reactivestreams.factorytest;

import com.specification.reactive.reactivestreams.service.MonoGeneratorService;
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
        monoGeneratorService.createNameMono()
                .subscribe(name -> System.out.println("Names: " + name));
    }
}

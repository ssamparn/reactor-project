package com.specification.reactive.reactivestreams.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class MonoGeneratorServiceTest {

    @InjectMocks
    private MonoGeneratorService monoGeneratorService;

    @Test
    void create_name_mono_test() {
        monoGeneratorService.createNameMono().log()
                .subscribe(name -> log.info("Names: {}", name));
    }
}

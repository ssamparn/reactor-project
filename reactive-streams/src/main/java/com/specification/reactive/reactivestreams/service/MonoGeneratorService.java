package com.specification.reactive.reactivestreams.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MonoGeneratorService {

    public Mono<String> createNameMono() {
        return Mono.just("alex");
    }

}

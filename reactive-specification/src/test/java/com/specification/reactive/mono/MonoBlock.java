package com.specification.reactive.mono;

import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class MonoBlock {

    @Test
    public void monoBlockTest() {

        getName();
        getName()
                .subscribeOn(Schedulers.boundedElastic())
                .block();
        getName();

        ReactiveSpecificationUtil.sleepSeconds(4);
    }

    private static Mono<String> getName() {
        System.out.println("Entered getName method: ");
        return Mono.fromSupplier(() -> {
            System.out.println("Generating Name...");
            ReactiveSpecificationUtil.sleepSeconds(2);
            return ReactiveSpecificationUtil.faker().name().fullName();
        }).map(String::toUpperCase);
    }
}

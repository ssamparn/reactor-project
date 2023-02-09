package com.specification.reactive.reactivestreams.mono;

import com.specification.reactive.reactivestreams.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

public class MonoFromSupplier {

    @Test
    public void monoFromSupplierTest() {
        // Use Mono.just() only when you know publisher already have the data.
        // Mono.just(getName());

        // Use Mono.fromSupplier() when you want to invoke publisher lazily.

        Supplier<String> stringSupplier = () -> getName();

        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);
         stringMono.subscribe(
                 ReactiveSpecificationUtil.onNext()
         );
    }

    private static String getName() {
        System.out.println("Generating Name: ");
        return ReactiveSpecificationUtil.faker().name().fullName();
    }
}

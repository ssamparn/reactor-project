package com.specification.reactive.reactivestreams.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Slf4j
public class MonoFromSupplier {

    @Test
    public void mono_from_supplier_test() {
        // Use Mono.just() only when you know publisher already have the data.
        // Mono.just(getName());

        // Use Mono.fromSupplier() when you want to invoke publisher lazily.

        Supplier<String> stringSupplier = () -> getName();

        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);
         stringMono.subscribe(
                 RsUtil.onNext()
         );
    }

    private static String getName() {
        log.info("Generating Name: ");
        return RsUtil.faker().name().fullName();
    }
}

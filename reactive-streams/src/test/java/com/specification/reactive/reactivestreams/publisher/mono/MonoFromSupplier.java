package com.specification.reactive.reactivestreams.publisher.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Slf4j
public class MonoFromSupplier {

    /* *
     * Difference between Mono.just() and Mono.fromSupplier():
     * Use Mono.just() only when you know publisher already have the data.
     * Use Mono.fromSupplier() when you want to invoke publisher lazily.
     * */
    @Test
    public void mono_from_supplier_test() {
        // Mono.fromSupplier() create a Mono, producing its value using the provided Supplier.
        // If the Supplier resolves to null, the resulting Mono completes empty.
        Supplier<String> stringSupplier = () -> getName();

        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);
         stringMono.subscribe(
                 RsUtil.onNext()
         );
    }

    private static String getName() {
        log.info("Publishing Names: "); // Imagine this is a time consuming operation
        RsUtil.sleepMilliSeconds(500);
        return RsUtil.faker().name().fullName();
    }
}

package com.specification.reactive.reactivestreams.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class MonoSupplierRefactoring {

    @Test
    public void Mono_Supplier_refactoring_test() {

        // Blocking
        getName();
        getName()
                .subscribe(RsUtil.onNext());
        getName();

        // Asynchronous
        getName();
        getName()
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(RsUtil.onNext());
        getName();

        // To get the name block the main thread
        RsUtil.sleepSeconds(4);
    }

    private static Mono<String> getName() {
        System.out.println("Entered getName method: ");
        return Mono.fromSupplier(() -> {
            System.out.println("Generating Name...");
            RsUtil.sleepSeconds(2);
            return RsUtil.faker().name().fullName();
        }).map(String::toUpperCase);
    }
}

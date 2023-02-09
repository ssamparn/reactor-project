package com.specification.reactive.reactivestreams.mono;

import com.specification.reactive.reactivestreams.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class MonoSupplierRefactoring {

    @Test
    public void MonoSupplierRefactoringTest() {

        // Blocking
        getName();
        getName()
                .subscribe(ReactiveSpecificationUtil.onNext());
        getName();

        // Asynchronous
        getName();
        getName()
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(ReactiveSpecificationUtil.onNext());
        getName();

        // To get the name block the main thread
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

package com.specification.reactive.mono;

import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

public class MonoFromCallable {

    @Test
    public void monoFromCallableTest() {

        Callable<String> stringCallable = () -> getName();

        Mono<String> stringMono = Mono.fromCallable(stringCallable);
        stringMono.subscribe(
                ReactiveSpecificationUtil.onNext()
        );
    }

    private static String getName() {
        System.out.println("Generating Name: ");
        return ReactiveSpecificationUtil.faker().name().fullName();
    }
}

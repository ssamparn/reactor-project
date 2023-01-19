package com.specification.reactive.mono;

import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

public class MonoFromFuture {

    @Test
    public void monoFromFutureTest() {
        Mono.fromFuture(MonoFromFuture::getName)
                .subscribe(ReactiveSpecificationUtil.onNext());
        ReactiveSpecificationUtil.sleepSeconds(1);
    }

    private static CompletableFuture<String> getName() {
        return CompletableFuture.supplyAsync(() -> ReactiveSpecificationUtil.faker().name().fullName());
    }
}

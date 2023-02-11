package com.specification.reactive.reactivestreams.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

public class MonoFromFuture {

    @Test
    public void mono_from_future_test() {
        Mono.fromFuture(MonoFromFuture::getName)
                .subscribe(RsUtil.onNext());
        RsUtil.sleepSeconds(1);
    }

    private static CompletableFuture<String> getName() {
        return CompletableFuture.supplyAsync(() -> RsUtil.faker().name().fullName());
    }
}

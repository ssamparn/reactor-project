package com.specification.reactive.reactivestreams.publisher.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class MonoFromFuture {

    /* *
     * Mono.fromFuture(CompletableFuture): Provides a hot publisher.
     * Mono.fromFuture(Supplier<CompletableFuture>): Provides a cold publisher.
     * */
    @Test
    public void mono_from_future_hot_publisher_test() {
        Mono.fromFuture(getName()) // hot publisher
                .subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        RsUtil.sleepSeconds(1); // blocking the thread as CompletableFuture uses a separate thread pool (common fork join pool)
    }

    @Test
    public void mono_from_future_cold_publisher_test() {
        Mono.fromFuture(() -> getName()) // cold publisher
                .subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());

        RsUtil.sleepSeconds(1); // blocking the thread as CompletableFuture uses a separate thread pool (common fork join pool)
    }

    private static CompletableFuture<String> getName() {
        return CompletableFuture.supplyAsync(() -> {
            log.info("generating names");
            return RsUtil.faker().name().fullName();
        });
    }
}

package com.specification.reactive.reactivestreams.publisher.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class MonoFromFuture {

    /**
     * Allows to convert a Future or CompletableFuture into a Mono.
     * This is particularly useful when you need to integrate legacy asynchronous code with reactive programming.
     * Mono.fromFuture(CompletableFuture): Provides a hot publisher.
     * Mono.fromFuture(Supplier<CompletableFuture>): Provides a cold publisher.
     * Common use cases:
     *  1. Wrapping Legacy Code: Integrating legacy code with Mono.fromFuture() is a practical approach to modernize asynchronous operations without extensive rewrites. Suppose you have legacy code that returns a Future, and you want to wrap it in a Mono
     *  2. Integrating Asynchronous API Calls: Suppose you have an asynchronous API call that returns a CompletableFuture, and you want to convert it into a Mono for further reactive processing:
     *  3. Database Operations: You can use Mono.fromFuture() to handle asynchronous database operations, such as fetching data from a database:
     *  4. Combining with Other Reactive Types: You can use Mono.fromFuture() to combine a future with other reactive types.
     *  5. Combining Multiple Futures: You can combine multiple CompletableFuture instances into a single Mono using Mono.fromFuture()
     *  5. Error Handling: Mono.fromFuture() can be used to handle errors effectively
     *  6. Handle Blocking Operations in a Non-Blocking manner: Legacy code often involves blocking operations. You can use Mono.fromFuture() with a scheduler to handle these operations in a non-blocking manner.
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

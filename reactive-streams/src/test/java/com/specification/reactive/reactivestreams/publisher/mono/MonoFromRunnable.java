package com.specification.reactive.reactivestreams.publisher.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

@Slf4j
public class MonoFromRunnable {

    /**
     * Use Mono.fromRunnable() if we have to emit empty signal after some method invocation.
     * Basically Mono.fromRunnable() creates a Mono that completes empty once the provided runnable task has been executed.
     * This is useful when you want to execute a side effect or a piece of code that doesn't return a value, but you still want to integrate it into a reactive pipeline.
     *
     * Note: Mono.fromRunnable() provides a Cold Publisher.
     * */
    @Test
    public void mono_from_runnable_test() {
        Mono.fromRunnable(() -> timeConsumingProcess())
                .subscribe(RsUtil.onNext(),
                        RsUtil.onError(),
                        () -> log.info("Process is done. Sending eMails...."));
    }

    private static void timeConsumingProcess() {
        RsUtil.sleepSeconds(1);
        log.info("Operation Complete!");
    }

    @Test
    public void mono_from_runnable_vs_mono_from_supplier_test() {
        getProductName(2)
                .subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
    }

    private static Mono<String> getProductName(int productId) {
        if (productId == 1) {
            return Mono.fromSupplier(() -> RsUtil.faker().commerce().productName());
        }
        return Mono.fromRunnable(() -> notifyBusiness(productId)); // sends an Mono.empty() signal but after doing some operation.
    }

    private static void notifyBusiness(int productId) {
        log.info("notifying business on unavailable product: {}", productId);
    }

    /**
     * Use Case: Logging
     * You might use Mono.fromRunnable() for logging or other side effects that don't need to return a value.
     * */
    @Test
    public void mono_from_runnable_usecase_test() {
        Mono<String> helloWorldMono = Mono.just("Hello, World")
                .doOnNext(value -> System.out.println("Processing: " + value))
                .then(Mono.fromRunnable(() -> System.out.println("Process is done. Sending eMails....")));

        helloWorldMono.subscribe(
                RsUtil.onNext(),
                RsUtil.onError(),
                () -> log.info("Done"));
    }

    /**
     * Mono.fromRunnable() is useful for integrating side-effects into a reactive pipeline.
     * It creates a Mono<Void> since Runnable does not return a value.
     * It can be used for logging, chaining side-effects, or any other non-returning operations.
     * Different usecases:
     *   1. Logging
     *   2. Metrics Collection
     *   3. Cleanup Operations
     *   4. Notification
     *   5. Side effects in Conditional Logic
     *   6. Chaining side effects
     *   7. Error Handling
     *   8. Database Operations
     * */
    @Test
    public void mono_from_runnable_usecase_chaining_side_effects_test() {
        Mono<Void> chaningSideEffectsMono = Mono.fromRunnable(() -> System.out.println("First side-effect"))
                .then(Mono.fromRunnable(() -> System.out.println("Second side-effect")))
                .then(Mono.fromRunnable(() -> System.out.println("Third side-effect")));

        chaningSideEffectsMono.subscribe(
                RsUtil.onNext(),
                RsUtil.onError(),
                () -> log.info("All side-effects completed"));
    }

}

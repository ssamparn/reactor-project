package com.specification.reactive.reactivestreams.publisher.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

@Slf4j
public class MonoFromRunnable {

    /**
     * Use Mono.fromRunnable() if we have to emit empty signal after some method invocation.
     * Basically Mono.fromRunnable() creates a Mono that completes empty once the provided Runnable has been executed.
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
}

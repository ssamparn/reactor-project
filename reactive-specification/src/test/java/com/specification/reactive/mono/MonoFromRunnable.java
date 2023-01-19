package com.specification.reactive.mono;

import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoFromRunnable {

    @Test
    public void monoFromRunnableTest() {
        Mono.fromRunnable(timeConsumingProcess())
                .subscribe(ReactiveSpecificationUtil.onNext(),
                        ReactiveSpecificationUtil.onError(),
                        ReactiveSpecificationUtil.onComplete());
    }

    private static Runnable timeConsumingProcess() {
        return () -> {
            ReactiveSpecificationUtil.sleepSeconds(3);
            System.out.println("Operation Complete!");
        };
    }
}

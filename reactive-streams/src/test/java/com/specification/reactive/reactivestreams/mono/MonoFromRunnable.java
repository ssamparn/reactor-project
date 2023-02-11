package com.specification.reactive.reactivestreams.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoFromRunnable {

    @Test
    public void mono_from_runnable_test() {
        Mono.fromRunnable(timeConsumingProcess())
                .subscribe(RsUtil.onNext(),
                        RsUtil.onError(),
                        RsUtil.onComplete());
    }

    private static Runnable timeConsumingProcess() {
        return () -> {
            RsUtil.sleepSeconds(3);
            System.out.println("Operation Complete!");
        };
    }
}

package com.specification.reactive.reactivestreams.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

@Slf4j
public class MonoFromRunnable {

    @Test
    public void mono_from_runnable_test() {
        Mono.fromRunnable(timeConsumingProcess())
                .subscribe(RsUtil.onNext(),
                        RsUtil.onError(),
                        () -> {
                            System.out.println("Process is done. Sending eMails....");
                        }); // subscribe() accepts a Runnable. this Runnable depends on the timeConsumingProcess Runnable.
    }

    private static Runnable timeConsumingProcess() {
        return () -> {
            RsUtil.sleepSeconds(3);
            log.info("Operation Complete!");
        };
    }
}

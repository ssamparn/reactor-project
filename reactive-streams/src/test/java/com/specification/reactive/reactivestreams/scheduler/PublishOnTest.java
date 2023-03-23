package com.specification.reactive.reactivestreams.scheduler;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class PublishOnTest {

    // If we know that some operations we want to perform on a Flux or Mono can be time-consuming, we probably don’t want to block the thread that started the execution.
    // For this purpose, we can instruct the Reactor to use a different Scheduler.

    @Test
    public void publish_on_test() {
        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    for (int i = 0; i < 20; i++) {
                        fluxSink.next(i);
                    }
                    fluxSink.complete();
                })
                .doOnNext(v -> printThreadName("next " + v));

        flux
                .publishOn(Schedulers.boundedElastic())
                .subscribe(v -> printThreadName("sub " + v));

        RsUtil.sleepSeconds(2);
    }

    private void printThreadName(String message) {
        System.out.println(message + "\t\t: Thread : " + Thread.currentThread().getName());
    }

    @Test
    public void multiple_publish_on_test() {
        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    for (int i = 0; i < 20; i++) {
                        fluxSink.next(i);
                    }
                    fluxSink.complete();
                })
                .doOnNext(v -> printThreadName("next " + v));

        flux
            .publishOn(Schedulers.boundedElastic())
            .doOnNext(v -> printThreadName("next " + v)) // Assuming this is IO intensive task e.g: Database calls
            .publishOn(Schedulers.parallel())
            .subscribe(v -> printThreadName("sub " + v)); // Assuming this is CPU intensive task e.g: Compute intensive tasks

        RsUtil.sleepSeconds(2);
    }
}

package com.specification.reactive.reactivestreams.scheduler;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class DefaultThreadTest {

    @Test
    public void printThreadNameTest() {
        // Default behavior of Reactive Stream. All executed in main thread.
        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    fluxSink.next(1);
                })
                .doOnNext(i -> printThreadName("next " + i));

        flux.subscribe(v -> printThreadName("sub " + v));
    }

    @Test
    public void defaultBehaviorOfReactorExecutionModel() {
        // Default Behavior Of Reactor Execution Model: The same thread that performs a subscription will be used for the whole pipeline execution.
        Flux<String> cityFlux = Flux.just("New York", "London", "Paris", "Amsterdam")
                .map(String::toUpperCase)
                .filter(cityName -> cityName.length() <= 8)
                .map(cityName -> cityName.concat(" City"))
                .log();

        cityFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        // The whole pipeline was executed by the same thread that started the subscription, the main thread.
    }


    @Test
    public void printThreadNameWithRunnableTest() {
        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    fluxSink.next(1);
                })
                .doOnNext(i -> printThreadName("next " + i));

        Runnable runnable = () -> flux.subscribe(v -> printThreadName("sub " + v));

        for (int i = 0; i < 2; i++) {
            new Thread(runnable).start();
        }

        RsUtil.sleepSeconds(5);
    }

    private void printThreadName(String message) {
        System.out.println(message + "\t\t: Thread : " + Thread.currentThread().getName());
    }
}

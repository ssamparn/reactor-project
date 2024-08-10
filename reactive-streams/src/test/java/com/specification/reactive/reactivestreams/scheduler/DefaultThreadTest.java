package com.specification.reactive.reactivestreams.scheduler;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

/* *
 * By default, the current thread does all the execution
 * */
@Slf4j
public class DefaultThreadTest {

    @Test
    public void reactive_stream_run_on_main_thread_by_default_test() {
        // Default behavior of Reactive Stream. All executed in main thread.
        Flux<Object> flux = Flux.generate(fluxSink -> {
                log.info("generate() running on thread: {}", Thread.currentThread().getName());
                    fluxSink.next(1);
                })
                .take(2)
                .doOnNext(i -> log.info("next() running on thread: {}", Thread.currentThread().getName()));

        flux.subscribe(v -> log.info("subscribe(1) running on thread: {}", Thread.currentThread().getName()));
        // let's attach another subscriber
        flux.subscribe(v -> log.info("subscribe(2) running on thread: {}", Thread.currentThread().getName()));
        // the result is the same. all the events executed in the main thread.
    }

    /* *
     * Default Behavior Of Reactor Execution Model: The same thread that performs a subscription will be used for the whole pipeline execution.
     * */
    @Test
    public void default_behavior_of_reactor_execution_model_test() {
        Flux<String> cityFlux = Flux.just("New York", "London", "Paris", "Amsterdam")
                .map(String::toUpperCase)
                .filter(cityName -> cityName.length() <= 8)
                .map(cityName -> cityName.concat(" City"));

        cityFlux.subscribe(RsUtil.onNext(), RsUtil.onError(), RsUtil.onComplete());
        // The whole pipeline was executed by the same thread that started the subscription, the main thread.
    }


    @Test
    public void default_behavior_of_reactor_execution_model_with_single_subscriber_with_runnable_test() {
        Flux<Object> flux = Flux.generate(fluxSink -> {
                    log.info("generate() running on thread: {}", Thread.currentThread().getName());
                    fluxSink.next(1);
                })
                .take(5)
                .doOnNext(i -> log.info("next() running on thread: {}", Thread.currentThread().getName()));

        Runnable runnable = () -> flux.subscribe(v -> log.info("subscribe() running on thread: {}", Thread.currentThread().getName()));

        Thread.ofPlatform().start(runnable); // subscribe to the flux publisher once.
        // because of runnable, since we are asking to subscribe in a different thread apart from main thread, its being subscribed in a different thread.
        // But its one thread (Thread-0) which takes care of all the executions.

        RsUtil.sleepSeconds(1);
    }

    @Test
    public void default_behavior_of_reactor_execution_model_with_multiple_subscriber_with_runnable_test() {
        Flux<Object> flux = Flux.generate(fluxSink -> {
                    log.info("generate() running on thread: {}", Thread.currentThread().getName());
                    fluxSink.next(1);
                })
                .take(5)
                .doOnNext(i -> log.info("next() running on thread: {}", Thread.currentThread().getName()));

        Runnable runnable = () -> flux.subscribe(v -> log.info("subscribe() running on thread: {}", Thread.currentThread().getName()));

        for (int i = 0; i < 5; i++) {
            Thread.ofPlatform().start(runnable); // subscribe to the flux publisher once
        }
        // because of runnable, since we are asking to subscribe in a different thread apart from main thread, its being subscribed in a different thread.
        // But now it's a set of 5 threads running in parallel which takes care of all the executions.

        RsUtil.sleepSeconds(1);
    }
}

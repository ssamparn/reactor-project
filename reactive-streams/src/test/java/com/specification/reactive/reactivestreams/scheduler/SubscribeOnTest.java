package com.specification.reactive.reactivestreams.scheduler;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class SubscribeOnTest {

    // If we know that some operations we want to perform on a Flux or Mono can be time-consuming, we probably don’t want to block the thread that started the execution.
    // For this purpose, we can instruct the Reactor to use a different Scheduler.

    // Schedulers.boundedElastic():
    // It has a bounded elastic thread pool of workers.
    // The number of threads can grow based on the need.
    // The number of threads can be much bigger than the number of CPU cores.
    // Used mainly for making blocking I/O calls, Network/Time-Consuming calls


    // Schedulers.parallel():
    // It has a fixed pool of workers.
    // The number of threads is equivalent to the number of CPU cores.
    // Useful for CPU intensive tasks.

    // Schedulers.single():
    // Reuses the same thread for all callers
    // A single dedicated thread for one-off tasks.

    // Schedulers.immediate():
    // Uses the current Thread

    // subscribeOn: for upstream
    // publishOn: for downstream

    @Test
    public void subscribe_on_test() {

        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    fluxSink.next(1);
                })
                .doOnNext(i -> printThreadName("next " + i));

        flux
            .doFirst(() -> printThreadName("first 2"))
            .subscribeOn(Schedulers.boundedElastic())
            .doFirst(() -> printThreadName("first 1"))
            .subscribe(v -> printThreadName("sub " + v));
    }

    private void printThreadName(String message) {
        System.out.println(message + "\t\t: Thread name : " + Thread.currentThread().getName());
    }

    @Test
    public void subscribe_on_with_runnable_test() {
        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    fluxSink.next(1);
                })
                .doOnNext(i -> printThreadName("next " + i));

        Runnable runnable = () -> flux
            .doFirst(() -> printThreadName("first 2"))
            .subscribeOn(Schedulers.boundedElastic())
            .doFirst(() -> printThreadName("first 1"))
            .subscribe(v -> printThreadName("sub " + v));

        for (int i = 0; i < 2; i++) {
            new Thread(runnable).start();
        }

        RsUtil.sleepSeconds(5);
    }

    @Test
    public void subscribe_on_with_multiple_schedulers_test() {
        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    fluxSink.next(1);
                })
                .subscribeOn(Schedulers.parallel())
                .doOnNext(i -> printThreadName("next " + i));

        Runnable runnable = () -> flux
            .doFirst(() -> printThreadName("first 2"))
            .subscribeOn(Schedulers.boundedElastic())
            .doFirst(() -> printThreadName("first 1"))
            .subscribe(v -> printThreadName("sub " + v));

        for (int i = 0; i < 2; i++) {
            new Thread(runnable).start();
        }

        RsUtil.sleepSeconds(5);
        // If you have multiple subscribeOn(), the one closest to the publisher will take precedence.
    }

    @Test
    public void subscribe_on_with_multiple_items_test() {
        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    for (int i = 0; i < 20; i++) {
                        fluxSink.next(i);
                    }
                    fluxSink.complete();
                })
                .doOnNext(v -> printThreadName("next " + v));

        flux
            .subscribeOn(Schedulers.parallel())
            .subscribe(v -> printThreadName("sub " + v));

        RsUtil.sleepSeconds(2);
        // Now with multiple items emitted, all items are emitted in the same thread.
        // All the operations are always executed in sequential. Data is processed one by one via 1 thread in the ThreadPool for a Subscriber.
        // Schedulers.parallel() is a thread pool for CPU tasks. It does not mean parallel execution.

    }

    @Test
    public void subscribe_on_with_multiple_items_test2() {
        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    for (int i = 0; i < 20; i++) {
                        fluxSink.next(i);
                    }
                    fluxSink.complete();
                })
                .doOnNext(v -> printThreadName("next " + v));

        Runnable runnable = () -> flux
                .subscribeOn(Schedulers.parallel())
                .subscribe(v -> printThreadName("sub " + v));

        for (int i = 0; i < 5; i++) {
            new Thread(runnable).start();
        }

        RsUtil.sleepSeconds(2);
    }
}

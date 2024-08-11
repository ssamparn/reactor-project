package com.specification.reactive.reactivestreams.scheduler;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;


/**
 * publishOn() is for downstream.
 * subscribeOn() is for upstream.
 * */

@Slf4j
public class PublishOnSubscribeOnTest {

    /* *
     * What if we have both publishOn and subscribeOn operators in a chain.
     */
    @Test
    public void multiple_subscribeOn_publishOn_schedulers_scheduled_by_main_thread_test() {
        Flux<Object> flux = Flux.create(
                sink -> {
                    for (int i = 1; i < 3; i++) {
                        log.info("generating: {}", i); // executed by bounded elastic thread pool
                        sink.next(i);
                    }
                    sink.complete();
                })
                .publishOn(Schedulers.parallel()) // While subscription request goes from bottom to top, it will not have any impact. The publishOn switches the whole sequence to a parallel thread pool while events flows from top to bottom.
                .doOnNext(value -> log.info("value received: {}", value)) // Will be executed by the parallel thread pool.
                .doFirst(() -> log.info("first-1")) // Run by the thread of bounded elastic thread pool
                .subscribeOn(Schedulers.boundedElastic()) // While subscription request goes from bottom to top, subscribeOn() will change the execution context.
                .doFirst(() -> log.info("first-2 ")); // Will be run by the thread which started the subscription, that is the main thread

        flux.subscribe(RsUtil.subscriber()); // The subscriber get executed on the thread pool provided in publishOn() closest to the subscriber that is scheduler B. That is the latest execution context.

        RsUtil.sleepSeconds(1);
    }

    @Test
    public void multiple_subscribeOn_publishOn_schedulers_scheduled_by_anonymous_thread_test() {
        Flux<Object> flux = Flux.create(
                        sink -> {
                            for (int i = 1; i < 3; i++) {
                                log.info("generating: {}", i); // executed by bounded elastic thread pool
                                sink.next(i);
                            }
                            sink.complete();
                        })
                .publishOn(Schedulers.parallel()) // While subscription request goes from bottom to top, it will not have any impact. The publishOn switches the whole sequence to a parallel thread pool while events flows from top to bottom.
                .doOnNext(value -> log.info("value received: {}", value)) // Will be executed by the parallel thread pool.
                .doFirst(() -> log.info("first-1")) // Run by the thread of bounded elastic thread pool
                .subscribeOn(Schedulers.boundedElastic()) // While subscription request goes from bottom to top, subscribeOn() will change the execution context.
                .doFirst(() -> log.info("first-2 ")); // Will be run by the thread which started the subscription, that is the Thread-0 (anonymous) thread

        Runnable runnable = () -> flux.subscribe(RsUtil.subscriber()); // The subscriber get executed on the thread pool provided in publishOn() closest to the subscriber that is scheduler B. That is the latest execution context.

        Thread.ofPlatform().start(runnable); // This thread is the one where the subscription happens.

        RsUtil.sleepSeconds(1);
    }
}

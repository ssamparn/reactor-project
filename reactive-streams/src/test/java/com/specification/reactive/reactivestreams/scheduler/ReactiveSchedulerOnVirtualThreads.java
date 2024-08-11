package com.specification.reactive.reactivestreams.scheduler;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/* *
 * Project Reactor supports virtual threads, in case we want to use them for Blocking I/O. Not for CPU intensive tasks.
 * System.setProperty("reactor.schedulers.defaultBoundedElasticOnVirtualThreads", "true");
 *
 * So bounded elastic thread pool can make use of virtual threads if we set the above system property to true.
 * */

@Slf4j
public class ReactiveSchedulerOnVirtualThreads {

    @Test
    public void reactive_bounded_elastic_thread_pool_on_virtual_threads_test() {

        System.setProperty("reactor.schedulers.defaultBoundedElasticOnVirtualThreads", "true"); // comment this to check if the bounded elastic thread pool is configured of virtual threads or not

        Flux<Object> musicFlux = Flux.generate(
                        () -> 1,
                        (state, synchronousSink) -> {
                            String music = RsUtil.faker().music().genre();
                            log.info("generated music: {}", music);
                            synchronousSink.next(music);
                            if (music.equalsIgnoreCase("Rock")) {
                                synchronousSink.complete();
                            }
                            return state + 1;
                        })
                .doOnNext((music) -> log.info("Music received: {}", music))
                .doFirst(() -> log.info("first-1 isVirtual Thread: {}", Thread.currentThread().isVirtual())) // By default, the bounded elastic thread pool is not configured of virtual threads. In the future, they might change it to run on virtual threads.
                .subscribeOn(Schedulers.boundedElastic())
                .doFirst(() -> log.info("first-2 isVirtual Thread: {}", Thread.currentThread().isVirtual()));

        Runnable musicRunnable = () -> musicFlux.subscribe(RsUtil.subscriber());

        Thread.ofPlatform().start(musicRunnable);

        RsUtil.sleepSeconds(1);
    }

}

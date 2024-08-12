package com.specification.reactive.reactivestreams.scheduler;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Parallel Processing of Events:
 *    parallel() + runOn(Schedulers or Thread Pool) for parallel processing of events.
 *
 *    Note: Often times we don't really need this. Always prefer non-blocking IO for network calls.
 * */
@Slf4j
public class ParallelScheduling {

    @Test
    public void sequential_processing_test() {
        Flux.<String>create(fluxSink -> {
            for (int i = 1; i < 5; i++) {
                String product = RsUtil.faker().commerce().productName();
                fluxSink.next(product);
            }
            fluxSink.complete();
        })
        .map(ParallelScheduling::timeConsumingProcessing)
        .subscribe(RsUtil.subscriber()); // without parallel execution, processing will be slow and synchronous
    }

    @Test
    public void parallel_scheduling_with_runOn_test() {
        Flux.<String>create(fluxSink -> {
                    for (int i = 1; i < 5; i++) {
                        String product = RsUtil.faker().commerce().productName();
                        fluxSink.next(product);
                    }
                    fluxSink.complete();
                })
                .doFirst(() -> log.info("first-1")) // Executed by main thread while subscription signal goes from bottom to top (subscriber to publisher)
                .doFirst(() -> log.info("first-2")) // Executed by main thread while subscription signal goes from bottom to top (subscriber to publisher)
                .doOnNext(product -> log.info("product received: {}", product)) // Executed by main thread while events starts flowing from top to bottom (publisher to subscriber)
                .parallel()
                .runOn(Schedulers.parallel()) // While the subscription signal goes from bottom to top, it will ignore the runOn(). Thread Pool will change & main thread will offload the task to the parallel thread pool while events starts flowing from top to bottom (publisher to subscriber). So the operator below it will run in the thread pool configured in runOn().
                .map(ParallelScheduling::timeConsumingProcessing)
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void parallel_scheduling_with_publishOn_test() {
        Flux.<String>create(fluxSink -> {
                    for (int i = 1; i < 5; i++) {
                        String product = RsUtil.faker().commerce().productName();
                        fluxSink.next(product);
                    }
                    fluxSink.complete();
                })
                .doFirst(() -> log.info("first-1")) // Executed by main thread while subscription signal goes from bottom to top (subscriber to publisher)
                .doFirst(() -> log.info("first-2")) // Executed by main thread while subscription signal goes from bottom to top (subscriber to publisher)
                .doOnNext(product -> log.info("product received: {}", product)) // Executed by main thread while events starts flowing from top to bottom (publisher to subscriber)
                .publishOn(Schedulers.parallel()) // While the subscription signal goes from bottom to top, it will ignore the publishOn(). Thread Pool will change & main thread will offload the task to the parallel thread pool while events starts flowing from top to bottom (publisher to subscriber). So the operator below it will run in the thread pool configured in publishOn().
                .map(ParallelScheduling::timeConsumingProcessing)
                .subscribe(RsUtil.subscriber());
        RsUtil.sleepSeconds(2);

        // However, with publishOn(Schedulers.parallel()), the processing will still be sequential. We can not achieve parallel processing with Schedulers.parallel().
        // Schedulers.parallel() is only suitable for CPU intensive operations, and not for parallel processing.
    }

    // simulate a time-consuming task processing
    private static String timeConsumingProcessing(String product) {
        log.info("time consuming task: {}", product);
        RsUtil.sleepSeconds(1);
        return product.concat("-processed");
    }

}

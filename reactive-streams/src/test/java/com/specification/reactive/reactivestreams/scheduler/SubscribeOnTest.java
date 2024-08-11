package com.specification.reactive.reactivestreams.scheduler;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.stream.IntStream;

/**
 * Project Reactor is concurrency agnostic.
 * It does not enforce a concurrency model, it leaves that to the developer.
 * By default, the execution happens in the thread of the caller to subscribe() method.
 * */
@Slf4j
public class SubscribeOnTest {

    @Test
    public void concurrency_agnostic_test() {
        Flux<Integer> integerFlux = Flux.range(1, 5);

        for (int i = 1; i <= 2; i++) {
            int finalI = i;
            integerFlux.subscribe(integer -> log.info("Consumer Id: " + finalI + " processed " + integer + " using thread: " + Thread.currentThread().getName()));
        }
        // Here all the logs are on the main thread, which is the thread of the caller to subscribe() method.
    }

    /**
     * Rather than enforcing a concurrency model, developers can control how the code should be executed in threads.
     * In Reactor the execution context is determined by the used Scheduler, which is an interface and the Schedulers provides implementation with static methods.
     * We can create different kinds of schedulers in Reactor.
     *
     * subscribeOn() is applied to the subscription process (for upstream)
     * If you place a subscribeOn() in a chain, it affects the source emission in the entire chain.
     *
     * e.g: Let's imagine a publisher P, and subscriber S subscribes to it and there are numerous operators(o1, o2, o3) in between.
     * Let's understand difference between creating and executing. Creating a Flux is different from executing a Flux.
     *      Flux.just(1, 2, 3): We are creating a Flux
     *      Flux.just(1, 2, 3).subscribe(): We are executing a Flux
     *
     *           Publisher
     *              |
     *              o1
     *              |
     *              o2
     *              |
     *              o3
     *              |
     *           subscribeOn
     *              |
     *           Subscriber
     *
     *  So the current thread will create the reactive pipeline with all the operators.
     *  These operators are nothing but Decorated Publishers. Each one of them behaves as a producer and subscriber at the same time.
     *  When the current thread subscribes, the execution control goes from bottom to top. That is from subscriber to publisher. But the moment it sees a subscribeOn()
     *  while going from bottom to top, what current thread will think that ...Ahh!! beyond this point it's not my job to execute and I will have to give this task to
     *  the requested thread pool or scheduler (which ever thread pool developer configures).
     *
     *  So it will simply off loads the task to the configured scheduler. Then the thread pool will start execution through the operator chains, reach to publisher and
     *  carry events downstream to the subscriber.
     * */

    @Test
    public void subscribeOn_scheduler_test() {
        Flux<Object> nameFlux = Flux.generate(fluxSink -> {
            String name = RsUtil.faker().name().fullName();
            log.info("Name generated: {}", name);
            fluxSink.next(name);
        })
        .take(5)
        .doOnNext(name -> log.info("Name received: {}", name));

        nameFlux
                .doFirst(() -> log.info("first-1"))
                .subscribeOn(Schedulers.boundedElastic()) // Comment this line to see the behavior. Without subscribeOn, execution will be done on main thread.
                // If we introduce subscribeOn here then first-2 will be executed by main thread and when it encounters subscribeOn(), main thread will be offloaded
                // and a new boundedElastic thread pool will assume taking responsibilities of the executions.
                .doFirst(() -> log.info("first-2"))
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(1);
    }

    @Test
    public void subscribe_on_test() {
        Flux<Object> flux = Flux.create(fluxSink -> {
            log.info("create");
            fluxSink.next(1);
        })
        .doOnNext(value -> log.info("value emitted: {}", value));

        flux
            .doFirst(() -> log.info("first-2"))
            .subscribeOn(Schedulers.boundedElastic())
            .doFirst(() -> log.info("first-1"))
            .subscribe(value -> log.info("value received: {}", value));
    }

    @Test
    public void subscribe_on_with_runnable_test() {
        Flux<Object> flux = Flux.create(fluxSink -> {
                for (int i = 1; i < 3; i++) {
                    log.info("generating: {}", i);
                    fluxSink.next(i);
                }
                fluxSink.complete();
        })
        .doOnNext(value -> log.info("value: {}", value));

        Runnable runnable = () -> flux
                .doFirst(() -> log.info("first-1")) // This is after boundedElastic(), hence will be executed by boundedElastic thread pool.
                .subscribeOn(Schedulers.boundedElastic())
                .doFirst(() -> log.info("first-2")) // first-2 will be printed by 2 different threads because of runnable in for loop.
                .subscribe(v -> log.info("subscriber()"));

        for (int i = 0; i < 2; i++) {
            Thread.ofPlatform().start(runnable);
        }

        RsUtil.sleepSeconds(1);
    }

    /* *
     * What if we have multiple subscribeOn operators in a chain?
     * We can have multiple subscribeOn() in a chain. In that case the closest to the source or publisher will take precedence
     * */

    @Test
    public void subscribe_on_with_multiple_schedulers_test() {
        Flux<Object> flux = Flux.generate(fluxSink -> {
                    log.info("create");
                    fluxSink.next(1);
                })
                .take(5)
                .subscribeOn(Schedulers.parallel()) // parallel thread pool will take precedence, as it is closest to the publisher
                .doOnNext(i -> log.info("value received: {}",i));

        Runnable runnable = () -> flux
                .doFirst(() -> log.info("first-2")) // first-2 will be printed by 2 different threads of boundedElastic. boundedElastic-1 and boundedElastic-2
                .subscribeOn(Schedulers.boundedElastic())
                .doFirst(() -> log.info("first-1")) // first-1 will be printed by 2 different threads. Thread-0 and Thread-1
                .subscribe(value -> log.info("subscriber: {}", value));

        for (int i = 0; i < 2; i++) {
            new Thread(runnable).start();
        }

        RsUtil.sleepSeconds(1);
        // If you have multiple subscribeOn(), the one closest to the publisher will take precedence.
    }

    @Test
    public void multiple_subscribeOn_schedulers_test() {

        Scheduler schedulerA = Schedulers.newBoundedElastic(5, 5, "scheduler-A-boundedElastic");
        Scheduler schedulerB = Schedulers.newParallel("scheduler-B-parallel");

        Flux<Object> flux = Flux.create(fluxSink -> {
                    for (int i = 1; i < 3; i++) {
                        log.info("generating: {}", i);
                        fluxSink.next(i);
                    }
                    fluxSink.complete();
                })
                .subscribeOn(schedulerB) // This thread pool (scheduler-B-parallel) will take precedence. All the tasks will be executed by this thread pool, as this is closest to the publisher
                .doOnNext(value -> log.info("value: {}", value));

        Runnable runnable = () -> flux
                .doFirst(() -> log.info("first-1")) // This is after boundedElastic(), hence will be executed by scheduler-A-boundedElastic thread pool.
                .subscribeOn(schedulerA)
                .doFirst(() -> log.info("first-2")) // first-2 will be printed by 2 different threads because of runnable in for loop.
                .subscribe(RsUtil.subscriber("subscribeOn-subscriber"));

        for (int i = 0; i < 2; i++) {
            Thread.ofPlatform().start(runnable);
        }

        RsUtil.sleepSeconds(1);
        // So if you define multiple subscribeOn operators in a chain, it will use the first one. The one closest to the publisher.
    }

    @Test
    public void subscribeOn_immediate_scheduler_main_test() {
        Flux<Object> objectFlux = Flux.create(fluxSink -> {
                    for (int i = 1; i < 3; i++) {
                        log.info("generating: {}", i);
                        fluxSink.next(i);
                    }
                    fluxSink.complete();
                })
                .doOnNext(value -> log.info("value: {}", value));

        objectFlux
                .doFirst(() -> log.info("first-1")) // This is after immediate(), hence will be executed by the same thread which subscribed to the publisher, that is the main thread
                .subscribeOn(Schedulers.immediate()) // Switch of thread pool and task off loading will not happen as the thread pool configured is Schedulers.immediate()
                .doFirst(() -> log.info("first-2")) // first-2 will be printed by the main thread
                .subscribe(v -> log.info("subscriber()"));

        RsUtil.sleepSeconds(1);
    }

    @Test
    public void subscribeOn_immediate_scheduler_boundedElastic_test() {
        Flux<Object> objectFlux = Flux.create(fluxSink -> {
                    for (int i = 1; i < 3; i++) {
                        log.info("generating: {}", i);
                        fluxSink.next(i);
                    }
                    fluxSink.complete();
                });

        objectFlux
                .subscribeOn(Schedulers.immediate()) // thread pool switch will not occur and the execution will continue in the same bounded elastic thread pool
                .doOnNext(value -> log.info("value: {}", value))
                .doFirst(() -> log.info("first-1")) // first-1 will be executed by the bounded elastic thread pool
                .subscribeOn(Schedulers.boundedElastic()) // Switch of thread pool and task off loading will occur as the thread pool configured is Schedulers.boundedElastic()
                .doFirst(() -> log.info("first-2")) // first-2 will be executed by the main thread
                .subscribe(v -> log.info("subscriber()"));

        RsUtil.sleepSeconds(1);
    }

    @Test
    public void subscribeOn_single_scheduler_test() {
        Flux<Object> objectFlux = Flux.create(fluxSink -> {
            for (int i = 1; i < 3; i++) {
                log.info("generating: {}", i);
                fluxSink.next(i);
            }
            fluxSink.complete();
        });

        objectFlux
                .subscribeOn(Schedulers.single()) // thread pool switch will occur and the execution will continue in the Schedulers.single() thread pool. The rule applies => The one closest to the publisher will take precedence.
                .doOnNext(value -> log.info("value: {}", value))
                .doFirst(() -> log.info("first-1")) // first-1 will be executed by the bounded elastic thread pool
                .subscribeOn(Schedulers.boundedElastic()) // Switch of thread pool and task off loading will occur as the thread pool configured is Schedulers.boundedElastic()
                .doFirst(() -> log.info("first-2")) // first-2 will be executed by the main thread
                .subscribe(v -> log.info("subscriber()"));

        RsUtil.sleepSeconds(1);
    }


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
    public void subscribeOn_parallel_scheduler_test() {
        Flux<Integer> integerFlux = Flux.range(1, 4)
                .map(i -> {
                    log.info("{}", String.format("First Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                })
                .subscribeOn(Schedulers.parallel())
                .map(i -> {
                    log.info("{}", String.format("Second Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                })
                .doFirst(() -> log.info("executed by main thread"));

        integerFlux
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void subscribe_on_with_multiple_items_test() {
        Flux<Object> flux = Flux.create(fluxSink -> {
            log.info("create");
            for (int i = 0; i < 20; i++) {
                fluxSink.next(i);
            }
            fluxSink.complete();
        })
                .doOnNext(value -> log.info("value received: {}", value));

        flux
            .subscribeOn(Schedulers.parallel())
            .subscribe(value -> log.info("subscriber: {}", value));

        RsUtil.sleepSeconds(2);
        // Now with multiple items emitted, all items are emitted in the same thread.
        // All the operations are always executed in sequential. Data is processed one by one via 1 thread in the ThreadPool for a Subscriber.
        // Schedulers.parallel() is a thread pool for CPU tasks. It does not mean parallel execution.
    }

    @Test
    public void subscribe_on_with_multiple_items_test2() {
        Flux<Object> flux = Flux.create(fluxSink -> {
                    log.info("create");
                    for (int i = 0; i < 20; i++) {
                        fluxSink.next(i);
                    }
                    fluxSink.complete();
                })
                .doOnNext(v -> log.info("value received: {} ",v));

        Runnable runnable = () -> flux
                .doFirst(() -> log.info("first-1"))
                .subscribeOn(Schedulers.parallel())
                .doFirst(() -> log.info("first-2"))
                .subscribe(value -> log.info("subscriber received: {}", value));

        for (int i = 0; i < 5; i++) {
            new Thread(runnable).start();
        }

        RsUtil.sleepSeconds(2);
    }
}

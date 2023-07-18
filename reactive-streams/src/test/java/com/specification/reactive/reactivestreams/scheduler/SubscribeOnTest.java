package com.specification.reactive.reactivestreams.scheduler;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.stream.IntStream;

@Slf4j
public class SubscribeOnTest {

    // Project Reactor is concurrency agnostic.
    // Reactor does not enforce a concurrency model, it leaves that to the developer.
    // By default, the execution happens in the thread of the caller to subscribe() method.

    @Test
    public void concurrency_agnostic_test() {
        Flux<Integer> integerFlux = Flux.range(0, 4);

        IntStream.range(1, 5)
                .forEach(value -> integerFlux.subscribe(integer -> System.out.println("Consumer Id: " + value + " processed "
                        + integer + " using thread: " + Thread.currentThread().getName())));
        // Here all the logs are on the main thread., which is the thread of the caller to subscribe() method.
    }

    // Rather than enforcing a concurrency model, developers can control how the code should be executed in threads.
    // In Reactor the execution context is determined by the used Scheduler, which is an interface and the Schedulers provides implementation with static methods.
    // We can create different kinds of schedulers in Reactor.

    // subscribeOn is applied to the subscription process.
    // If you place a subscribeOn() in a chain, it affects the source emission in the entire chain.
    @Test
    public void subscribeOn_single_scheduler_test() {
        Flux<Integer> integerFlux = Flux.range(1, 4)
                .map(i -> {
                    System.out.println(String.format("First Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                })
                .subscribeOn(Schedulers.single())
                .map(i -> {
                    System.out.println(String.format("Second Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                });

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    public void subscribeOn_boundedElastic_scheduler_test() {
        Flux<Integer> integerFlux = Flux.range(1, 4)
                .map(i -> {
                    System.out.println(String.format("First Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(i -> {
                    System.out.println(String.format("Second Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                });

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    public void subscribeOn_parallel_scheduler_test() {
        Flux<Integer> integerFlux = Flux.range(1, 4)
                .map(i -> {
                    System.out.println(String.format("First Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                })
                .subscribeOn(Schedulers.parallel())
                .map(i -> {
                    System.out.println(String.format("Second Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                });

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    public void subscribeOn_immediate_scheduler_test() {
        Flux<Integer> integerFlux = Flux.range(1, 4)
                .map(i -> {
                    System.out.println(String.format("First Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                })
                .subscribeOn(Schedulers.immediate())
                .map(i -> {
                    System.out.println(String.format("Second Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                });

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    // What if we have multiple subscribeOn operators in a chain.
    @Test
    public void multiple_subscribeOn_schedulers_boundedElastic_test() {

        Scheduler schedulerA = Schedulers.newBoundedElastic(5, 5, "schedulerA");
        Scheduler schedulerB = Schedulers.newBoundedElastic(5, 5, "schedulerB");

        Flux<Integer> integerFlux = Flux.range(1, 5)
                .map(i -> {
                    System.out.println(String.format("First map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                })
                .subscribeOn(schedulerA)
                .map(i -> {
                    System.out.println(String.format("Second map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                })
                .subscribeOn(schedulerB)
                .map(i -> {
                    System.out.println(String.format("Third map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                });

        // As you can see, all maps are executed on a thread picked from scheduler A which is used by the first subscribeOn.
        // So if you define multiple subscribeOn operators in a chain, it will use the first one.
        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
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

package com.specification.reactive.reactivestreams.scheduler;

import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class PublishOnTest {

    // Like other operators in general, publishOn() is applied in the middle of a chain.
    // It affects subsequent operators after publishOn().
    // After publishOn() they will be executed on a thread picked from publishOn() scheduler.

    @Test
    public void publishOn_single_scheduler_test() {
        Flux<Integer> integerFlux = Flux.range(1, 4)
                .map(i -> {
                    System.out.println(String.format("First Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                })
                .publishOn(Schedulers.single())
                .map(i -> {
                    System.out.println(String.format("Second Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                });
        // As you can see, only the second map which is placed after publishOn in the chain is executed on the scheduler.
        // pubilshOn() doesn't affect any operator before it.

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    public void publishOn_boundedElastic_scheduler_test() {
        Flux<Integer> integerFlux = Flux.range(1, 4)
                .map(i -> {
                    System.out.println(String.format("First Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    System.out.println(String.format("Second Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                });
        // As you can see, only the second map which is placed after publishOn in the chain is executed on the scheduler.
        // pubilshOn() doesn't affect any operator before it.

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    public void publishOn_parallel_scheduler_test() {
        Flux<Integer> integerFlux = Flux.range(1, 4)
                .map(i -> {
                    System.out.println(String.format("First Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                })
                .publishOn(Schedulers.parallel())
                .map(i -> {
                    System.out.println(String.format("Second Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                });
        // As you can see, only the second map which is placed after publishOn in the chain is executed on the scheduler.
        // pubilshOn() doesn't affect any operator before it.

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    public void publishOn_immediate_scheduler_test() {
        Flux<Integer> integerFlux = Flux.range(1, 4)
                .map(i -> {
                    System.out.println(String.format("First Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                })
                .publishOn(Schedulers.immediate())
                .map(i -> {
                    System.out.println(String.format("Second Map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                });
        // As you can see, only the second map which is placed after publishOn in the chain is executed on the scheduler.
        // pubilshOn() doesn't affect any operator before it.

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    // If there is more than one publishOn in a chain, how will it behave? You can see on the following example and its output.
    @Test
    public void multiple_publishOn_schedulers_boundedElastic_test() {

        Scheduler schedulerA = Schedulers.newBoundedElastic(5, 5, "schedulerA");
        Scheduler schedulerB = Schedulers.newBoundedElastic(5, 5, "schedulerB");

        Flux<Integer> integerFlux = Flux.range(1, 5)
                .map(i -> {
                    System.out.println(String.format("First map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                })
                .publishOn(schedulerA)
                .map(i -> {
                    System.out.println(String.format("Second map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                })
                .publishOn(schedulerB)
                .map(i -> {
                    System.out.println(String.format("Third map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                });

        // The first publishOn() affects the subsequent operators after it, which means it should affect the second and third maps.
        // However, there is another publishOn which affects the third map.
        // The result shows us that the third map uses thread from scheduler B.
        // We can conclude that if there is more than one preceding publishOn() operators, the nearest preceding publishOn will be used.
        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

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

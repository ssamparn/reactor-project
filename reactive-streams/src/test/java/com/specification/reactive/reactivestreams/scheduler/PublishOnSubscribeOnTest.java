package com.specification.reactive.reactivestreams.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@Slf4j
public class PublishOnSubscribeOnTest {

    // What if we have both publishOn and subscribeOn operators in a chain.
    @Test
    public void multiple_subscribeOn_publishOn_schedulers_boundedElastic_test() {

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
                .publishOn(schedulerB)
                .map(i -> {
                    System.out.println(String.format("Third map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                });

        // Initially, subscribeOn schedules all operators on schedulerA.
        // But the presence of publishOn makes the third map which is placed after it to use its scheduler.
        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    public void multiple_publishOn_subscribeOn_schedulers_boundedElastic_test() {

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
                .subscribeOn(schedulerB)
                .map(i -> {
                    System.out.println(String.format("Third map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                });


        // Initially, subscribeOn schedules all operators on schedulerB.
        // But the presence of publishOn makes the second and third map which is placed after it to use its scheduler.
        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    public void multiple_publishOn_subscribeOn_schedulers_boundedElastic_another_test() {

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
                .subscribeOn(schedulerB)
                .map(i -> {
                    System.out.println(String.format("Third map - (%s), Thread: %s", i, Thread.currentThread().getName()));
                    return i;
                })
                .subscribeOn(schedulerA);

        // Very Important:
        // To determine the execution of operators in a chain if you use publishOn or subscribeOn, what you need to do are:
        // 1. First, find the topmost subscribeOn. If found, set all operators in the chain to use a thread picked from that its scheduler.
        // 2. Then, find all publishOn operators from top to bottom. For each, set the subsequent operators below it to use a thread picked from its scheduler.

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

}

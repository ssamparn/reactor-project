package com.specification.reactive.reactivestreams.scheduler;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

/**
 * publishOn():
 *      Let's take the same example.
 *
 *      Let's imagine a publisher P, and subscriber S subscribes to it and there are numerous operators(o1, o2, o3) in between.
 *
 *                  Publisher
 *                     |
 *                     o1
 *                     |
 *                     o2
 *                     |
 *                     o3
 *                     |
 *                 subscribeOn
 *                     |
 *                  Subscriber
 *
 * As we understood in subscribeOn(), as a developer of the publisher (from producer side) he/she chose the best scheduler for their Flux (Publisher) using subscribeOn.
 * We also know the rule, that the closest subscribeOn(Schedulers) thread pool takes precedence and determines how a flux (publisher) should emit items.
 * It also makes sense for them as they are the best people to determine how a publisher works and determine their thread pool accordingly.
 * Now developer of the subscriber S (imagine it is another application), subscribes to the publisher P.
 * Now the data emitted is executed in the subscriber application in a thread pool chosen by the producer.
 * Publisher developer might know which thread pool to chose, but subscriber developer is the best person to judge what should be the thread pool of the data flowing in consumer application.
 * So as a developer of the subscriber (consumer app) he should have the ability to change the thread pool.
 * e.g: Publisher dev might have chosen Schedulers.parallel() for their data execution as the task is CPU intensive, but Subscriber dev may decide it's a time-consuming task. I need to change the thread pool from Schedulers.parallel() to Schedulers.boundedElastic().
 *
 * So what are options for Subscriber application developer?
 * Answer: publishOn()
 *
 * Like other operators in general, publishOn() is applied in the middle of the reactive subscriber chain.
 * It takes signals from upstream (i.e: from publisher) and replays them downstream while executing the tasks on a worker from the associated Scheduler.
 * It affects subsequent operators after publishOn().
 * After publishOn() they will be executed on a thread picked from the thread pool provided in the publishOn() scheduler.
 *
 * In simple terms, if we take above example, So the current thread will create the reactive pipeline with all the operators.
 * These operators are nothing but Decorated Publishers. Each one of them behaves as a producer and subscriber at the same time.
 * When the current thread subscribes, the execution control goes from bottom to top. That is from subscriber to publisher.
 * When it encounters a publishOn() while control goes from bottom to top, it ignores the publishOn(). The execution control (by current thread) will keep going to publisher.
 * Publisher emits the data and current thread takes care of the data execution. Data flows from top to bottom. But the moment it sees a publishOn() while coming down from top to bottom (Publisher --> Subscriber),
 * what current thread will think that ...Ahh!! beyond this point it's not my job to execute the task and I will have to give this task to the requested thread pool or scheduler (which ever thread pool developer configured in publishOn()).
 *
 * So it will simply off loads the task to the configured scheduler. Then the thread pool will start execution through the operator chains and carry events downstream to the subscriber.
 * */

@Slf4j
public class PublishOnTest {

    @Test
    public void publishOn_simple_scheduler_test() {
        Flux<Object> flux = Flux.create(
                sink -> {
                    for (int i = 1; i < 3; i++) {
                        log.info("generating: {}", i);
                        sink.next(i);
                    }
                    sink.complete();
                })
                .doOnNext(value -> log.info("value received: {}", value))
                .doFirst(() -> log.info("first-1")) // Run by the same thread which run first-1
                .publishOn(Schedulers.boundedElastic()) // While subscription request goes from bottom to top, it will not have any impact. The publishOn switches the whole sequence to a boundedElastic thread pool while events flows from top to bottom.
                .doFirst(() -> log.info("first-2 ")); // Will be run by the thread which started the subscription, that is the Thread-0

        Runnable musicRunnable = () -> flux.subscribe(RsUtil.subscriber()); // The subscriber get executed on the thread pool provided in publishOn(). That is the latest execution context, which is the one from publishOn.

        Thread.ofPlatform().start(musicRunnable); // This thread is the one where the subscription happens.

        RsUtil.sleepSeconds(1);
    }

    @Test
    public void publishOn_single_scheduler_test() {
        Flux<Object> flux = Flux.create(
                sink -> {
                    for (int i = 1; i < 3; i++) {
                        log.info("generating: {}", i);
                        sink.next(i);
                    }
                    sink.complete();
                })
                .doOnNext(value -> log.info("value received: {}", value))
                .doFirst(() -> log.info("first-1")) // Run by the same thread which run first-1
                .publishOn(Schedulers.single()) // While subscription request goes from bottom to top, it will not have any impact. The publishOn switches the whole sequence to a single-1 thread while events flows from top to bottom.
                .doFirst(() -> log.info("first-2 ")); // Will be run by the thread which started the subscription, that is the Thread-0

        Runnable musicRunnable = () -> flux.subscribe(RsUtil.subscriber()); // The subscriber get executed on the thread pool provided in publishOn(). That is the latest execution context, which is the one from publishOn.

        Thread.ofPlatform().start(musicRunnable); // This thread is the one where the subscription happens.

        RsUtil.sleepSeconds(1);
    }

    @Test
    public void publishOn_parallel_scheduler_test() {
        Flux<Object> flux = Flux.create(
                sink -> {
                    for (int i = 1; i < 3; i++) {
                        log.info("generating: {}", i);
                        sink.next(i);
                    }
                    sink.complete();
                })
                .doOnNext(value -> log.info("value received: {}", value))
                .doFirst(() -> log.info("first-1")) // Run by the same thread which run first-1
                .publishOn(Schedulers.parallel()) // While subscription request goes from bottom to top, it will not have any impact. The publishOn switches the whole sequence to a parallel thread pool while events flows from top to bottom.
                .doFirst(() -> log.info("first-2 ")); // Will be run by the thread which started the subscription, that is the Thread-0

        Runnable musicRunnable = () -> flux.subscribe(RsUtil.subscriber()); // The subscriber get executed on the thread pool provided in publishOn(). That is the latest execution context, which is the one from publishOn.

        Thread.ofPlatform().start(musicRunnable); // This thread is the one where the subscription happens.

        RsUtil.sleepSeconds(1);
    }

    @Test
    public void publishOn_immediate_scheduler_test() {
        Flux<Object> flux = Flux.create(
                sink -> {
                    for (int i = 1; i < 3; i++) {
                        log.info("generating: {}", i);
                        sink.next(i);
                    }
                    sink.complete();
                })
                .doOnNext(value -> log.info("value received: {}", value))
                .doFirst(() -> log.info("first-1")) // Run by the same thread which run first-1
                .publishOn(Schedulers.immediate()) // While subscription request goes from bottom to top, it will not have any impact. The publishOn does not switch the whole sequence to the provided thread pool while events flows from top to bottom because of Schedulers.immediate(). Means the same thread will continue the execution.
                .doFirst(() -> log.info("first-2 ")); // Will be run by the thread which started the subscription, that is the Thread-0

        Runnable musicRunnable = () -> flux.subscribe(RsUtil.subscriber()); // The subscriber get executed on the thread pool provided in publishOn(). That is the latest execution context, which is the one from publishOn.

        Thread.ofPlatform().start(musicRunnable); // This thread is the one where the subscription happens.

        RsUtil.sleepSeconds(1);
    }

    /* *
     * What if there are multiple publishOn() operators in a chain?
     * We can have multiple publishOn() operators in a chain. In that case, the closest to the subscriber will take precedence.
     */
    @Test
    public void multiple_publishOn_schedulers_boundedElastic_test() {
        Scheduler schedulerA = Schedulers.newBoundedElastic(5, 5, "schedulerA");
        Scheduler schedulerB = Schedulers.newBoundedElastic(5, 5, "schedulerB");

        Flux<Object> flux = Flux.create(
                sink -> {
                    for (int i = 1; i < 3; i++) {
                        log.info("generating: {}", i);
                        sink.next(i);
                    }
                    sink.complete();
                })
                .publishOn(schedulerA)
                .doOnNext(value -> log.info("value received: {}", value)) // Will be executed by the bounded elastic thread pool scheduler A.
                .doFirst(() -> log.info("first-1")) // Run by the same thread which run first-1
                .publishOn(schedulerB) // While subscription request goes from bottom to top, it will not have any impact. The publishOn does not switch the whole sequence to the provided thread pool while events flows from top to bottom because of Schedulers.immediate(). Means the same thread will continue the execution.
                .doFirst(() -> log.info("first-2 ")); // Will be run by the thread which started the subscription, that is the Thread-0

        Runnable musicRunnable = () -> flux.subscribe(RsUtil.subscriber()); // The subscriber get executed on the thread pool provided in publishOn() closest to the subscriber that is scheduler B. That is the latest execution context.

        for (int i = 0; i < 2; i++) {
            Thread.ofPlatform().start(musicRunnable); // This thread is the one where the subscription happens.
        }

        RsUtil.sleepSeconds(1);
        // We can conclude that if there is more than one preceding publishOn() operators, the nearest preceding publishOn will be used.
    }
}

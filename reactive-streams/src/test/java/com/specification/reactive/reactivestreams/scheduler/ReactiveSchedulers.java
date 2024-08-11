package com.specification.reactive.reactivestreams.scheduler;

public class ReactiveSchedulers {

    /* *
     * By default, the current thread does all the execution. That's the default behavior.
     * Default Behavior Of Reactor Execution Model: The same thread that performs a subscription will be used for the whole pipeline execution.
     *
     * While this approach has advantages, it might have many disadvantages. Why??
     * Because I might have 10 CPU's in my machines that means I can spawn 10 threads concurrently. Why only one main thread has to do all the work. I might want to make use of all the power of CPU of my machine.
     *
     * We also saw that we can create a thread (Thread.ofPlatform(provide a subscriptionTask)) ourselves and provide a subscriber to it and the new thread will do the subscription instead of main thread. That might solve the problem,  but we don't want to do that as well.
     * As it is a lot of low level code and thread management.
     *
     * That's why reactor provides a set of thread pools that we can use to schedule tasks. They are called "SCHEDULERS"
     *
     * A Scheduler is nothing but a Thread Pool.
     *
     * ---------------------------------------------------------------------------------
     * |     Scheduler Type       |                    Usage                            |
     * ---------------------------------------------------------------------------------
     * |     boundedElastic       |  Network / time-consuming / Blocking I/O Operations |
     * ---------------------------------------------------------------------------------
     * |       parallel           |             CPU Intensive Tasks                     |
     * ---------------------------------------------------------------------------------
     * |        single            |   A single dedicated thread for one-off tasks       |
     * ---------------------------------------------------------------------------------
     * |       immediate          |              Current Thread                         |
     * ---------------------------------------------------------------------------------
     *
     * Operators for Scheduling:
     * ---------------------------------------
     * |     Operator     |      Usage       |
     * ---------------------------------------
     * |    subscribeOn   |  for Upstream    |
     * ---------------------------------------
     * |    publishOn    |  for Downstream   |
     * ---------------------------------------
     *
     * If we know that some operations we want to perform on a Flux or Mono can be time-consuming, we probably don’t want to block the thread that started the execution.
     * For this purpose, we can instruct the Reactor to use a different Scheduler.
     *
     * Schedulers.boundedElastic():
     * -----------------------------
     *    - It has a bounded elastic thread pool of workers.
     *    - The number of threads = 10 * Number of CPU Cores
     *    - The number of threads can grow based on the need.
     *    - The number of threads can be much bigger than the number of CPU cores.
     *    - Used mainly for making blocking I/O calls, Network or Time-Consuming calls.
     *
     * Schedulers.parallel():
     * --------------------------
     *    - It has a fixed pool of workers.
     *    - The number of threads = Number of CPU cores.
     *    - Useful for CPU intensive tasks.
     *
     * Schedulers.single():
     * -----------------------
     *    - Reuses the same thread for all callers.
     *    - A single dedicated thread for one-off tasks.
     *    - The number of threads = 1
     *
     * Schedulers.immediate():
     * ------------------------
     *    - Uses the current Thread
     *    - The number of threads = 1
     * */
}

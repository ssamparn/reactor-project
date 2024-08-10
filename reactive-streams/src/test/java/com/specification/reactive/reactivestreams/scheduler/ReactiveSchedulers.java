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
     * */
}

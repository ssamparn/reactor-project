package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.custom.subscriber.SubscriberImpl;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

/* *
 * Flux.create() DOES NOT check the downstream demand by default! It is by design.
 * */

@Slf4j
public class FluxCreateDownstreamDemand {

    private SubscriberImpl subscriber = new SubscriberImpl();

    @Test
    public void flux_create_downstream_produce_early_test() {
        // fluxSink considers every emitted item as object, so we have to type cast.

        // publisher implementation
        Flux.<String>create(fluxSink -> {
            for (int i = 0; i < 10; i++) {
                String name = RsUtil.faker().name().firstName();
                log.info("Generated Name: {}", name);
                fluxSink.next(name);
            }
            fluxSink.complete();
        }).subscribe(subscriber);

        // In SubscriberImpl implementation, we don't do any request to publisher onSubscribe(). So we have to make request() explicitly.

        // subscriber implementation
        // subscriber.getSubscription().cancel();

        /* *
         * So when we say the Flux.create() DOES NOT check the downstream demand by default, that means even if we cancel the subscription immediately, publisher still emits all the items upfront (produce early).
         * And after cancelling the subscription, subscriber obviously has not received any item but again publisher still emits all the items upfront.
         * Now you might think this is bad. Now good or bad depends on the business requirement.
         *
         * Sometimes, we think we have to be lazy as much as possible. In most of the cases that is true, but in some cases doing work (publisher emitting all the items upfront) might be good.
         * e.g: Preparing food once in the morning in bulk quantity and eating throughout the day might be efficient than preparing food when you get hungry multiple times during the day. In that case this behavior is helpful.
         *
         * In short, with Flux.create() we get a hot publisher. All the items emitted by Flux.create() gets stored in a queue & subscriber can get it whenever it needs.
         * What is length of the queue? How many items can it store?
         * Answer: It is an unbounded queue. So we can go up to Integer.MAX_VALUE. But if the object size is really huge, we will get Out Of Memory Exception before even reaching that limit.
         *
         * */

        // Uncomment below code block and comment the above subscription cancel i.e: subscriber.getSubscription().cancel();
//         RsUtil.sleepSeconds(1);
//         subscriber.getSubscription().request(2);
//
//         RsUtil.sleepSeconds(1);
//         subscriber.getSubscription().request(2);
//
//         RsUtil.sleepSeconds(1);
//         subscriber.getSubscription().cancel();

        // RsUtil.sleepSeconds(1);
        // subscriber.getSubscription().request(2);

        // After cancel the request to emit items will not work
    }

    /**
     * With onDemand subscription by the subscriber, the publisher will not emit all the items upfront.
     * It will be on demand of subscriber.
     */
    @Test
    public void flux_create_downstream_produce_on_demand_test() {
        // fluxSink considers every emitted item as object, so we have to type cast.

        // publisher implementation
        Flux.<String>create(fluxSink -> {
            fluxSink.onRequest(request -> { // when the subscriber requests for events, at that time this onRequest() callback gets invoked.
                for (int i = 0; i < request && !fluxSink.isCancelled(); i++) {
                    String name = RsUtil.faker().name().firstName();
                    log.info("Generated Name: {}", name);
                    fluxSink.next(name);
                }
            });
        }).subscribe(subscriber);

        // subscriber implementation
        RsUtil.sleepSeconds(1);
        subscriber.getSubscription().request(2);

        RsUtil.sleepSeconds(1);
        subscriber.getSubscription().request(2);

        RsUtil.sleepSeconds(1);
        subscriber.getSubscription().cancel();

        RsUtil.sleepSeconds(1);
        subscriber.getSubscription().request(2);
        // After cancel the request to emit items will not work
    }



}

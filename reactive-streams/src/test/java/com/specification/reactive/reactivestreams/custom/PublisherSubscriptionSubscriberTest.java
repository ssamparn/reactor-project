package com.specification.reactive.reactivestreams.custom;

import com.specification.reactive.reactivestreams.custom.publisher.PublisherImpl;
import com.specification.reactive.reactivestreams.custom.subscriber.SubscriberImpl;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/* *
 * 1. A Publisher will not produce data unless subscriber subscribes to it.
 * 2. A Publisher will produce data only less than or equal (<=) to the subscriber requests events. Publisher can also produce 0 items.
 * 3. A Subscriber can cancel the subscription at any point in time. Publisher should stop at that moment as subscriber is no longer interested in consuming the data.
 * 4. A Publisher can send the error signal to indicate something is wrong. Once error signal is emitted no further data is sent.
 * */

@Slf4j
public class PublisherSubscriptionSubscriberTest {

    @Test
    public void customPublisherWillNotProduceDataUnlessSubscriberSubscribes_Test() {
        PublisherImpl publisher = new PublisherImpl();
        SubscriberImpl subscriber = new SubscriberImpl();

        publisher.subscribe(subscriber);
    }

    @Test
    public void customPublisherWillProduceEventsOnlyIfSubscriberRequests_Test() {
        PublisherImpl publisher = new PublisherImpl();
        SubscriberImpl subscriber = new SubscriberImpl();

        publisher.subscribe(subscriber);

        subscriber.getSubscription().request(3);
        RsUtil.sleepSeconds(1);

        subscriber.getSubscription().request(3);
        RsUtil.sleepSeconds(1);

        subscriber.getSubscription().request(3);
        RsUtil.sleepSeconds(1);

        subscriber.getSubscription().request(3);
    }

    @Test
    public void customSubscriberCanCancelSubscription_Test() {
        PublisherImpl publisher = new PublisherImpl();
        SubscriberImpl subscriber = new SubscriberImpl();

        publisher.subscribe(subscriber);

        subscriber.getSubscription().request(3);
        RsUtil.sleepSeconds(1);

        subscriber.getSubscription().cancel();

        subscriber.getSubscription().request(3);
    }

    @Test
    public void customPublisherCanSendErrorSignal_Test() {
        PublisherImpl publisher = new PublisherImpl();
        SubscriberImpl subscriber = new SubscriberImpl();

        publisher.subscribe(subscriber);

        subscriber.getSubscription().request(12);
        RsUtil.sleepSeconds(1);
        subscriber.getSubscription().request(2);
    }
}

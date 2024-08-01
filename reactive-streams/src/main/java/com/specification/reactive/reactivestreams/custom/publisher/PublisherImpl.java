package com.specification.reactive.reactivestreams.custom.publisher;

import com.specification.reactive.reactivestreams.custom.subscription.SubscriptionImpl;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * This publisher publishes the customer eMail address.
 * */
@Slf4j
public class PublisherImpl implements Publisher<String> {

    @Override
    public void subscribe(Subscriber<? super String> subscriber) {
        Subscription subscription = new SubscriptionImpl(subscriber);
        subscriber.onSubscribe(subscription);
    }
}

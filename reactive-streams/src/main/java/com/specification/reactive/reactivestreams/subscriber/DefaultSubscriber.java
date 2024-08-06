package com.specification.reactive.reactivestreams.subscriber;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@Slf4j
public class DefaultSubscriber<T> implements Subscriber<T> {

    private String subscriberName = "";

    public DefaultSubscriber() {
    }

    public DefaultSubscriber(String subscriberName) {
        this.subscriberName = "Subscribed by Subscriber: " + subscriberName + ".";
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(Long.MAX_VALUE); // make unbounded request
    }

    @Override
    public void onNext(T item) {
        log.info("{} Received Object : {}", this.subscriberName, item);
    }

    @Override
    public void onError(Throwable throwable) {
        log.info("{} Error Thrown during subscription: {}", this.subscriberName, throwable.getMessage());
    }

    @Override
    public void onComplete() {
        log.info("{} Subscription Completed ", this.subscriberName);
    }
}

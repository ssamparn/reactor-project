package com.specification.reactive.service;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

public class BaseSubscriberImpl<T> extends BaseSubscriber<T> {

    @Override
    public void hookOnSubscribe(Subscription subscription) {
        System.out.println("Subscribed");
        request(1);
    }

    @Override
    public void hookOnNext(T value) {
        System.out.println(value);
        request(1);
    }

    @Override
    public void hookOnComplete() {
        System.out.println("Completed");
    }
}

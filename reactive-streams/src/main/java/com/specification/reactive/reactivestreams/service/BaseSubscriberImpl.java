package com.specification.reactive.reactivestreams.service;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

@Slf4j
public class BaseSubscriberImpl<T> extends BaseSubscriber<T> {

    @Override
    public void hookOnSubscribe(Subscription subscription) {
        log.info("Subscribed");
        request(1);
    }

    @Override
    public void hookOnNext(T value) {
        log.info((String) value);
        request(1);
    }

    @Override
    public void hookOnComplete() {
        log.info("Completed");
    }
}

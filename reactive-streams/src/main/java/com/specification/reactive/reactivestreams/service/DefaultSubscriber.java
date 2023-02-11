package com.specification.reactive.reactivestreams.service;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@Slf4j
public class DefaultSubscriber implements Subscriber<Object> {

    private String name = "";

    public DefaultSubscriber() {
    }

    public DefaultSubscriber(String name) {
        this.name = name + " - ";
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Object o) {
        log.info("{} Received : {}", name, o);
    }

    @Override
    public void onError(Throwable throwable) {
        log.info("{} Error : {}", name, throwable.getMessage());
    }

    @Override
    public void onComplete() {
        log.info("{} Completed ", name);
    }
}

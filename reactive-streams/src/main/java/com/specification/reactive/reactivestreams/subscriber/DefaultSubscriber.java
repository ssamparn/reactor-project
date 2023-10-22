package com.specification.reactive.reactivestreams.subscriber;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@Slf4j
public class DefaultSubscriber implements Subscriber<Object> {

    private String name = "";

    public DefaultSubscriber() {
    }

    public DefaultSubscriber(String name) {
        this.name = "Subscribed by " + name + " - ";
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(Long.MAX_VALUE); // make unbounded request
    }

    @Override
    public void onNext(Object o) {
        log.info("{} Received Object : {}", name, o);
    }

    @Override
    public void onError(Throwable throwable) {
        log.info("{} Error Thrown : {}", name, throwable.getMessage());
    }

    @Override
    public void onComplete() {
        log.info("{} Completed ", name);
    }
}

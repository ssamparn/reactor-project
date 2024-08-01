package com.specification.reactive.reactivestreams.custom.subscription;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import static com.specification.reactive.reactivestreams.util.RsUtil.faker;

@Slf4j
public class SubscriptionImpl implements Subscription {
    private static final int MAX_ITEMS = 10;
    private int count = 0;

    private Subscriber<? super String> subscriber;
    private boolean isCancelled;

    public SubscriptionImpl(Subscriber<? super String> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void request(long requested) {
        if (isCancelled) {
            return;
        }
        if (requested > MAX_ITEMS) {
            this.subscriber.onError(new RuntimeException("requested number of items is greater than maximum allowed"));
            this.isCancelled = true;
            return;
        }
        log.info("subscriber has requested {} items", requested);
        for (int i = 0; i < requested && count < MAX_ITEMS; i++) {
            count++;
            this.subscriber.onNext(faker().internet().emailAddress());
        }
        if (count == MAX_ITEMS) {
            log.info("no more data to produce");
            this.subscriber.onComplete();
            this.isCancelled = true;
        }
    }

    @Override
    public void cancel() {
        log.info("subscriber has cancelled");
        this.isCancelled = true;
    }
}

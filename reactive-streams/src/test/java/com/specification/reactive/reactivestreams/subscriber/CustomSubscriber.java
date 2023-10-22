package com.specification.reactive.reactivestreams.subscriber;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class CustomSubscriber {

    @Test
    public void custom_subscribe_test() {
        AtomicReference<Subscription> atomicReference = new AtomicReference<>();
        Flux.range(1, 20)
                .log()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        log.info("Received Subscription : {}", subscription);
                        atomicReference.set(subscription);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        log.info("onNext : {}", integer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        log.info("onError : {}", throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        log.info("onComplete");
                    }
                });

        RsUtil.sleepSeconds(2);
        atomicReference.get().request(3); // 1, 2, 3

        RsUtil.sleepSeconds(2);
        atomicReference.get().request(5); // 4, 5, 6, 7, 8

        RsUtil.sleepSeconds(1);

        log.info("Going to cancel");
        atomicReference.get().cancel();

        RsUtil.sleepSeconds(2);
        atomicReference.get().request(4); // No event is going to be emitted after the subscription is cancelled.
    }
}

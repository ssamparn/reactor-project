package com.specification.reactive.reactivestreams.subscriber;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

@Slf4j
public class BaseSubscriberImplTest {

    @Test
    public void base_subscriber_impl_test_approach_1() {
        BaseSubscriberImpl<Integer> baseSubscriber = new BaseSubscriberImpl<>();
        Flux<Integer> integerFlux = Flux.range(1, 5);

        integerFlux.subscribe(baseSubscriber);
    }

    @Test
    public void base_subscriber_impl_test_approach_2() {
        Flux.range(1, 5)
            .subscribe(new BaseSubscriber<>() {
                @Override
                protected void hookOnSubscribe(Subscription subscription) {
                    log.info("Subscribed");
                    request(1);
                }

                @Override
                protected void hookOnNext(Integer value) {
                    log.info("On Next: {}", value);
                    request(1);
                }

                @Override
                protected void hookOnComplete() {
                    log.info("Completed");
                }
            });
    }
}

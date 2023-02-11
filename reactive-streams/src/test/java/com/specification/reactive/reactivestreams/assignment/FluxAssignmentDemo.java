package com.specification.reactive.reactivestreams.assignment;

import com.specification.reactive.reactivestreams.service.StockPricePublisher;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class FluxAssignmentDemo {

    @Test
    public void flux_assignment_test() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        StockPricePublisher.getPrice()
                .subscribe(new Subscriber<Integer>() {

                    private Subscription subscription;

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        this.subscription = subscription;
                        subscription.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer price) {
                        log.info( "{} : Stock Price : {}", LocalDateTime.now(), price);
                        if (price < 90 || price > 110) {
                            this.subscription.cancel();
                            latch.countDown();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        latch.countDown();
                    }
                });
        latch.await();
    }

    @Test
    public void flux_generate_assignment_emit_all_country_names_till_canada_using_synchronous_sink() {
        Flux.generate(synchronousSink -> {
            String country = RsUtil.faker().country().name();
            synchronousSink.next(country);
            if (country.equalsIgnoreCase("canada")) {
                synchronousSink.complete();
            }
        })
        .subscribe(RsUtil.subscriber("Flux Country"));
    }

    // canada
    // max = 10
    // subscriber cancels - exit.
    @Test
    public void flux_generate_assignment_emit_country_names_till_canada_but_subscribe_max_10_using_synchronous_sink() {

        Flux.generate(
                () -> 1,
                (counter, synchronousSink) -> {
                    String country = RsUtil.faker().country().name();
                    synchronousSink.next(country);
                    if (country.equalsIgnoreCase("canada") || counter == 10) {
                        synchronousSink.complete();
                    }
                    return counter + 1;
                }
        )
        .subscribe(RsUtil.subscriber("Flux Country"));
    }
}

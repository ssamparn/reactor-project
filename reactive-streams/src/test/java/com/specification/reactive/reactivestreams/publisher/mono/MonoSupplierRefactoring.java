package com.specification.reactive.reactivestreams.publisher.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class MonoSupplierRefactoring {

    @Test
    public void mono_supplier_blocking_test() {

        // Blocking code. We are just invoking the publisher but not subscribing to it.
        getName();
        getName();
        getName();

        // subscribing to the publisher in a blocking manner
        getName().subscribe(RsUtil.onNext());

        // To get the name from the method, we have to block the main thread
        RsUtil.sleepSeconds(4);
    }

    @Test
    public void mono_supplier_async_test() {

        getName();
        getName();
        getName();

        // subscribing to the publisher in an asynchronous manner
        getName()
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(RsUtil.onNext());

        // To get the name from the method, we have to block the main thread
        RsUtil.sleepSeconds(4);
    }


    private static Mono<String> getName() {
        log.info("Entered getName method: ");
        return Mono.fromSupplier(() -> {
            log.info("Publishing Names...");
            RsUtil.sleepMilliSeconds(2000);
            return RsUtil.faker().name().fullName();
        }).map(String::toUpperCase);
    }
}

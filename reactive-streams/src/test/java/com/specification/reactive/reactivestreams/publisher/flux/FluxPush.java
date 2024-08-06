package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.service.NameProducer;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxPush {

    @Test
    public void flux_push_test() {
        NameProducer nameProducer = new NameProducer();

        Flux.push(nameProducer)
                .subscribe(RsUtil.subscriber());

        Runnable runnable = nameProducer::emitName;

        for (int i = 0; i < 10; i++) {
            new Thread(runnable).start(); // Creating Flux with Flux.push() is not thread safe.
        }

        RsUtil.sleepSeconds(2);
    }
}

package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.service.NameProducer;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxCreateRefactoring {

    @Test
    public void flux_creation_test() {
        NameProducer nameProducer = new NameProducer();

        Flux.create(nameProducer)
                .subscribe(RsUtil.subscriber());

        Runnable runnable = nameProducer::produce;

        for (int i = 0; i < 10; i++) {
            new Thread(runnable).start(); // Creating Flux with Flux.create() is thread safe.
        }

        RsUtil.sleepSeconds(2);
    }
}

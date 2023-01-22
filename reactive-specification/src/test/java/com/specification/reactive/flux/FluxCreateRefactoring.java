package com.specification.reactive.flux;

import com.specification.reactive.service.NameProducer;
import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxCreateRefactoring {

    @Test
    public void fluxCreateTest() {
        NameProducer nameProducer = new NameProducer();

        Flux.create(nameProducer)
                .subscribe(ReactiveSpecificationUtil.subscriber());

        Runnable runnable = nameProducer::produce;

        for (int i = 0; i < 10; i++) {
            new Thread(runnable).start();
        }

        ReactiveSpecificationUtil.sleepSeconds(2);
    }
}

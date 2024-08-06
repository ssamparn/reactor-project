package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.service.NameProducer;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

/* *
 * This is an alternative way how we can use Flux.create(fluxSink) in an application.
 * */
public class FluxCreateRefactoring {

    @Test
    public void flux_creation_test() {
        NameProducer nameGenerator = new NameProducer();

        // publisher implementation
        Flux.create(nameGenerator) // NameProducer implements a Consumer<FluxSink<String>>. That's why we are able to pass an instance into Flux.create().
                .subscribe(RsUtil.subscriber());

        // subscriber implementation
        // This is a sample subscriber (consumer) written somewhere else in our application. we will invoke emitName() to emit names.
        Runnable nameRunnable = () -> nameGenerator.emitName();

        for (int i = 0; i < 10; i++) {
            Thread.ofPlatform().start(nameRunnable); // Creating Flux with Flux.create() is thread safe.
        }

        RsUtil.sleepSeconds(2);
    }
}

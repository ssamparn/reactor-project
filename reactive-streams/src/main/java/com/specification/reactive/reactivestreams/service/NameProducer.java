package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// Since this class is going to be used in Flux.create(), which accepts a Consumer<FluxSink>,
// we are implementing a Consumer<FluxSink<>> and will create an instance of this class and pass it into Flux.create().

@Slf4j
public class NameProducer implements Consumer<FluxSink<String>> {

    private FluxSink<String> stringFluxSink;

    private List<String> cache = new ArrayList<>();

    @Override
    public void accept(FluxSink<String> stringFluxSink) {
        this.stringFluxSink = stringFluxSink;
    }

    public void emitName() {
        String name = RsUtil.faker().name().fullName();
        String thread = Thread.currentThread().getName();
        this.stringFluxSink.next(thread + " : " + name);
    }

    public Flux<String> generateNames() {
        return Flux.generate(stringSynchronousSink -> {
            System.out.println("Generated fresh");
            RsUtil.sleepSeconds(1);
            String name = RsUtil.faker().name().fullName();
            cache.add(name);
            stringSynchronousSink.next(name);
        })
        .cast(String.class)
        .startWith(getFromCache());
    }

    private Flux<String> getFromCache() {
        return Flux.fromIterable(cache);
        // Arraylist is used as cache here.
        // In real life scenarios, populate the cache with events with onNext() event call.
    }
}

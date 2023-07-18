package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.util.RsUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NameProducer implements Consumer<FluxSink<String>> {

    private FluxSink<String> fluxSink;

    private List<String> cache = new ArrayList<>();

    @Override
    public void accept(FluxSink<String> stringFluxSink) {
        this.fluxSink = stringFluxSink;
    }

    public void produce() {
        String name = RsUtil.faker().name().fullName();
        String thread = Thread.currentThread().getName();
        this.fluxSink.next(thread + " : " + name);
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

package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/* *
 * Since this class is going to be used in Flux.create() demo, which accepts a Consumer<FluxSink>,
 * we are implementing a Consumer<FluxSink<>> and will create an instance of this class and pass it into Flux.create().
 * */

@Slf4j
public class NameProducer implements Consumer<FluxSink<String>> {

    private FluxSink<String> stringFluxSink;

    private List<String> cache = new ArrayList<>();

    @Override
    public void accept(FluxSink<String> stringFluxSink) {
        log.info("Instantiating a Flux Sink");
        this.stringFluxSink = stringFluxSink;
    }

    public void emitName() {
        String name = RsUtil.faker().name().fullName();
        String thread = Thread.currentThread().getName();
        this.stringFluxSink.next(thread + " : " + name);
    }

    /**
     * This method is only to be used in Flux.startWith() demo
     * */
    public Flux<String> generateNames() {
        return Flux.<String>generate(sink -> {
            log.info("Generated fresh names");
            RsUtil.sleepMilliSeconds(500);
            String name = RsUtil.faker().name().fullName();
            cache.add(name);
            sink.next(name);
        })
        .startWith(getFromCache()); // First the subscriber will get events from the cache. If the condition does not satisfy, then new events (fresh names) will be emitted from the publisher.
    }

    private Flux<String> getFromCache() {
        log.info("Getting names from cache");
        return Flux.fromIterable(cache);
        // Arraylist is used as cache here. But in real life scenarios, populate the cache with events by mutating the cache object with doOnNext() call.
    }
}

package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.service.NameProducer;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/* *
 * Flux Sink is thread safe
 * */

@Slf4j
public class FluxCreateThreadSafety {

    @Test
    public void flux_create_with_flux_sink_thread_safety_test() {
//        nonThreadSafeBehaviorDemo();
        threadSafeBehaviorDemo();
    }

    // demo a thread safety behavior flux sink: fluxSink is thread safe.
    private static void threadSafeBehaviorDemo() {
        List<String> names = new ArrayList<>();

        // publisher implementation
        NameProducer nameProducer = new NameProducer();
        Flux.create(nameProducer)
                .subscribe(names::add); // as soon as we received a name from name producer via, we are adding it to list


        // subscriber implementation
        // This is a sample subscriber written in some other part of your application.
        Runnable runnable = () -> {
            for (int i = 1; i <= 1000; i++) {
                nameProducer.emitName();
            }
        };

        for (int i = 0; i < 10; i++) {
            Thread.ofPlatform().start(runnable); // each and every thread is supposed to add 1000 names.
        }

        RsUtil.sleepSeconds(3);
        log.info("List size: {}", names.size()); // size of list should be 10000.
    }

    // demo a non thread safety behavior: array list is not thread safe.
    private static void nonThreadSafeBehaviorDemo() {
        List<Integer> integers = new ArrayList<>();

        Runnable runnable = () -> {
            for (int i = 1; i <= 1000; i++) {
                integers.add(i);
            }
        };

        for (int i = 0; i < 10; i++) {
            Thread.ofPlatform().start(runnable); // each and every thread is supposed to add 1000 items.
        }

        log.info("List size: {}", integers.size()); // size of list should be 10000, but it is lesser than that.
    }
}

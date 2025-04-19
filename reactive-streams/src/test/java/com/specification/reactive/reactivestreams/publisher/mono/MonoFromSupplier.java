package com.specification.reactive.reactivestreams.publisher.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class MonoFromSupplier {

    /* *
     * Difference between Mono.just() and Mono.fromSupplier():
     * Use Mono.just() only when you know publisher already have the data present in application memory.
     * Use Mono.fromSupplier() when you want to subscribe to the publisher lazily and delay the execution of publisher emitting events.
     * Mono.fromSupplier() creates a Mono, producing its value using the provided Supplier.
     * If the Supplier resolves to null, the resulting Mono completes empty.
     *
     * In short Mono.just() returns a hot publisher whereas Mono.fromSupplier() returns a cold publisher.
     * Mono.fromSupplier is lot like Mono.defer() which is also a cold publisher.
     * */
    @Test
    public void mono_from_supplier_test() {
        Mono.fromSupplier(() -> getName())
                .subscribe(RsUtil.onNext());
    }

    private static String getName() {
        log.info("Publishing Names: "); // Imagine this is a time-consuming operation
        RsUtil.sleepMilliSeconds(500);
        return RsUtil.faker().name().fullName();
    }

    @Test
    public void mono_from_supplier_sum_test() {
        List<Integer> integers = List.of(1, 2, 3, 4, 5);

        Mono.fromSupplier(() -> sum(integers))
                .subscribe(RsUtil.onNext());
    }

    private int sum(List<Integer> list) {
        log.info("finding the sum of {}", list);
        return list.stream().mapToInt(Integer::intValue).sum();
    }

    @Test
    public void mono_just_vs_supplier_test() {
        Mono<String> nameJustMono = Mono.just(getName());

        // same name will be emitted thrice: hot publisher
        nameJustMono.subscribe(RsUtil.onNext());
        nameJustMono.subscribe(RsUtil.onNext());
        nameJustMono.subscribe(RsUtil.onNext());

        // 3 different names will be emitted: cold publisher
        Mono<String> nameSupplierMono = Mono.fromSupplier(() -> getName());
        nameSupplierMono.subscribe(RsUtil.onNext());
        nameSupplierMono.subscribe(RsUtil.onNext());
        nameSupplierMono.subscribe(RsUtil.onNext());
    }
}

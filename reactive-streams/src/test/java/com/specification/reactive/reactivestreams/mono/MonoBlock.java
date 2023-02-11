package com.specification.reactive.reactivestreams.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class MonoBlock {

    @Test
    public void mono_block_test() {

        getName();
        getName()
                .subscribeOn(Schedulers.boundedElastic())
                .block();
        getName();

        RsUtil.sleepSeconds(4);
    }

    private static Mono<String> getName() {
        log.info("Entered getName method: ");
        return Mono.fromSupplier(() -> {
            log.info("Generating Name...");
            RsUtil.sleepSeconds(2);
            return RsUtil.faker().name().fullName();
        }).map(String::toUpperCase);
    }
}

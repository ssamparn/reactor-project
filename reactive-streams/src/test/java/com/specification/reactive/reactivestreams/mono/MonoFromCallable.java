package com.specification.reactive.reactivestreams.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
public class MonoFromCallable {

    @Test
    public void mono_from_Callable_test() {
        Callable<String> stringCallable = () -> getName();

        Mono<String> stringMono = Mono.fromCallable(stringCallable);

        stringMono.subscribe(RsUtil.onNext());
    }

    private static String getName() {
        log.info("Generating Name: ");
        return RsUtil.faker().name().fullName();
    }
}

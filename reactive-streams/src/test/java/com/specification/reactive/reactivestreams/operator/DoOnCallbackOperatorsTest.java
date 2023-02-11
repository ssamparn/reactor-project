package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class DoOnCallbackOperatorsTest {

    @Test
    public void do_callback_operator_complete_test() {
        Flux.create(fluxSink -> {
            log.info("Inside flux creation");
            for (int i = 1; i <= 5; i++) {
                fluxSink.next(i);
            }
            fluxSink.complete();
            log.info("--flux completed");
        })
        .doOnComplete(() -> log.info("doOnComplete"))
        .doFirst(() -> log.info("doFirst: 1"))
        .doOnNext(obj -> log.info("doOnNext : {}", obj))
        .doOnSubscribe(obj -> log.info("doOnSubscribe 1: {}", obj))
        .doOnRequest(l ->  log.info("doOnRequest : {}", l))
        .doOnError(err -> log.info("doOnRequest : {}", err.getMessage()))
        .doOnTerminate(() -> log.info("doOnTerminate"))
        .doFirst(() -> log.info("doFirst: 2"))
        .doOnCancel(() -> log.info("doOnCancel"))
        .doFinally(finalSignal -> log.info("doFinally : {}", finalSignal))
        .doFirst(() -> log.info("doFirst: 3"))
        .doOnDiscard(Object.class, obj -> log.info("doOnDiscard : {}", obj))
        .doOnSubscribe(obj -> log.info("doOnSubscribe 2: {}", obj))
        .subscribe(RsUtil.subscriber());
    }


// The request flows from subscriber to publisher.
// The subscription object passed from publisher to subscriber.
// doOnTerminate and doFinally always gets executed.

    @Test
    public void do_callback_operator_error_test() {
        Flux.create(fluxSink -> {
            log.info("Inside flux creation");
            for (int i = 1; i <= 5; i++) {
                fluxSink.next(i);
            }
            fluxSink.error(new RuntimeException("oops"));
            log.info("--flux completed");
        })
        .doOnComplete(() -> log.info("doOnComplete"))
        .doFirst(() -> log.info("doFirst: 1"))
        .doOnNext(obj -> log.info("doOnNext : {}", obj))
        .doOnSubscribe(obj -> log.info("doOnSubscribe 1: {}", obj))
        .doOnRequest(l ->  log.info("doOnRequest : {}", l))
        .doOnError(err -> log.info("doOnRequest : {}", err.getMessage()))
        .doOnTerminate(() -> log.info("doOnTerminate"))
        .doFirst(() -> log.info("doFirst: 2"))
        .doOnCancel(() -> log.info("doOnCancel"))
        .doFinally(finalSignal -> log.info("doFinally : {}", finalSignal))
        .doFirst(() -> log.info("doFirst: 3"))
        .doOnDiscard(Object.class, obj -> log.info("doOnDiscard : {}", obj))
        .doOnSubscribe(obj -> log.info("doOnSubscribe 2: {}", obj))
        .subscribe(RsUtil.subscriber());
    }

}
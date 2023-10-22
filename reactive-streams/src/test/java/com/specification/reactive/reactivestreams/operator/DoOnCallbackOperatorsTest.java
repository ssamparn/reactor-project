package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class DoOnCallbackOperatorsTest {

    /* *
     * Callback precedence:
     * ---------------------
     * ---------------------
     * The request flows from subscriber to publisher.
     * The subscription object passed from publisher to subscriber.
     * doFirst() always gets executed first (bottom to top). ALWAYS. Even before doOnSubscribe().
     * After doFirst(), doOnSubscribe() (top to bottom) gets executed.
     * After subscription, request is made. So after doOnSubscribe(), doOnRequest() gets executed. Then doOnNext() or doOnError() (In case of any error)
     * In case there is no error, doOnComplete() gets executed upon completion signal.
     * doOnTerminate() gets executed before doFinally(). doOnTerminate() gets executed when the subscription gets terminated i.e: before completion or error signal gets received.
     * But doOnTerminate() does not get executed if we cancel the subscription e.g: with a take() operator.
     * doFinally() always gets executed at the end.
     * */

    @Test
    public void do_callback_operator_complete_test() {
        Flux.create(fluxSink -> {
            log.info("Inside flux creation");
            for (int i = 1; i <= 5; i++) {
                fluxSink.next(i);
            }
            fluxSink.complete();
            log.info("Flux completed");
        })
        .doFinally(finalSignal -> log.info("doFinally : {}", finalSignal))
        .doOnComplete(() -> log.info("doOnComplete"))
        .doFirst(() -> log.info("doFirst: 1"))
        .doOnNext(obj -> log.info("doOnNext : {}", obj))
        .doOnSubscribe(obj -> log.info("doOnSubscribe 1: {}", obj))
        .doOnRequest(l ->  log.info("doOnRequest : {}", l))
        .doOnError(err -> log.info("doOnRequest : {}", err.getMessage()))
        .doOnTerminate(() -> log.info("doOnTerminate"))
        .doFirst(() -> log.info("doFirst: 2"))
        .doOnCancel(() -> log.info("doOnCancel"))
        .doFirst(() -> log.info("doFirst: 3")) // 1
        .doOnDiscard(Object.class, obj -> log.info("doOnDiscard : {}", obj))
        .doOnSubscribe(obj -> log.info("doOnSubscribe 2: {}", obj))
        .subscribe(RsUtil.subscriber());
    }



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
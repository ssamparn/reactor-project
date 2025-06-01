package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class DoOnCallbackOperatorsTest {

    /* *
     * doOn Operator Callback precedence:
     * ---------------------
     * ---------------------
     * The request flows from subscriber(downstream) to publisher(upstream).
     * The subscription object passed from publisher(upstream) to subscriber(downstream).
     * doFirst() always gets executed first (bottom to top). ALWAYS. Even before doOnSubscribe().
     * After doFirst(), doOnSubscribe() (top to bottom) gets executed.
     * After subscription, request is made. So after doOnSubscribe(), doOnRequest() gets executed (bottom to top). Then doOnNext() (top to bottom) or doOnError() (In case of any error)
     * In case there is no error, doOnComplete() (top to bottom) gets executed upon completion signal.
     * doOnTerminate() gets executed before doFinally() and after doOnComplete(). doOnTerminate() gets executed when the subscription gets terminated i.e: before completion or error signal gets received.
     * But doOnTerminate() does not get executed if we cancel the subscription e.g: with a take() operator.
     * doFinally() always gets executed at the end.
     *
     * V. Imp Note: Except doOnNext() no other doOn methods can mutate / change the value. So if you have to set a value in java object, use setter in doOnNext().
     * */

    @Test
    public void do_callback_operator_complete_test() {
        Flux.<Integer>create(fluxSink -> {
            log.info("Inside flux creation");
            for (int i = 1; i <= 5; i++) {
                fluxSink.next(i);
            }
            fluxSink.complete();
            log.info("publisher completed");
        })
        .doOnComplete(() -> log.info("doOnComplete-1"))
        .doFirst(() -> log.info("doFirst-1"))
        .doOnNext(obj -> log.info("doOnNext-1 : {}", obj))
        .doOnSubscribe(obj -> log.info("doOnSubscribe-1 : {}", obj))
        .doOnRequest(l ->  log.info("doOnRequest-1 : {}", l))
        .doOnError(err -> log.info("doOnError-1 : {}", err.getMessage()))
        .doOnTerminate(() -> log.info("doOnTerminate-1")) // invoked after doOnComplete() or doOnError() irrespective of the outcome. But will not run for cancel.
        .doOnCancel(() -> log.info("doOnCancel-1"))
        .doOnDiscard(Object.class, obj -> log.info("doOnDiscard-1 : {}", obj))
        .doFinally(finalSignal -> log.info("doFinally-1 : {}", finalSignal)) // finally irrespective of the reason. It's like a finally block of try-catch-finally.
        .take(2)
        .doOnComplete(() -> log.info("doOnComplete-2"))
        .doFirst(() -> log.info("doFirst-2"))
        .doOnNext(obj -> log.info("doOnNext-2 : {}", obj))
        .doOnSubscribe(obj -> log.info("doOnSubscribe-2 : {}", obj))
        .doOnRequest(l ->  log.info("doOnRequest-2 : {}", l))
        .doOnError(err -> log.info("doOnError-2 : {}", err.getMessage()))
        .doOnTerminate(() -> log.info("doOnTerminate-2"))
        .doOnCancel(() -> log.info("doOnCancel-2"))
        .doOnDiscard(Object.class, obj -> log.info("doOnDiscard-2 : {}", obj))
        .doFinally(finalSignal -> log.info("doFinally-2 : {}", finalSignal)) // finally irrespective of the reason. It's like a finally block of try-catch-finally.
        .take(4)
        .subscribe(RsUtil.subscriber());
    }

    @Test
    public void do_callback_operator_error_test() {
        Flux.<Integer>create(fluxSink -> {
            log.info("Inside flux creation");
            for (int i = 1; i <= 5; i++) {
                fluxSink.next(i);
            }
            fluxSink.error(new RuntimeException("oops"));
            log.info("publisher completed");
        })
        .doOnComplete(() -> log.info("doOnComplete-1"))
        .doFirst(() -> log.info("doFirst-1"))
        .doOnNext(obj -> log.info("doOnNext-1 : {}", obj))
        .doOnSubscribe(obj -> log.info("doOnSubscribe-1: {}", obj))
        .doOnRequest(l ->  log.info("doOnRequest-1: {}", l))
        .doOnError(err -> log.info("doOnError-1: {}", err.getMessage()))
        .doOnTerminate(() -> log.info("doOnTerminate-1"))
        .doOnCancel(() -> log.info("doOnCancel-1"))
        .doOnDiscard(Object.class, obj -> log.info("doOnDiscard-1 : {}", obj))
        .doFinally(finalSignal -> log.info("doFinally-1: {}", finalSignal)) // finally irrespective of the reason. It's like a finally block of try-catch-finally.
        //.take(2)
        .doOnComplete(() -> log.info("doOnComplete-2"))
        .doFirst(() -> log.info("doFirst-2"))
        .doOnNext(obj -> log.info("doOnNext-2 : {}", obj))
        .doOnSubscribe(obj -> log.info("doOnSubscribe-2: {}", obj))
        .doOnRequest(l ->  log.info("doOnRequest-2: {}", l))
        .doOnError(err -> log.info("doOnError-2: {}", err.getMessage()))
        .doOnTerminate(() -> log.info("doOnTerminate-2"))
        .doOnCancel(() -> log.info("doOnCancel-2"))
        .doOnDiscard(Object.class, obj -> log.info("doOnDiscard-2 : {}", obj))
        .doFinally(finalSignal -> log.info("doFinally-2: {}", finalSignal)) // finally irrespective of the reason. It's like a finally block of try-catch-finally.
        //.take(4)
        .subscribe(RsUtil.subscriber());
    }

}
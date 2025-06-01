package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.exception.CustomException;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Handle error in a reactive pipeline
 * */
@Slf4j
public class OnErrorOperatorTest {

    /**
     * onError() operator: When working with the Reactive Streams, if an error occurs while the Publisher is emitting events,
     * the entire flow will be interrupted, and the error signal will be sent to the subscriber.
     * And no other signals will be sent after the error signal.
     * */

    @Test
    public void on_error_simple_demo_test() {
        Flux.just(2, 7, 10)
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .concatWith(Mono.just(12)) // Here 12 will never be emitted as it is subscribed after the error occurred.
                .subscribe(RsUtil.subscriber());
    }

    /* *
     * In the Project Reactor, we can handle exceptions using some of the following operators:
     *
     *      onErrorReturn(): It is helpful in scenarios like, a publisher while emitting items, in case of any error just emit a default hardcoded value or any value based on simple computation.
     *                       So this way the subscriber will not receive an exception. Point to note is publisher stops emitting items after onError.
     *                       Also, the placement of onErrorReturn() is very important. It should be placed right after the publisher which is most likely throw an exception.
     *                       Ideally just place it right above .subscribe() method call.
     *
     *      onErrorResume(): It is helpful in scenarios like, a publisher (e.g a rest service) while emitting items, and in case of any error, provide an alternative fallback publisher (e.g: another rest service as a fallback service)
     *                       to the subscriber to subscribe from. e.g: Try to get the data from a rest api, but if the rest api is down or is too slow try to get it from cache.
     *
     *      onErrorComplete(): It is helpful in scenarios like, a publisher (e.g a rest service) while emitting items, and in case of any error just emit a completion signal.
     *
     *      onErrorContinue(): It is helpful in scenarios like, a publisher (e.g a rest service) while emitting items, and in case of any error the subscriber catches the exception,
     *                         the element that caused the exception will be dropped, and the Publisher will continue emitting the remaining elements.
     *                         Use case: This can be really helpful while pushing erroneous events to a kafka dlq.
     *
     *      onErrorMap(): It is helpful to transform one type of error / exception into another type of exception. This is useful when you want to
     *                      1. Wrap low-level exceptions into custom exception. e.g: translate technical exceptions into domain-specific ones.
     *                      2. Add context to errors. e.g: to log or enrich error messages
     *                      3. Standardize error types across your reactive pipeline / application
     *
     *      doOnError(): In Project Reactor, the doOnError() operator is used to perform side effects when an error occurs in a reactive stream.
     *                   It’s typically used for logging, metrics, or cleanup, but it does not modify the error or stop it from propagating.
     *
     *  Note 1: The exception handling operators in Project Reactor are defined in both Mono and Flux classes.
     *  Note 2: We can combine multiple onError operators in both Mono and Flux classes.
     * */

    /* *
    * onErrorReturn(): We may want to substitute a default value in case of an Exception.
    * Say we’re processing a stream of integers, and we return 100 divided by whatever value we have in the stream.
    * In case we encounter a zero, we want to return the maximum possible value. In such cases, we have a function like orErrorReturn( ).
    *  */

    @Test
    public void on_error_return_test() {
        Flux.just(1, 2, 3, 4, 0, 21)
                .map(i -> 100 / i)
                .onErrorReturn(Integer.MAX_VALUE)
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void on_error_return_placement_test() {
        Flux.just(1, 2, 3, 4, 0, 21)
                .onErrorReturn(Integer.MAX_VALUE) // will not work here. exception will still be thrown as placement of onErrorReturn() is important.
                .map(i -> 100 / i)
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void on_error_return_another_test() {
        Flux.range(1, 10)
                .log()
                .map(i -> 20 / (5 - i))
                .onErrorReturn(20) // the pipeline stops after the error and a cancel() event gets emitted.
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void on_error_handling_demo_test() {
        Flux.just(2, 7, 10)
                .log()
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .concatWith(Mono.just(12)) // Here 12 will never be emitted as it was supposed to be subscribed after the error occurred.
                .onErrorReturn(100) // 100 will be emitted instead
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void on_error_handling_matching_exception_test() {
        Flux.just(2, 7, 10)
                .log()
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .concatWith(Mono.just(12))
                .onErrorReturn(RuntimeException.class ,100) // Here publisher emits 100 only if the exception is of type RuntimeException. So 100 will be emitted.
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void on_error_handling_not_matching_exception_test() {
        Flux.just(2, 7, 10)
                .log()
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .concatWith(Mono.just(12))
                .onErrorReturn(IllegalArgumentException.class ,100) // Here publisher emits 100 only if the exception is of type RuntimeException. So 100 will not be emitted.
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void on_error_handling_matching_exception_another_test() {
        Flux.range(1, 10)
                .map(i -> i == 5 ? 5 / 0 : i)
                .onErrorReturn(ArithmeticException.class, -1) // -1 will be emitted as the exception thrown is of type ArithmeticException
                .onErrorReturn(IllegalArgumentException.class ,-2) // -2 will not be emitted as the exception thrown is not of type IllegalArgumentException
                .subscribe(RsUtil.subscriber());
    }


    /* *
     * onErrorResume(): In certain scenarios, you may want to provide a fallback publisher in case you encounter an error while subscribing to the primary publisher.
     * Let’s just say you wanted to hit an unreliable service which may produce some error.
     * You may use a onErrorResume( ) which will return another stream on elements from the point you encountered that error.
    */
    @Test
    public void error_handling_with_on_error_resume_test() {
        Flux.just(2, 7, 10)
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .onErrorResume(ex -> Mono.just(12))
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void error_handling_with_on_error_resume_another_test() {
        Flux.just(1, 2, 3, 4, 0, 21)
                .map(i -> 100 / i)
                .onErrorResume(ex -> Flux.range(1, 3))
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void on_error_resume_test() {
        Flux.range(1, 10)
                .map(i -> i == 5 ? 5 / 0 : i)
                .onErrorResume(ArithmeticException.class, e -> fallBack()) // the pipeline stops after the error and a cancel() call is made.
                // Here we are not using the exception object. We are simply ignoring it and return a value from fallback().
                .subscribe(RsUtil.subscriber());
    }

    // Note: Both the type of tests (with map tests and concatenating Flux.error onto the publisher produces different behavior

    /* *
     * onErrorComplete(): publisher (e.g a rest service) emits completion signal in case of any error. So the actual error or exception is not known (to the subscriber) and is hidden (from the subscriber).
     * */

    @Test
    public void on_error_complete_test() {
        Flux.just(1, 2, 3, 4, 0, 21)
                .map(i -> 100 / i)
                .onErrorComplete()
                .subscribe(RsUtil.subscriber());
    }

    /* *
     *  onErrorContinue(): In onErrorReturn(), onErrorResume() and onErrorComplete() the reactive pipeline stops after any error and any cancel signal gets emitted. But what if you want the publisher to continue the event emission.
     *  That's when we should use onErrorContinue(). The onErrorContinue() catches the exception, the element that caused the exception will be dropped, and the Publisher will continue emitting the remaining elements.
     */
    @Test
    public void on_error_continue_test() {
        Flux.range(1, 10)
                .map(i -> i == 5 ? 5 / 0 : i)
                .onErrorContinue((err, obj) -> {
                    log.error("Exception caught: {}", err.getMessage());
                    log.error("The element that caused the exception is: {}", obj);
                }) // the pipeline gets continued even after the error. for i = 5, the produced value infinite will be dropped from the sequence and the pipeline will get continued as it is.
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void on_error_continue_another_test() {
        Flux.just(2, 7, 10, 8, 12, 22, 24)
                .map(element -> {
                    if (element == 8) {
                        throw new RuntimeException("Exception Occured!");
                    }
                    return element;
                })
                .onErrorContinue((ex, erroneousElement) -> {
                    log.error("Exception caught: {}", ex.getMessage());
                    log.error("The element that caused the exception is: {}", erroneousElement);
                })
                .subscribe(RsUtil.subscriber());
    }

    /* *
     * onErrorMap(): With the onErrorMap(), the code can’t recover from the exception.
     * This method only catches the exception and transforms it from one type to another.
     * Sometimes we need to provide custom exceptions in order to be more clear about things. So instead of a DivideByZero we may want CustomException.
     * Such is the use case of onErrorMap( ).
     */

    @Test
    public void on_error_map_test() {
        Flux.just(2, 7, 10, 8, 12, 22, 24)
                .map(element -> {
                    if (element == 8) {
                        throw new RuntimeException("Exception occured!");
                    }
                    return element;
                })
                .onErrorMap(ex -> {
                    log.error("Exception caught: {}", ex.getMessage());
                    return new CustomException(ex);
                })
                .subscribe(RsUtil.subscriber());
    }

    private static Mono<Integer> fallBack() {
        return Mono.fromSupplier(() -> RsUtil.faker().random().nextInt(100, 200));
    }

    /**
     * doOnError(): doOnError() is an equivalent of onErrorMap().
     * In some scenarios, we may want the error to propagate and just want to log stuff up so that we know where it failed. This is where doOnError() can help us.
     * It’ll catch, perform side effect operation and rethrow the exception.
     * */
    @Test
    public void do_on_error_callback_operator_test() {
        Flux.just(2, 7, 10, 8, 12, 22, 24)
                .map(element -> {
                    if (element == 8) {
                        throw new RuntimeException("Exception occured!");
                    }
                    return element;
                })
                .doOnError(ex -> log.error("Exception caught: {}", ex.getMessage()))
                .subscribe(RsUtil.subscriber());
    }
}

package com.specification.reactive.reactivestreams.repeat.n.retry;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/* *
 * Retry():
 * In reactive programming, we know that we have a publisher and a subscriber.
 * Publisher emits events to the subscriber. Subscriber cannot expect any events after the onComplete or onError signals.
 * But what retry() operator does, if the onError signal is received, subscriber will automatically resubscribe to the publisher,
 * and request the publisher to emit the events once again.
 * So it will keep on retrying the operation based on provided configuration.
 *
 * Use cases of Retry():
 *
 * Now let's imagine a product service.
 * We are sending one request to get the product information, and you are running multiple instances of product service in the real life
 * behind one load balancer. Now let's imagine that one service is under load. So when we send the request somehow the service returned
 * 500 internal server error. If you have been working in a microservices architecture, you will know we will be getting this kind
 * of error once in a while. So by doing this or by adding retry operator, when it sees this kind of error, it will be retrying the request
 * one more time automatically. Now your load balancer might be routing the request to a different server instance, which might be in a good condition. You might be getting a response.
 * So this is where the retry operator will be very helpful. Just by adding this, you are making your application more resilient.
 * Sometimes you might not want to retry immediately. Instead, you might want to retry after a second or so. You might want to add some delay. For that also reactor provides some options.
 * */

@Slf4j
public class RetryTest {

    /* *
     * retry(): will retry indefinitely
     * retry(numRetries): will retry based on the number of retries provided.
     * */

    @Test
    public void retry_behavior_test() {
        Mono<String> countryMono = getCountry();

        countryMono
//                 .retry() // will retry indefinitely
                .retry(6)
                .subscribe(RsUtil.subscriber());
    }

    @Test
    public void retry_when_behavior_test() {
        Mono<String> countryMono = getCountry();

        countryMono
                .retryWhen(Retry.fixedDelay(7, Duration.ofMillis(500)))
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(4);
    }

    @Test
    public void retry_when_behavior_with_callback_test() {
        Mono<String> countryMono = getCountry();

        countryMono
                .retryWhen(
                        Retry.fixedDelay(7, Duration.ofMillis(500))
                                .doBeforeRetry(rs -> log.info("retrying {}", rs.failure().getMessage()))
                                .doAfterRetry(rs -> log.info("total number of retries: {}", rs.totalRetries())))
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(4);
    }

    /* *
     * By filtering exceptions to retry for, we can decide for which exceptions to retry for & for which exceptions to not retry for.
     * e.g: We can retry for 500 INTERNAL_SERVER_ERROR & not retry for 400 BAD_REQUEST.
     * */
    @Test
    public void retry_when_behavior_with_callback_retry_with_specific_exception_test() {
        Mono<String> countryMono = getCountry();

        countryMono
                .retryWhen(
                        Retry.fixedDelay(7, Duration.ofMillis(500))
                                .filter(ex -> RuntimeException.class.equals(ex.getClass())) // retry for a specific exception. will retry for RuntimeException.class
                                // .filter(ex -> IllegalArgumentException.class.equals(ex.getClass()))
                                .doBeforeRetry(rs -> log.info("retrying {}", rs.failure().getMessage()))
                                .doAfterRetry(rs -> log.info("total number of retries: {}", rs.totalRetries())))
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(4);
    }

    /* *
     * If the max number of retries exhausted, we will get a RetryExhausted exception along with the original runtime exception of publisher,
     * but what is the point of propagating this RetryExhausted exception to the subscriber. As subscriber should only care about the publisher error signal, which is the original RuntimeException.
     * For that there is an option onRetryExhaustedThrow some exception. This way we will absorb the retry exhausted exception & will only throw the original exception.
     * */
    @Test
    public void retry_when_behavior_with_callback_retry_exhausted_exception_test() {
        Mono<String> countryMono = getCountry();

        countryMono
                .retryWhen(
                        Retry.fixedDelay(4, Duration.ofMillis(500))
                                .filter(ex -> RuntimeException.class.equals(ex.getClass())) // retry for a specific exception. will retry for RuntimeException.class.
                                .onRetryExhaustedThrow((retrySpecBackOff, retrySignal) -> retrySignal.failure())) // subscriber will not see any RetryExhausted exception & will only see the original runtime exception.
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(4);
    }

    private static Mono<String> getCountry() {
        AtomicInteger atomicInteger = new AtomicInteger(0);

        return Mono.fromSupplier(() -> {
            if (atomicInteger.incrementAndGet() < 7) {
                throw new RuntimeException("oops");
            }
            return RsUtil.faker().country().name();
        })
        .doOnError(error -> log.info("Error: {}", error.getMessage()))
        .doOnSubscribe(s -> log.info("subscribing"));
    }
}

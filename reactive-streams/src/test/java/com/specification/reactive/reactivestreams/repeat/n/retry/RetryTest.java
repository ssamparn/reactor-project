package com.specification.reactive.reactivestreams.repeat.n.retry;

import lombok.extern.slf4j.Slf4j;

/* *
 * Retry():
 * In reactive programming, we know that we have a publisher and a subscriber.
 * Publisher emits events to the subscriber. Subscriber cannot expect any events after the onComplete or onError signals.
 * But what retry() operator does, if the onError signal is received, subscriber will automatically resubscribe to the publisher,
 * and request the publisher to emit the events once again.
 * So it will keep on retrying the operation based on provided configuration.
 * */
@Slf4j
public class RetryTest {


}

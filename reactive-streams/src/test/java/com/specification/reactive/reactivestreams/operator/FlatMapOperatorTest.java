package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.model.User;
import com.specification.reactive.reactivestreams.service.PurchaseOrderService;
import com.specification.reactive.reactivestreams.service.UserService;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;

public class FlatMapOperatorTest {

    /* *
     * Note: If the return type is a publisher (flux or mono), then 99.99% of the time you have to use flatMap to flatten it.
     * flatMap(): This operator takes in a function that itself returns an asynchronous publisher to subscribe upon.
     * For every outer emission, the inner publisher is eagerly subscribed to.
     * In comparison to the map operator, the inner emissions are flattened into the resulting sequence.
     * However, the initial ordering is not guaranteed to be preserved as emitted items are propagated as they come.
     * Emissions from different inner publishers may interleave.
     *
     * The operator is useful, when order does not matter and interleaving is no problem, which actually applies to many use cases.
     * For example, when we perform an HTTP request for every emitted item of the outer publisher and these only return a single value, we’re not in danger of interleaved values.
     * When strict ordering is necessary and values from different inner publishers must not interleave, then have a look at the concatMap operator.
     * */

    @Test
    public void flatMap_operator_test() {
        UserService.getUsers()
                .map(User::getUserId)
                .flatMap(PurchaseOrderService::getOrders)
                .subscribe(RsUtil.subscriber());
        RsUtil.sleepSeconds(2);
    }

    /* *
     * Difference between map() and flatMap():
     * The map operator applies a one-to-one transformation to stream elements, while flatMap does one-to-many.
     * map(): Transform the items emitted by publisher by applying a synchronous function to each item.
     * flatMap(): Transform the elements emitted by publisher asynchronously into Publishers.c
     * It’s easy to see map is a synchronous operator – it’s simply a method that converts one value to another. This method executes in the same thread as the caller.
     * The other statement – flatMap is asynchronous – is not that clear. In fact, the transformation of elements into Publishers can be either synchronous or asynchronous.
     * In our sample code, that operation is synchronous since we emit elements with the Flux#just method. However, when dealing with a source that introduces high latency, such as a remote server, asynchronous processing is a better option.
     * The important point is that the pipeline doesn’t care which threads the elements come from – it just pays attention to the publishers themselves.
     */
}

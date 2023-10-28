package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.model.User;
import com.specification.reactive.reactivestreams.service.PurchaseOrderService;
import com.specification.reactive.reactivestreams.service.UserService;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;

public class FlatMapOperatorTest {

    /**
     * Note: If the return type is a publisher (flux or mono), then 99.99% of the time you have to use flatMap to flatten it.
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

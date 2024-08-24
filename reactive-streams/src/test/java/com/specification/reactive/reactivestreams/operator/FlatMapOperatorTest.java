package com.specification.reactive.reactivestreams.operator;

import com.specification.reactive.reactivestreams.model.User;
import com.specification.reactive.reactivestreams.service.OrderService;
import com.specification.reactive.reactivestreams.service.PaymentService;
import com.specification.reactive.reactivestreams.service.UserService;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;

/* *
 * Note: If the return type of method is a reactive publisher (flux or mono), then 99.99% of the time you have to use flatMap to flatten it & subscribes to it.
 * flatMap(): This operator takes in a function that itself returns an asynchronous publisher to subscribe upon.
 * For every outer emission of events, all the inner publishers are eagerly subscribed to at the same time.
 * That is subscriber will not wait for one inner publisher to complete before subscribing to the next inner publisher. So at any point of time all the inner publishers (Flux<T>) may be subscribed and all of them are emitting events.
 * So this behavior is very similar to the merge() or mergeWith() behavior where all publishers (Flux<T>) are subscribed at the same time and subscription happens eagerly.
 *
 * Now the question is using flatMap() how many concurrent requests (subscribing to inner publishers can we do at the same time)?
 * Answer: 256. This is the reactor queue size. flatMap() also accepts an additional parameter called concurrency, if you want to control the inner publisher concurrent subscription count.
 *
 * In comparison to the map operator, the inner emissions are flattened into the resulting sequence.
 * However, the initial ordering is not guaranteed to be preserved as emitted events are propagated as they come.
 * Emissions from different inner publishers may interleave and ordering of resulting events may not remain intact.
 *
 * The operator is useful, when order does not matter and interleaving is no problem, which actually applies to many use cases.
 * For example, when we perform an HTTP request for every emitted item of the outer publisher and these only return a single value, we’re not in danger of interleaved values.
 * When strict ordering is necessary and values from different inner publishers must not interleave, then have a look at the concatMap() operator.
 * */

public class FlatMapOperatorTest {

    /* *
     * flatMap(): In some cases we have to make sequential calls which are dependent on each other. e.g: The response of one publisher is used while subscribing to another publisher.
     * e.g: Given
     *          User Service (Microservice -1)
     *              - get all users
     *              - get userId for the given username
     *          Payment Service (Microservice -2)
     *              - get user balance for the given userId
     *          Order Service (Microservice -3)
     *              - get user orders for the given userId
     *  Scenario to implement:
     *      1. I have userName. I need to get user balance (flatMap will be used as the return type of the inner publisher is a Mono)
     *      2. I have userName. I need to get user orders (flatMapMany will be used as the return type of the inner publisher is a Flux)
     *      3. Get all orders from order service
     * */

    /**
     * Demo sequential non-blocking I/O calls.
     * V.Imp Note: flatMap() is used to flatten the inner mono publisher & then subscribes to that inner publisher.
     * */
    @Test
    public void flatMap_operator_test() {
        String sam = "sam";
        String mike = "mike";
        String jake = "jake";

        UserService.getUserId(mike)
                .flatMap(PaymentService::getUserBalance)
                .subscribe(RsUtil.subscriber());
        RsUtil.sleepSeconds(2);
    }

    /**
     * Demo sequential non-blocking I/O calls.
     * V.Imp Note: flatMapMany() is used to flatten the inner flux publisher & then subscribes to that inner publisher.
     * */

    @Test
    public void flatMapMany_operator_test() {
        String sam = "sam";
        String mike = "mike";
        String jake = "jake";

        UserService.getUserId(jake)
                .flatMapMany(OrderService::getUserOrders)
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
     * The important point is that the pipeline does not care which threads the elements come from – it just pays attention to the publishers themselves.
     * */

    /* *
     * Get all orders from order service.
     * If you observe clearly, the order of orders is not intact.
     * And one more observation is that all the inner publishers are subscribed at the same time.
     * */
    @Test
    public void get_all_orders_from_order_service_test() {
        UserService.getAllUsers()
                .map(User::getUserId)
                .flatMap(OrderService::getUserOrders) // we are subscribing to an inner publisher which returns a Flux with a Flux<Integer>. That's why we are not using flatMapMany().
                .subscribe(RsUtil.subscriber("order-subscriber"));

        RsUtil.sleepSeconds(2);

        // The above test is similar to Flux.merge() behavior. We can rewrite the above test as (not recommended though)

         // OrderService.getUserOrders(1)
         //      .mergeWith(OrderService.getUserOrders(2))
         //      .mergeWith(OrderService.getUserOrders(3))
         //      .subscribe(RsUtil.subscriber("order-subscriber"));
         // RsUtil.sleepSeconds(2);
    }

}

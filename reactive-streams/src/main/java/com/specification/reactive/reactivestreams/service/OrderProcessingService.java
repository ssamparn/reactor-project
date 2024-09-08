package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/* *
 * Used in groupBy() assignment demo
 * */
public class OrderProcessingService {

    public static final Map<String, UnaryOperator<Flux<Order>>> order_processor_map = Map.of(
            "Kids", processKidsOrder(),
            "Automotive", processAutomotiveOrder()
    );

    private static UnaryOperator<Flux<Order>> processAutomotiveOrder() {
        return orderFlux -> orderFlux
                .map(order -> new Order(order.getProductName(), order.getCategory(), order.getPrice() + 100));
    }

    private static UnaryOperator<Flux<Order>> processKidsOrder() {
        return orderFlux -> orderFlux
                .flatMap(order -> createFreeOrderForKids(order).flux().startWith(order));
    }

    private static Mono<Order> createFreeOrderForKids(Order order) {
        return Mono.fromSupplier(
                () -> new Order(order.getProductName().concat("-FREE") , order.getCategory(), 0)
        );
    }

    public static Predicate<Order> canProcessOrder() {
        return order -> order_processor_map.containsKey(order.getCategory());
    }

    public static UnaryOperator<Flux<Order>> getProcessor(String orderCategory) {
        return order_processor_map.get(orderCategory);
    }
}
package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.model.Order;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/* *
 * Inventory Service: Consumes Order Streams emitted from the Order Service and provides "quantity" per order category
 *                    And more over Inventory Service will emit its data every 2 seconds.
 *
 *                    Original Inventory : Assume we have 500 quantities for each category.
 *                    So deduct quantity for every order based on the category.
 * */

public class InventoryService {

    private Map<String, Integer> inventory = new HashMap<>();

    public InventoryService() {
        inventory.put("Kids", 500);
        inventory.put("Automotive", 500);
    }

    public Consumer<Order> subscribeOrderStream() {
        return order -> inventory.computeIfPresent(order.getCategory(), (k, v) -> v - order.getQuantity());
    }

    public Flux<String> inventoryStream() {
        return Flux.interval(Duration.ofSeconds(2))
                .map(i -> inventory.toString());
    }
}

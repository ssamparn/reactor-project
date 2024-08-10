package com.nonblocking.http.client.assignment.hotandcoldpublisher;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/* *
 * Inventory Service: Consumes Order Streams emitted from the Order Service and provides "quantity" per order category
 *                    And more over Inventory Service will emit its data every 2 seconds.
 *
 *                    Original Inventory : Assume we have 500 quantities for each category.
 *                    So deduct quantity for every order based on the category.
 * */

public class InventoryService implements OrderProcessor {

    private Map<String, Integer> inventory = new HashMap<>();


    @Override
    public void consume(Order order) {
        Integer currentInventory = inventory.getOrDefault(order.category(), 500);
        Integer updatedInventory = currentInventory - order.quantity();
        inventory.put(order.category(), updatedInventory);
    }

    @Override
    public Flux<String> stream() {
        return Flux.interval(Duration.ofSeconds(2))
                .map(i -> inventory.toString());
    }
}

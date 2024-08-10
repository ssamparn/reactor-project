package com.nonblocking.http.client.assignment.hotandcoldpublisher;

import com.nonblocking.http.client.reactorclient.impl.ExternalServiceClient;
import com.nonblocking.http.client.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

/* *
 * Order Service: Provides a Stream of Orders.
 *                Order Stream will contain item, category, price (total price, it is not price per quantity) & quantity
 *                Needs a minimum 2 subscribers to emit items.
 *                Order Stream Message Format: "item:category:price:quantity"
 * */

@Slf4j
public class HotAndColdPublisherTest {

    RevenueService revenueService = new RevenueService();
    InventoryService inventoryService = new InventoryService();

    private ExternalServiceClient client = new ExternalServiceClient();

    @Test
    public void productServiceTest() {
        Flux<String> ordersStream = client.getDemo04OrdersStream();

        Flux<Order> orderFlux = ordersStream
                .map(this::parse)
                .doOnNext(order -> log.info("{}", order))
                .publish()
                .refCount(2);

        // subscribe to revenue and inventory service
        orderFlux.subscribe(order -> revenueService.consume(order));
        orderFlux.subscribe(order -> inventoryService.consume(order));

        inventoryService.stream()
                .subscribe(product -> log.info("product received: {}", product),
                        err -> log.error("error occurred: {}", err.getMessage()),
                        () -> log.info("Completed"));
        revenueService.stream()
                .subscribe(product -> log.info("product received: {}", product),
                        err -> log.error("error occurred: {}", err.getMessage()),
                        () -> log.info("Completed"));

        Util.sleepSeconds(60);
    }

    private Order parse(String message) {
        String[] strings = message.split(":");
        return new Order(strings[0], strings[1], Double.parseDouble(strings[2]), Integer.parseInt(strings[3]));
    }

}

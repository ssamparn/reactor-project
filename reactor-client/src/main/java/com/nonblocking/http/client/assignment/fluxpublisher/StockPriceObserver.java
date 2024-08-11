package com.nonblocking.http.client.assignment.fluxpublisher;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * The stock service will emit price changes every 500ms for 20 seconds.
 * The price might change between 80-120.
 *
 *      Task:
 *          - Create a subscriber with $1000 balance.
 *          - Whenever the price drops below 90, buy a stock.
 *          - When the price goes above 110,
 *              - Sell all the stocks.
 *              - Cancel the subscription.
 *              - Print the profit you made.
 * */

@Slf4j
public class StockPriceObserver implements Subscriber<Integer> {

    private int quantity = 0;
    private int balance = 1000;
    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
        this.subscription = subscription;
    }

    @Override
    public void onNext(Integer price) {
        log.info("price of stock: {}", price);
        if (price < 90 && balance >= price) {
            quantity++;
            balance = balance - price;
            log.info("bought a stock at {}, total quantity: {}, remaining balance: {}", price, quantity, balance);
        } else if (price > 110 && quantity > 0) {
            log.info("selling {} quantities at price {}", quantity, price);
            balance = balance + (quantity * price);
            int profit = balance - 1000;
            quantity = 0;
            subscription.cancel();
            log.info("after selling stocks made a profit of: {}", profit);
        }
    }

    @Override
    public void onError(Throwable ex) {
        log.error("error occurred while fetching stock price: {}", ex.getMessage());
    }

    @Override
    public void onComplete() {
        log.info("fetching stock price completed");
    }
}

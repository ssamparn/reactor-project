package com.nonblocking.http.client.assignment;

import com.nonblocking.http.client.reactorclient.impl.ExternalServiceClient;
import com.nonblocking.http.client.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class StockPriceObserverTest {

    private ExternalServiceClient client = new ExternalServiceClient();
    private StockPriceObserver subscriber = new StockPriceObserver();

    @Test
    public void stockPriceObserverTest() {
        client.getStockPrices()
                .subscribe(subscriber);

        Util.sleepSeconds(20);
    }
}

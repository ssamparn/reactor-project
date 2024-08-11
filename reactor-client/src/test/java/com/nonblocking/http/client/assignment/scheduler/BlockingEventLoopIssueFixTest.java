package com.nonblocking.http.client.assignment.scheduler;

import com.nonblocking.http.client.reactorclient.impl.ExternalServiceClient;
import com.nonblocking.http.client.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class BlockingEventLoopIssueFixTest {

    private ExternalServiceClient client = new ExternalServiceClient();

    @Test
    public void blocking_event_loop_issue_fix_test() {
        for (int i = 1; i <= 5; i++) {
            client.getProductNameWithScheduler(i)
                    .map(BlockingEventLoopIssueFixTest::processProduct)
                    .subscribe( product -> log.info("product received: {}", product),
                            err -> log.error("error occurred: {}", err.getMessage()),
                            () -> log.info("Completed")
                    );
        }
        Util.sleepSeconds(2);
    }

    // Let's introduce a problem.
    // We have introduced a time-consuming operation in our reactive chain. So the nio thread gets blocked inside the map() and emitting items sequentially now.
    private static String processProduct(String product) {
        Util.sleepMilliSeconds(300);
        return "processed-" + product;
    }
}

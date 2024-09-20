package com.nonblocking.http.client.assignment.repeat;

import com.nonblocking.http.client.reactorclient.impl.ExternalServiceClient;
import com.nonblocking.http.client.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class RepeatAssignmentTest {

    private ExternalServiceClient externalServiceClient = new ExternalServiceClient();

    /* *
     * repeat() repeats the request again and again.
     * Observe the logs at both client and server side.
     * */
    @Test
    public void repeatAssignmentTest() {
        externalServiceClient.getCountryNameForRepeat()
                .repeat()
                .takeUntil(country -> country.equalsIgnoreCase("canada"))
                .subscribe(country -> log.info("country received: {}", country),
                        err -> log.error("error occurred: {}", err.getMessage()),
                        () -> log.info("Completed"));

        Util.sleepSeconds(10);
    }
}

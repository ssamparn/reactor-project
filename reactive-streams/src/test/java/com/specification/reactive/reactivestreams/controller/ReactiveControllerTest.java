package com.specification.reactive.reactivestreams.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@WebFluxTest(controllers = ReactiveController.class)
@AutoConfigureWebTestClient(timeout = "10000")
public class ReactiveControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    final void get_simple_flux_test() {
        Flux<Integer> integerFluxResponse = webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(integerFluxResponse)
                .expectSubscription()
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .expectNext(6)
                .verifyComplete();
    }

    @Test
    final void get_simple_flux_test_2() {

        EntityExchangeResult<List<Integer>> entityExchangeResult = webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();
        Assertions.assertEquals(entityExchangeResult.getResponseBody(), Arrays.asList(1, 2, 3, 4, 5, 6));
    }

    @Test
    final void get_simple_flux_test_3() {

        webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith(response -> Assertions.assertEquals(response.getResponseBody(), Arrays.asList(1, 2, 3, 4, 5, 6)));
    }

    @Test
    final void flux_stream_test() {
        Flux<Long> longFluxResponse = webTestClient.get()
                .uri("/fluxstream")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longFluxResponse)
                .expectSubscription()
                .expectNext(0L, 1L, 2L, 3L)
                .thenCancel()
                .verify();
    }

    @Test
    final void mono_test() {
        webTestClient.get()
                .uri("/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith((response) -> {
                    Assertions.assertEquals(response.getResponseBody(), Integer.valueOf(1));
                });
    }
}

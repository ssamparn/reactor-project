package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/* *
 * To be used in flatMap() demo.
 * Imagine user-service, as an application, has 2 endpoints.
 * Imagine there would be a client class which will call these 2 endpoints (I/O requests)
 * */
public class UserService {

    private static final Map<String, Integer> userTable = Map.of(
        "sam", 1,
        "mike", 2,
        "jake", 3
    );

    public static Flux<User> getAllUsers() {
        return Flux.fromIterable(userTable.entrySet())
                .map(entry -> new User(entry.getValue(), entry.getKey()));
    }

    public static Mono<Integer> getUserId(String userName) {
        return Mono.fromSupplier(() -> userTable.getOrDefault(userName, 0));
    }
}

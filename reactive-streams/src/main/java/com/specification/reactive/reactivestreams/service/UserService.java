package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.model.User;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/* *
 * To be used in flatMap() demo.
 * To be used in concatMap() demo.
 * To be used in zipWhen() demo.
 * To be used in collectList() demo.
 * To be used in Combining publishers assignment.
 * Imagine user-service, as an application, has 2 endpoints. getAllUsers() and getUserId() based on userName.
 * Imagine there would be a client class which will call these 2 endpoints (I/O requests)
 * */

@Slf4j
@Service
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
        return Mono.fromSupplier(() -> userTable.getOrDefault(userName, 0))
                .doOnNext(value -> log.info("User id: {} based on userName: {}", value, userName));
    }

    /* *
     * UserService provides a method getUser() to retrieve user data based on a given userId.
     * It returns a Mono<User> representing user information (userid, userName).
     * */
    public Mono<User> getUser(String userId) {
        String userName = RsUtil.faker().name().fullName();
        log.info("Getting user {}", userName);
        return Mono.defer(() -> Mono.just(new User(Integer.parseInt(userId), userName)));
    }
}

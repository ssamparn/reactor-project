package com.specification.reactive.reactivestreams.service;

import com.specification.reactive.reactivestreams.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* *
 * The User Database handles the persistence of user data to a database.
 * We are using a concurrent map to represent data storage here for simplicity.
 *
 * It provides a saveUserData() method that takes user information and returns a Mono<Boolean> to signify the success or failure of the database operation.
 * */

@Slf4j
@Component
public class UserDatabase {

    private static final Map<String, User> user_db = new ConcurrentHashMap<>();

    public Mono<Boolean> saveUserData(User user) {
        return Mono.create(sink -> {
            try {
                user_db.putIfAbsent(String.valueOf(user.getUserId()), user);
                log.info("Saved user: {} to database", user.getUserName());
                sink.success(true);
            } catch (Exception e) {
                sink.success(false);
            }
        });
    }
}

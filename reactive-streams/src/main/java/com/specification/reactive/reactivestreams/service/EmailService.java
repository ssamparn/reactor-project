package com.specification.reactive.reactivestreams.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/* *
 * To be used in zipWhen() demo.
 * Imagine email-service, sending email to the user provided.
 * */

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final UserService userService;

    /* *
     * EmailService is responsible for sending emails to users.
     * Importantly, it depends on the UserService to fetch user details and then send an email based on the retrieved information.
     * The sendEmail() method returns a Mono<Boolean> indicating whether the email was sent successfully or not.
     * */

    public Mono<Boolean> sendEmail(String userId) {
        return userService.getUser(userId)
                .flatMap(user -> {
                    log.info("Sending email to: {}", user.getUserName());
                    return Mono.just(true);
                })
                .switchIfEmpty(Mono.just(false));
    }
}

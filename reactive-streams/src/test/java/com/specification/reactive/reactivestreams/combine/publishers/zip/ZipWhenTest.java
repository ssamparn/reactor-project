package com.specification.reactive.reactivestreams.combine.publishers.zip;

import com.specification.reactive.reactivestreams.model.User;
import com.specification.reactive.reactivestreams.service.EmailService;
import com.specification.reactive.reactivestreams.service.UserDatabase;
import com.specification.reactive.reactivestreams.service.UserService;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

/* *
 * zipWhen(): Mono.zipWhen() (as there is no zipWhen() in Flux) is an operator that allows us to combine the results of two or more Mono streams in a coordinated manner.
 * It’s commonly used when we have multiple asynchronous operations to be performed concurrently, and we need to combine their results into a single output.
 * We start with two or more Mono streams that represent asynchronous operations. These Mono publishers can emit different types of data, and they may or may not have dependencies on each other.
 * We then use zipWhen() to coordinate. We apply the zipWhen() operator to one of the Mono publisher. This operator waits for the first Mono to emit a value and then uses that value to trigger the execution of other Mono publishers.
 * The result of zipWhen() is a new Mono that combines the results of all the Mono publishers into a single data structure, typically a Tuple or an object that you define (using map()).
 * Finally, we can specify how we want to combine the results of the Mono publishers. We can use the combined values to create a new object, perform calculations, or construct a meaningful response.
 *
 * */
@Slf4j
public class ZipWhenTest {

    /* *
     * We are going to make use of UserService, EmailService, UserDatabase.
     * We will retrieve user from UserService & make a call to EmailService and then store the user in the User Database
     * */

    private UserService userService = new UserService();
    private EmailService emailService = new EmailService(userService);
    private UserDatabase userDatabase = new UserDatabase();

    record UserDetails(String userId, String userName, Boolean emailSent, Boolean userStored) {

    }

    @Test
    public void zipWhen_test() {
        Mono<User> userMono = userService.getUser("1");

        Mono<Boolean> emailSentMono = emailService
                .sendEmail("1") // email will be sent in a different thread pool.
                .subscribeOn(Schedulers.parallel())
                .doOnNext(i -> log.info("Sending emails in a different thread pool"));

        Mono<Boolean> userStoredInDbMono = userMono.flatMap(user -> userDatabase.saveUserData(user));

        Mono<UserDetails> userDetailsMono = userMono.zipWhen(user -> emailSentMono, (user, emailSent) -> Tuples.of(user, emailSent))
                .zipWhen(t -> userStoredInDbMono, (t, userStored) -> {
                    String userName = t.getT1().getUserName();
                    Boolean emailSent = t.getT2();
                    return new UserDetails("1", userName, emailSent, userStored);
                });

        userDetailsMono.subscribe(RsUtil.subscriber());
    }
}

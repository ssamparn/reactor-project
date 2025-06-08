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
 * In short, Mono.zipWhen() is a powerful operator in Project Reactor that allows you to trigger a dependent asynchronous operation based on the result of a Mono, and then combine both results.
 * How it works?
 *
 *   Mono<A> monoA = ...;
 *   Mono<B> result = monoA.zipWhen(a -> asyncFunction(a));
 *
 *  - It waits for monoA to emit.
 *  - Then it calls the function with the emitted value to get another Mono<B>.
 *  - Finally, it zips the original value (A) with the result of the function (B), producing a Tuple2<A, B>.
 *
 * Use Cases for Mono.zipWhen():
 *
 *   1. Fetching Related Data Based on Initial Result:
 *
 *      Mono<User> userMono = getUserById("123");
 *      Mono<Tuple2<User, Account>> userWithAccount = userMono.zipWhen(user -> getAccountByUserId(user.getId()));
 *
 *   Use case: Fetch a user, then fetch their account using the user ID.
 *
 *   2. Enriching Data:
 *
 *     Mono<Order> orderMono = getOrder();
 *     Mono<Tuple2<Order, ShippingInfo>> enrichedOrder = orderMono.zipWhen(order -> getShippingInfo(order.getShippingId()));
 *
 *   Use case: Add shipping info to an order after retrieving it.
 *
 *   3. Conditional Logic or Validation:
 *
 *     Mono<User> userMono = getUser();
 *     Mono<Tuple2<User, Boolean>> userWithValidation = userMono.zipWhen(user -> validateUser(user));
 *
 *   Use case: Validate a user after fetching them, and combine the result.
 *
 *   4. Audit or Logging Side Effects:
 *
 *     Mono<Event> eventMono = getEvent();
 *     Mono<Tuple2<Event, Void>> loggedEvent = eventMono.zipWhen(event -> logEvent(event));
 *
 *   Use case: Log an event after retrieving it, and continue with both results.
 *
 *   5. Chaining Dependent Asynchronous Calls:
 *
 *     Mono<Product> productMono = getProduct();
 *     Mono<Tuple2<Product, Discount>> productWithDiscount = productMono.zipWhen(product -> getDiscount(product.getCategory()));
 *
 *   Use case: Fetch a product, then fetch a discount based on its category.
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

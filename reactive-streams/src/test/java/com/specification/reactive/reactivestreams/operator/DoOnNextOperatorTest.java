package com.specification.reactive.reactivestreams.operator;

/**
 * We can use doOnNext() to mutate or change or set the value of an object.
 * But don't think mutation is bad. We understand Immutability is good, but that does not mean mutation is bad.
 * Functional programming prefers pure functions (with no side effects). We also prefer pure functions, with no side effects. However, it is not applicable for all scenarios.
 * e.g: Our database entity classes are mutable objects. So we have to mutate or set the value of these classes on doOnNext() call.
 *
 *      interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
 *          Mono<Customer> findById(Long id);
 *      }
 *
 *      // traditional code
 *      Customer> customer = this.repository.findById(123).get();
 *      customer.setAge(10);
 *      this.repository.save(customer);
 *
 *      // reactive
 *      this.repository.findById(123)
 *          .doOnNext(c -> c.setAge(10))
 *          .flatMap(this.repository::save);
 *
 * */
public class DoOnNextOperatorTest {
}

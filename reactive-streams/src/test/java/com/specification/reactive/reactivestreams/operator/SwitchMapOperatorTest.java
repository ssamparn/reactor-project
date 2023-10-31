package com.specification.reactive.reactivestreams.operator;

public class SwitchMapOperatorTest {

    /* *
     * switchMap(): The switchMap() operator must not be confused with concatMap(), as it looks very similar at the first glance.
     * However, it works by cancelling the previous inner subscriber whenever the outer publisher emits an item.
     * The name of the operator is derived from this switch from the previous to the new publisher.
     * As long as the outer publisher does not emit a new item, values from the current inner publisher will be propagated to the result sequence.

     * Now the big question is: When could this behaviour be useful?
     * In a scenario where the previous emitted values are not relevant anymore, but only the current ones, this operator can be used.
     * For example for an infinite stream of hot data, like keystrokes in a user search. If a user types a new character, we immediately want to update the search and we don’t care about previous search results, because they’re actually outdated.
     * In this exact example, the switchMap() operator will most likely be combined with debouncing behaviour, so that not every key stroke actually leads to a new call in the backend, relieving our precious servers a bit.
     * */

    /* *
     * Use flatMap() for fast parallel calls, where ordering and interleaving cannot be guaranteed.
     * Use concatMap() for strict natural order and no interleaving.
     * Use flatMapSequential() for parallel calls with strict order and no interleaving by taking advantage of queueing up elements of slower inner publishers.
     * Use switchMap() for situations, where only the current data is of importance and dropping previous data is explicitly desired.
     * */
}

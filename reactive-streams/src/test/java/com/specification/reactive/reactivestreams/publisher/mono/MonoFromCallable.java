package com.specification.reactive.reactivestreams.publisher.mono;

import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
public class MonoFromCallable {

    /* *
     * Difference between Mono.fromCallable() and Mono.fromSupplier():
     *
     * Both Callable and Supplier are functional interfaces in java.util.concurrent and java.util.function packages respectively
     * If you use Mono.fromCallable() it computes a result or throws a checked exception if unable to do so.
     * But if you use Mono.fromSupplier(), then you get a result. You can only get a result or a run time exception but not checked exception.
     *
     * Let's take a look at their method signature
     * ------------------------------------
     *  public interface Callable<V> {
     *      V call() throws Exception;
     *  }
     * ------------------------------------
     *  public interface Supplier<T> {
     *      T get();
     *  }
     * ------------------------------------
     *
     * Use Mono.fromCallable() for a task that returns a result and may throw a checked exception (IO Exception). This is useful for tasks like reading and writing to files, where many kinds of checked exceptions (IOExceptions) can be thrown. Callable is also designed to be run on another thread (e.g: Executor Service).
     * Use Mono.fromSupplier() for a task that is very unlikely to throw a checked exception. So if you use Mono.fromSupplier() to wrap a method or task which throws checked exceptions instead of Mono.fromCallable(), then you have to handle the exception with a try-catch block. Whereas with Mono.fromCallable() handling exception is not required.
     *
     * Note: Both Mono.fromCallable() & Mono.fromSupplier() provides a cold publisher.
     * */

    @Test
    public void mono_from_Callable_test() {
        Callable<String> stringCallable = () -> getName();

        Mono<String> stringMono = Mono.fromCallable(stringCallable);

        stringMono.subscribe(RsUtil.onNext());
    }

    private static String getName() {
        log.info("Generating Name: ");
        return RsUtil.faker().name().fullName();
    }
}

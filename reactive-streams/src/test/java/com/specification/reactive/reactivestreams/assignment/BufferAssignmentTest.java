package com.specification.reactive.reactivestreams.assignment;

import com.specification.reactive.reactivestreams.model.BookOrder;
import com.specification.reactive.reactivestreams.model.RevenueReport;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/* *
 * We have a Book Order Service which emits BookOrder event every 100ms.
 * A book has 3 characteristics viz Genre, Title, Price.
 * Assignment is to generate a report every 2 seconds for the revenue made on the following genres.
 * "Science fiction", "Fantasy", "Suspense/Thriller"
 * */
@Slf4j
public class BufferAssignmentTest {

    private static final Set<String> allowedBookCategories = Set.of(
            "Science fiction",
            "Fantasy",
            "Suspense/Thriller"
    );

    @Test
    public void buffer_assignment_test() {
        getBookOrders()
                .filter(bookOrder -> allowedBookCategories.contains(bookOrder.genre()))
                .buffer(Duration.ofSeconds(2))
                .map(BufferAssignmentTest::createRevenueReport)
                .subscribe(RsUtil.subscriber());

        RsUtil.sleepSeconds(60);
    }

    private static Flux<BookOrder> getBookOrders() {
        return Flux.interval(Duration.ofMillis(100))
                .map(i -> BookOrder.create());
    }

    private static RevenueReport createRevenueReport(List<BookOrder> bookOrders) {
        Map<String, Integer> bookCategoryRevenue = bookOrders.stream()
                .collect(Collectors.groupingBy(
                        BookOrder::genre, Collectors.summingInt(BookOrder::price)
                ));
        return new RevenueReport(LocalDateTime.now(), bookCategoryRevenue);
    }
}
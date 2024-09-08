package com.specification.reactive.reactivestreams.model;

import java.time.LocalDateTime;
import java.util.Map;

public record RevenueReport(LocalDateTime time,
                            Map<String, Integer> bookCategoryRevenue) {
}
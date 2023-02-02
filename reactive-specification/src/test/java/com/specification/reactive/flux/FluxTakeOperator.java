package com.specification.reactive.flux;

import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxTakeOperator {

    @Test
    public void fluxTakeTest() {
        Flux.range(1, 10)
                .log()
                .take(7)
                .log()
                .subscribe(ReactiveSpecificationUtil.subscriber());
    }
}

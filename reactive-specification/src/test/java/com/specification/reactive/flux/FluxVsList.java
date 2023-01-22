package com.specification.reactive.flux;

import com.specification.reactive.util.NameGeneratorUtil;
import com.specification.reactive.util.ReactiveSpecificationUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FluxVsList {

    @Test
    public void fluxVsListTest() {
        List<String> names = NameGeneratorUtil.getNamesViaList(5);
        System.out.println(names);

        NameGeneratorUtil.getNamesViaFlux(5)
                .subscribe(ReactiveSpecificationUtil.onNext());
    }
}

package com.specification.reactive.reactivestreams.flux;

import com.specification.reactive.reactivestreams.util.NameGeneratorUtil;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FluxVsList {

    @Test
    public void flux_Vs_list_test() {
        List<String> names = NameGeneratorUtil.getNamesViaList(5);
        System.out.println(names);

        NameGeneratorUtil.getNamesViaFlux(5)
                .subscribe(RsUtil.onNext());
    }
}

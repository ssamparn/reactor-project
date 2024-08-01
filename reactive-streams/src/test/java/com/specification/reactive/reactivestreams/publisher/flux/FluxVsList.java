package com.specification.reactive.reactivestreams.publisher.flux;

import com.specification.reactive.reactivestreams.util.NameGeneratorUtil;
import com.specification.reactive.reactivestreams.util.RsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class FluxVsList {

    @Test
    public void flux_vs_list_getting_names_via_list_test() {
        List<String> names = NameGeneratorUtil.getNamesViaList(5);
        log.info(String.valueOf(names)); // will take the entire 5 seconds for the list to be displayed
    }

    @Test
    public void flux_vs_list_getting_names_via_flux_test() {
        NameGeneratorUtil.getNamesViaFlux(5)
                .subscribe(RsUtil.onNext()); // Will not wait for 5 seconds for the flux to emit items. As and when the item is there, flux will emit it every second.
    }
}

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
        // will take 5 seconds completely for the list to be displayed
        List<String> names = NameGeneratorUtil.getNamesList(5);
        log.info(String.valueOf(names));
    }

    @Test
    public void flux_vs_list_getting_names_via_flux_test() {
        // Will not wait for 5 seconds for the flux to emit items. As and when the item is there, flux will emit it every second.
        // So returning a Flux<String> instead of a List<String> make publisher responsible.
        NameGeneratorUtil.getNamesFlux(5)
                .subscribe(RsUtil.onNext());
    }
}

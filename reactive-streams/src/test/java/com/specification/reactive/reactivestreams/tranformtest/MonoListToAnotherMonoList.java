package com.specification.reactive.reactivestreams.tranformtest;

import com.specification.reactive.reactivestreams.model.MasterPair;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

public class MonoListToAnotherMonoList {

    @Test
    public void transform_monoOfList_ToAnotherMonoOfList_test() {

        List<MasterPair> masterPairs = List.of(
                new MasterPair(1L),
                new MasterPair(2L),
                new MasterPair(3L)
        );

        Mono<List<MasterPair>> monoListMasterPair = Mono.just(masterPairs);

        Mono<List<Long>> monoListLong = monoListMasterPair
                .flatMapIterable(pairs -> pairs)
                .map(MasterPair::getPrivateKey)
                .collectList();

        StepVerifier.create(monoListLong)
                .expectNextCount(1)
                .verifyComplete();

    }
}

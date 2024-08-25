package com.specification.reactive.reactivestreams.tranformtest;

import com.specification.reactive.reactivestreams.model.MasterPair;
import com.specification.reactive.reactivestreams.util.RsUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/* *
 * We can use either flatMapMany() or flatMapIterable() to convert a Mono<List<T>> to a Flux<T>
 * */
public class MonoListToAnotherMonoList {

    private static final List<MasterPair> masterPairs = List.of(
            new MasterPair(1L),
            new MasterPair(2L),
            new MasterPair(3L)
    );

    @Test
    public void transform_monoOfList_ToAnotherMonoOfList_using_flatMapMany_test() {
        Mono<List<MasterPair>> monoListMasterPair = Mono.fromSupplier(() -> masterPairs);

        Mono<List<Long>> monoListLong = monoListMasterPair
                .flatMapMany(Flux::fromIterable)
                .map(MasterPair::getPrivateKey)
                .collectList();

        monoListLong.subscribe(RsUtil.subscriber("flatMapMany() subscriber"));
    }

    @Test
    public void transform_monoOfList_ToAnotherMonoOfList_using_flatMapIterable_test() {
        Mono<List<MasterPair>> monoListMasterPair = Mono.fromSupplier(() -> masterPairs);

        Mono<List<Long>> monoListLong = monoListMasterPair
                .flatMapIterable(pairs -> pairs)
                .map(MasterPair::getPrivateKey)
                .collectList();

        monoListLong.subscribe(RsUtil.subscriber("flatMapIterable() subscriber"));
    }
}

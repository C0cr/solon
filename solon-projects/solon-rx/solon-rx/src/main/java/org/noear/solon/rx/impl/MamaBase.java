package org.noear.solon.rx.impl;

import org.noear.solon.rx.Baba;
import org.noear.solon.rx.Mama;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.function.Function;

/**
 * @author noear 2025/1/28 created
 */
public abstract class MamaBase<T> extends AbstractPublisher<T, Mama<T>> implements Mama<T> {
    @Override
    public <R> Mama<R> flatMap(Function<? super T, ? extends Publisher<? extends R>> mapper) {
        return new MamaFlatMap<>(this, mapper);
    }

    @Override
    public <R> Mama<R> map(Function<? super T, ? extends R> mapper) {
        return new MamaMap<>(this, mapper);
    }

    @Override
    public Baba<T> firstOrEmpty() {
        return new BabaTake<>(this, 1);
    }

    @Override
    public Baba<List<T>> collectList() {
        return new BabaCollect<>(this);
    }
}
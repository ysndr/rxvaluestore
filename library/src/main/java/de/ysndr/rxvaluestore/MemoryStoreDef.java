package de.ysndr.rxvaluestore;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by yannik on 2/22/17.
 */

@RxValueStore
@Value.Immutable
abstract class MemoryStoreDef<T> extends Store<BehaviorSubject<T>, T> {
    @Override
    @Value.Lazy
    @Value.Parameter(false)
    public BehaviorSubject<T> store() {
        return BehaviorSubject.create(initial().get());
    }

    @Override
    abstract Optional<T> initial();

    @Override
    public Observable<T> observable() {
        return store().asObservable().share();
    }

    @Value.Check
    protected void check() {
        Preconditions.checkState(initial().isPresent(),
                "'initial' should be defined to apply the store");
    }

    @Override
    public <O> Observable.Transformer<O, T> apply() {
        // set default value to store
        // check would have failed if initial was not present
        if (!store().hasValue() && initial().isPresent()) store().onNext(initial().get());

        return source -> source
                .flatMap(__ -> store().share().first());
//                .compose(update()); // readd that element back to the store for caching
    }

    @Override
    public Observable.Transformer<T, T> update() {
        return source -> source
                .doOnNext(store()::onNext); // update Subject
    }
}

package de.ysndr.rxvaluestore;

import com.google.common.base.Optional;
import com.pacoworks.rxobservablediskcache.RxObservableDiskCache;
import org.immutables.value.Value;
import rx.Observable;
import rx.Single;
import rx.subjects.BehaviorSubject;

import java.util.NoSuchElementException;

/**
 * Created by yannik on 2/22/17.
 */

@Value.Immutable @RxValueStore
abstract class RxCacheStoreDef<T, P> extends Store<RxObservableDiskCache<T, P>, T> {

    abstract String key();
    abstract RxObservableDiskCache<T, P> store();
    /**
     * the initial value used before any value was inserted via `update()`
     */
    abstract Optional<T> initial();

    @Value.Lazy
    protected BehaviorSubject<T> proxy() {
        return (initial().isPresent())
                ? BehaviorSubject.create(initial().get())
                : BehaviorSubject.create();
    }

    @Override
    @Value.Lazy
    Observable<T> observable() {
        return proxy().asObservable().share();
    }

    @Override
    public <O> Observable.Transformer<O, T> apply() {
        return source -> source
                .flatMap(__ -> store().transform(Observable.<T>empty().toSingle(), key())
                        .map(cached -> cached.value)
                        .first()
                        .onErrorResumeNext(error ->
                                (error instanceof NoSuchElementException && initial().isPresent())
                                        ? Observable.just(initial().get())
                                        : Observable.error(error)));
    }

    @Override
    public Observable.Transformer<T, T> update() {
        BehaviorSubject<T> proxyRef = proxy();

        return source -> source
                .doOnNext(System.out::println)
                .flatMap(value -> store()
                        .transform(Single.just(value), key())
                        .last() // first is old value
                        .map(cached -> cached.value)
                        .doOnNext(System.out::println)
                )
                .doOnNext(__ -> System.out.println("updated: " + __))
                .doOnNext(proxy()::onNext);
    }
}

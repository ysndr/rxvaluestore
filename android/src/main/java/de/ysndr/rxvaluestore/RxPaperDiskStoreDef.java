package de.ysndr.rxvaluestore;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.pacoworks.rxpaper.RxPaperBook;
import org.immutables.value.Value;
import rx.Observable;

/**
 * Created by yannik on 2/22/17.
 */

@Value.Immutable @RxValueStore
abstract class RxPaperDiskStoreDef<T> extends Store<RxPaperBook, T> {


    public abstract String key();
    public abstract RxPaperBook store();
    public abstract Optional<T> initial();

    @Value.Check
    protected void check() {
        Preconditions.checkState(!initial().isPresent(),
                "'initial' should be defined to apply the store");
    }

    @Override
    public Observable<T> observable() {
        return store().<T>observeUnsafe(key());
    }


    @Override
    public <O> Observable.Transformer<O, T> apply() {
        return source -> {
            return source
                    .flatMap(__ -> store().read(key(), initial().get())
                            .toObservable());
                    //.doOnUnsubscribe(() -> book().delete(key()))
        };
    }


    @Override
    public Observable.Transformer<T, T> update() {
        return source -> source
                .distinctUntilChanged()
                .doOnNext(value -> store().write(key(), value));
    }
}

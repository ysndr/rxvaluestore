package de.ysndr.rxvaluestore;

import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

/**
 * Created by yannik on 3/12/17.
 */
public class MemoryStoreDefTest {


    MemoryStore<String> store;
    TestSubscriber<String> subscriber;

    @Before
    public void before() {
        store = MemoryStore.of(Optional.of("Hello"));
        subscriber = TestSubscriber.create();
    }

    @Test
    public void observable() throws Exception {
        store.observable().subscribe(subscriber);

        subscriber.assertValueCount(1);
        subscriber.assertNotCompleted();
        subscriber.assertValues("Hello");
    }

    @Test(expected = IllegalStateException.class)
    public void checkAbsent() throws  Exception {
        MemoryStore<String> test= MemoryStore.of(Optional.absent());
    }

    @Test
    public void checkPresent() throws  Exception {
        MemoryStore<String> test= MemoryStore.of(Optional.of("Exists"));
    }

    @Test
    public void apply() throws Exception {
        Observable.never()
                .<String>startWith("Not Hello")
                .compose(store.apply())
                .subscribe(subscriber);

        subscriber.assertValueCount(1);
        subscriber.assertNotCompleted();
        subscriber.assertValues("Hello");
    }

    @Test
    public void update() throws Exception {
        Observable.<String>create(subscr -> subscr.onNext("World"))
                .compose(store.update())
                .subscribe(subscriber);

        subscriber.assertValueCount(1);
        subscriber.assertNotCompleted();
        subscriber.assertValues("World");
    }

}
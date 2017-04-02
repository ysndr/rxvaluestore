package de.ysndr.rxvaluestore;

import android.app.Application;
import com.google.common.base.Optional;
import com.pacoworks.rxobservablediskcache.RxObservableDiskCache;
import com.pacoworks.rxpaper.RxPaperBook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaTestPlugins;


/**
 * Created by yannik on 3/29/17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RxCacheStoreDefTest {

    private Application application;
    private RxPaperBook book;
    private RxObservableDiskCache<String, MockPolicy> cache;
    private RxCacheStore<String, MockPolicy> store;
    private TestSubscriber subscriber;

    class MockPolicy {}

    // @Before => JUnit 4 annotation that specifies this method should run before each test is run
    // Useful to do setup for objects that are needed in the test
    @Before
    public void setup() {
        // Convenience method to run MainActivity through the Activity Lifecycle methods:
        // onCreate(...) => onStart() => onPostCreate(...) => onResume()
        RxJavaTestPlugins.setImmediateScheduler();

        application = RuntimeEnvironment.application;
        RxPaperBook.init(application);
        book = RxPaperBook.with("test_cache_book");
        cache = RxObservableDiskCache.create(book, value -> new MockPolicy(), policy -> true);
        store = RxCacheStore.of("test_key", cache, Optional.absent());
        subscriber = TestSubscriber.create();

    }

    @After
    public void teardown() throws Exception {
        book.destroy();
        RxJavaTestPlugins.resetJavaTestPlugins();
    }

    // @Test => JUnit 4 annotation specifying this is a test to be run
    // The test simply checks that our TextView exists and has the text "Hello world!
    @Test
    public void update() {
        Observable.<String>never().startWith("String")
                .doOnNext(System.out::println)
                .compose(store.update())
                .map(value -> "And now for something completely different")
                .compose(store.update())
                .doOnNext(System.out::println)
                .compose(store.apply())
                .subscribe(subscriber);

        subscriber.assertValue("And now for something completely different");
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotCompleted();
    }

    @Test
    public void apply() {
        Observable.<String>never().startWith("String")
                .doOnNext(System.out::println)
                .compose(store.update())
                .compose(store.apply())
                .subscribe(subscriber);

        subscriber.assertValue("String");
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotCompleted();
    }

    @Test
    public void applyMulti() {
        Observable.<String>never().startWith("String")
                .doOnNext(System.out::println)
                .compose(store.update())
                .compose(store.apply())
                .compose(store.apply())
                .compose(store.apply())
                .subscribe(subscriber);

        subscriber.assertValue("String");
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotCompleted();
    }

    @Test
    public void observe() {
        // hey :*
        Observable<String> value$ = store.observable();
        value$.subscribe(subscriber);

        Observable.<String>never()
                .startWith("Hello")
                .compose(store.update())
                .map(value -> "World")
                .compose(store.update())
                .map(value -> "!")
                .compose(store.update())
                .subscribe();

        subscriber.assertNotCompleted();
        subscriber.assertValueCount(3);
        subscriber.assertValues("Hello", "World", "!");
    }

    @Test
    public void observeInitial() {

        RxCacheStore<String, MockPolicy> storeCpy = store.withInitial("Hello");
        Observable<String> value$ = storeCpy.observable();
        value$.subscribe(subscriber);

        Observable.<String>never()
                .startWith("World")
                .compose(storeCpy.update())
                .map(value -> "!")
                .compose(storeCpy.update())
                .subscribe();

        subscriber.assertNotCompleted();
        subscriber.assertValues("Hello", "World", "!");
        subscriber.assertValueCount(3);
    }


}

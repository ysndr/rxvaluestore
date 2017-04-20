package de.ysndr.rxvaluestore;

import org.immutables.value.Value;
import org.javatuples.*;
import rx.Observable;
import rx.Single;


/**
 * Created by yannik on 2/22/17.
 */

public abstract class Store <S, T> {

    @Value.Parameter
    abstract S store();

    @Value.Parameter
    abstract T initial();

    abstract Observable<T> observable();

    public Single<T> single() {
        return observable().toSingle();
    }

    abstract <O> Observable.Transformer <O, T> apply();

    public <O> Observable.Transformer <O, Pair<T, O>> attach() {
        return source -> source.flatMap(value0 -> single().map( value -> Pair.with(value, value0)).toObservable());
    }

    public <O1> Observable.Transformer<Unit<O1>, Pair<T, O1>> attachToUnit() {
        return source -> source.flatMap(tuple -> single().map(tuple::addAt0).toObservable());
    }
    public <O1, O2> Observable.Transformer<Pair<O1, O2>, Triplet<T, O1, O2>> attachToPair() {
        return source -> source.flatMap(tuple -> single().map(tuple::addAt0).toObservable());
    }
    public <O1, O2, O3> Observable.Transformer<Triplet<O1, O2, O3>, Quartet<T, O1, O2, O3>> attachToTriplet() {
        return source -> source.flatMap(tuple -> single().map(tuple::addAt0).toObservable());
    }
    public <O1, O2, O3, O4> Observable.Transformer<Quartet<O1, O2, O3, O4>, Quintet<T, O1, O2, O3, O4>> attachToQuartet() {
        return source -> source.flatMap(tuple -> single().map(tuple::addAt0).toObservable());
    }
    public <O1, O2, O3, O4, O5> Observable.Transformer<Quintet<O1, O2, O3, O4, O5>, Sextet<T, O1, O2, O3, O4, O5>> attachToQuintet() {
        return source -> source.flatMap(tuple -> single().map(tuple::addAt0).toObservable());
    }
    public <O1, O2, O3, O4, O5, O6>  Observable.Transformer<Sextet<O1, O2, O3, O4, O5, O6>, Septet<T, O1, O2, O3, O4, O5, O6>> attachToSextet() {
        return source -> source.flatMap(tuple -> single().map(tuple::addAt0).toObservable());
    }
    public <O1, O2, O3, O4, O5, O6, O7> Observable.Transformer<Septet<O1, O2, O3, O4, O5, O6, O7>, Octet<T, O1, O2, O3, O4, O5, O6, O7>> attachToSeptet() {
        return source -> source.flatMap(tuple -> single().map(tuple::addAt0).toObservable());
    }
    public <O1, O2, O3, O4, O5, O6, O7, O8> Observable.Transformer<Octet<O1, O2, O3, O4, O5, O6, O7, O8>, Ennead<T, O1, O2, O3, O4, O5, O6, O7, O8>> attachToOctet() {
        return source -> source.flatMap(tuple -> single().map(tuple::addAt0).toObservable());
    }
    public <O1, O2, O3, O4, O5, O6, O7, O8, O9> Observable.Transformer<Ennead<O1, O2, O3, O4, O5, O6, O7, O8, O9>, Decade<T, O1, O2, O3, O4, O5, O6, O7, O8, O9>> attachToEnead() {
        return source -> source.flatMap(tuple -> single().map(tuple::addAt0).toObservable());
    }

    public abstract Observable.Transformer <T, T> update();
}

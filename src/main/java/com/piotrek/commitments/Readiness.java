package com.piotrek.commitments;

import com.piotrek.Driver;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


/**
 * Created by Piotrek on 2016-10-24.
 */
public class Readiness {
    private final HashMap<LocalDate, Collection<Excuse>> excuses = new HashMap<LocalDate, Collection<Excuse>>();

    public void add(Excuse excuse) {
        if(!excuses.containsKey(excuse.getDate())){
            excuses.put(excuse.getDate(), new ArrayList<Excuse>());
        }
        excuses.get(excuse.getDate()).add(excuse);
    }

    public Single<Commitment> getCommitment(final LocalDate date, final Driver driver) {
        return Observable.fromIterable(excuses.keySet())
                .filter(
                        new Predicate<LocalDate>() {
                            @Override
                            public boolean test(@NonNull LocalDate localDate) throws Exception {
                                return localDate.equals(date);
                            }
                        }
                )
                .flatMap(
                        new Function<LocalDate, Observable<Excuse>>() {
                            @Override
                            public Observable<Excuse> apply(@NonNull LocalDate localDate) throws Exception {
                                return Observable.fromIterable(excuses.get(localDate));
                            }
                        }
                )
                .filter(
                        new Predicate<Excuse>() {
                            @Override
                            public boolean test(@NonNull Excuse excuse) throws Exception {
                                return excuse.matches(date, driver);
                            }
                        }
                )
                .map(
                        new Function<Excuse, Commitment>() {
                            @Override
                            public Commitment apply(@NonNull Excuse excuse) throws Exception {
                                return excuse.getCommitment();
                            }
                        }
                )
                .first(new ReadyToDrive(date, driver));
    }

}

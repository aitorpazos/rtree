package com.github.davidmoten.rtree;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observables.StringObservable;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

public class GreekEarthquakes3d {

    static Observable<Entry<Object, Point>> entries() {
        Observable<String> source = Observable.using(new Func0<InputStream>() {
            @Override
            public InputStream call() {
                try {
                    return new GZIPInputStream(GreekEarthquakes3d.class
                            .getResourceAsStream("/greek-earthquakes-3d.txt.gz"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Func1<InputStream, Observable<String>>() {
            @Override
            public Observable<String> call(InputStream is) {
                return StringObservable.from(new InputStreamReader(is));
            }
        }, new Action1<InputStream>() {
            @Override
            public void call(InputStream is) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return StringObservable.split(source, "\n").flatMap(
                new Func1<String, Observable<Entry<Object, Point>>>() {

                    @Override
                    public Observable<Entry<Object, Point>> call(String line) {
                        if (line.trim().length() > 0) {
                            String[] items = line.split(" ");
                            double lat = Double.parseDouble(items[0]);
                            double lon = Double.parseDouble(items[1]);
                            double alt = Double.parseDouble(items[2]);
                            return Observable.just(Entry.entry(new Object(),
                                    Geometries.point(new double[]{lat, lon, alt})));
                        } else
                            return Observable.empty();
                    }
                });
    }

    static List<Entry<Object, Point>> entriesList() {
        return entries().toList().toBlocking().single();
    }
}

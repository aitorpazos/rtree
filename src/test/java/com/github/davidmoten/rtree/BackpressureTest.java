package com.github.davidmoten.rtree;

import static com.github.davidmoten.rtree.RTreeTest.e;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Zone;
import com.github.davidmoten.util.ImmutableStack;
import com.github.davidmoten.util.TestingUtil;

public class BackpressureTest {

    @Test
    public void testConstructorIsPrivate() {
        TestingUtil.callConstructorAndCheckIsPrivate(Backpressure.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBackpressureSearch() {
        Subscriber<Object> sub = Mockito.mock(Subscriber.class);
        ImmutableStack<NodePosition<Object, Geometry>> stack = ImmutableStack.empty();
        Func1<Geometry, Boolean> condition = Mockito.mock(Func1.class);
        Backpressure.search(condition, sub, stack, 1);
        Mockito.verify(sub, Mockito.never()).onNext(Mockito.any());
    }

    @Test
    public void testBackpressureSearchNodeWithConditionThatAlwaysReturnsFalse() {
        RTree<Object, Zone> tree = RTree.maxChildren(3).<Object, Zone> create().add(e(1))
                .add(e(3)).add(e(5)).add(e(7));

        Set<Entry<Object, Zone>> found = new HashSet<Entry<Object, Zone>>();
        tree.search(e(1).geometry()).subscribe(backpressureSubscriber(found));
        assertEquals(1, found.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRequestZero() {
        Subscriber<Object> sub = new Subscriber<Object>() {

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Object t) {

            }
        };
        sub.add(new Subscription() {
            volatile boolean subscribed = true;

            @Override
            public void unsubscribe() {
                subscribed = false;
            }

            @Override
            public boolean isUnsubscribed() {
                return !subscribed;
            }
        });
        Node<Object, Geometry> node = Mockito.mock(Node.class);
        NodePosition<Object, Geometry> np = new NodePosition<Object, Geometry>(node, 1);
        ImmutableStack<NodePosition<Object, Geometry>> stack = ImmutableStack
                .<NodePosition<Object, Geometry>> empty().push(np);
        Func1<Geometry, Boolean> condition = Mockito.mock(Func1.class);
        ImmutableStack<NodePosition<Object, Geometry>> stack2 = Backpressure.search(condition, sub,
                stack, 0);
        assertTrue(stack2 == stack);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRequestZeroWhenUnsubscribed() {
        Subscriber<Object> sub = new Subscriber<Object>() {

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Object t) {

            }
        };
        sub.add(new Subscription() {

            volatile boolean subscribed = true;

            @Override
            public void unsubscribe() {
                subscribed = false;
            }

            @Override
            public boolean isUnsubscribed() {
                return !subscribed;
            }
        });
        sub.unsubscribe();
        Node<Object, Geometry> node = Mockito.mock(Node.class);
        NodePosition<Object, Geometry> np = new NodePosition<Object, Geometry>(node, 1);
        ImmutableStack<NodePosition<Object, Geometry>> stack = ImmutableStack
                .<NodePosition<Object, Geometry>> empty().push(np);
        Func1<Geometry, Boolean> condition = Mockito.mock(Func1.class);
        ImmutableStack<NodePosition<Object, Geometry>> stack2 = Backpressure.search(condition, sub,
                stack, 1);
        assertTrue(stack2.isEmpty());
    }

    @Test
    public void testBackpressureIterateWhenNodeHasMaxChildrenAndIsRoot() {
        Entry<Object, Zone> e1 = RTreeTest.e(1);
        @SuppressWarnings("unchecked")
        List<Entry<Object, Zone>> list = Arrays.asList(e1, e1, e1, e1);
        RTree<Object, Zone> tree = RTree.star().maxChildren(4).<Object, Zone> create()
                .add(list);
        HashSet<Entry<Object, Zone>> expected = new HashSet<Entry<Object, Zone>>(list);
        final HashSet<Entry<Object, Zone>> found = new HashSet<Entry<Object, Zone>>();
        tree.entries().subscribe(backpressureSubscriber(found));
        assertEquals(expected, found);
    }

    @Test
    public void testBackpressureRequestZero() {
        Entry<Object, Zone> e1 = RTreeTest.e(1);
        @SuppressWarnings("unchecked")
        List<Entry<Object, Zone>> list = Arrays.asList(e1, e1, e1, e1);
        RTree<Object, Zone> tree = RTree.star().maxChildren(4).<Object, Zone> create()
                .add(list);
        HashSet<Entry<Object, Zone>> expected = new HashSet<Entry<Object, Zone>>(list);
        final HashSet<Entry<Object, Zone>> found = new HashSet<Entry<Object, Zone>>();
        tree.entries().subscribe(new Subscriber<Entry<Object, Zone>>() {

            @Override
            public void onStart() {
                request(1);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Entry<Object, Zone> t) {
                found.add(t);
                request(0);
            }
        });
        assertEquals(expected, found);
    }

    @Test
    public void testBackpressureIterateWhenNodeHasMaxChildrenAndIsNotRoot() {
        Entry<Object, Zone> e1 = RTreeTest.e(1);
        List<Entry<Object, Zone>> list = new ArrayList<Entry<Object, Zone>>();
        for (int i = 1; i <= 17; i++)
            list.add(e1);
        RTree<Object, Zone> tree = RTree.star().maxChildren(4).<Object, Zone> create()
                .add(list);
        HashSet<Entry<Object, Zone>> expected = new HashSet<Entry<Object, Zone>>(list);
        final HashSet<Entry<Object, Zone>> found = new HashSet<Entry<Object, Zone>>();
        tree.entries().subscribe(backpressureSubscriber(found));
        assertEquals(expected, found);
    }

    @Test
    public void testBackpressureIterateWhenConditionFailsAgainstNonLeafNode() {
        Entry<Object, Zone> e1 = e(1);
        List<Entry<Object, Zone>> list = new ArrayList<Entry<Object, Zone>>();
        for (int i = 1; i <= 17; i++)
            list.add(e1);
        list.add(e(2));
        RTree<Object, Zone> tree = RTree.star().maxChildren(4).<Object, Zone> create()
                .add(list);
        HashSet<Entry<Object, Zone>> expected = new HashSet<Entry<Object, Zone>>(list);
        final HashSet<Entry<Object, Zone>> found = new HashSet<Entry<Object, Zone>>();
        tree.entries().subscribe(backpressureSubscriber(found));
        assertEquals(expected, found);
    }

    @Test
    public void testBackpressureIterateWhenConditionFailsAgainstLeafNode() {
        Entry<Object, Zone> e3 = e(3);
        RTree<Object, Zone> tree = RTree.star().maxChildren(4).<Object, Zone> create()
                .add(e(1)).add(e3);
        Set<Entry<Object, Zone>> expected = Collections.singleton(e3);
        final Set<Entry<Object, Zone>> found = new HashSet<Entry<Object, Zone>>();
        tree.search(e3.geometry()).subscribe(backpressureSubscriber(found));
        assertEquals(expected, found);
    }

    @Test
    public void testBackpressureFastPathNotInitiatedTwice() {
        Entry<Object, Zone> e3 = e(3);
        RTree<Object, Zone> tree = RTree.star().maxChildren(4).<Object, Zone> create()
                .add(e(1)).add(e3);
        Set<Entry<Object, Zone>> expected = Collections.singleton(e3);
        final Set<Entry<Object, Zone>> found = new HashSet<Entry<Object, Zone>>();
        tree.search(e3.geometry()).subscribe(new Subscriber<Entry<Object, Zone>>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Entry<Object, Zone> t) {
                found.add(t);
                request(Long.MAX_VALUE);
            }
        });
        assertEquals(expected, found);
    }

    private static Subscriber<Entry<Object, Zone>> backpressureSubscriber(
            final Set<Entry<Object, Zone>> found) {
        return new Subscriber<Entry<Object, Zone>>() {

            @Override
            public void onStart() {
                request(1);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Entry<Object, Zone> t) {
                found.add(t);
                request(1);
            }
        };
    }

}

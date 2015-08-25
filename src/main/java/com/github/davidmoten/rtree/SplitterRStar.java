package com.github.davidmoten.rtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.ListPair;
import com.google.common.base.Preconditions;

public final class SplitterRStar implements Splitter {

    private final Comparator<ListPair<?>> comparator;

    private class DimComparator implements Comparator<HasGeometry>{
        private int dim;
        private boolean max;
        
        public DimComparator(int dim, boolean max){
            this.dim = dim;
            this.max = max;
        }
        
        @Override
        public int compare(HasGeometry o1, HasGeometry o2) {
            // ((Float) n1.geometry().mbr().x1()).compareTo(n2.geometry().mbr().x1());
            if (max){
                return ((Float)o1.geometry().mbr().coord2(dim)).compareTo(o2.geometry().mbr().coord2(dim));
            } else {
                return ((Float)o1.geometry().mbr().coord1(dim)).compareTo(o2.geometry().mbr().coord1(dim));
            }
        }
        
    }
    
    @SuppressWarnings("unchecked")
    public SplitterRStar() {
        this.comparator = Comparators.compose(Comparators.overlapListPairComparator,
                Comparators.areaPairComparator);
    }

    @Override
    public <T extends HasGeometry> ListPair<T> split(List<T> items, int minSize) {
        Preconditions.checkArgument(!items.isEmpty());
        // sort nodes into increasing x, calculate min overlap where both groups
        // have more than minChildren

        Map<SortType, List<ListPair<T>>> map = new HashMap<SortType, List<ListPair<T>>>(9, 1.0f);
        List<SortType> sortTypes = new ArrayList<SortType>();
        
        // We generate SortTypes map from firstItem
        if (null != items && items.size() > 0){
            HasGeometry item = (HasGeometry) items.get(0);
            float[] coord1 = item.geometry().mbr().coord1();
            for (int i=0; i<coord1.length; i++){
                SortType minSortType = new SortType(i, false);
                sortTypes.add(minSortType);
                map.put(minSortType, getPairs(minSize, sort(items, new DimComparator(i, false))));
                SortType maxSortType = new SortType(i, true);
                sortTypes.add(maxSortType);
                map.put(maxSortType, getPairs(minSize, sort(items, new DimComparator(i, true))));
            }
        }
        
        // compute S the sum of all margin-values of the lists above
        // the list with the least S is then used to find minimum overlap
        SortType leastMarginSumSortType = Collections.min(sortTypes, marginSumComparator(map));
        List<ListPair<T>> pairs = map.get(leastMarginSumSortType);

        return Collections.min(pairs, comparator);
    }

    private static class SortType {
        int dim; boolean max;
        
        public SortType(int dim, boolean max){
            this.dim = dim;
            this.max = max;
        }
        
        public boolean equals(SortType o){
            return this.dim == o.dim && this.max == o.max;
        }
        
        public int hashCode(){
            return Integer.valueOf(dim).hashCode() + Boolean.valueOf(max).hashCode();
        }
    }

    private static <T extends HasGeometry> Comparator<SortType> marginSumComparator(
            final Map<SortType, List<ListPair<T>>> map) {
        return Comparators.toComparator(new Func1<SortType, Double>() {
            @Override
            public Double call(SortType sortType) {
                return (double) marginValueSum(map.get(sortType));
            }
        });
    }

    private static <T extends HasGeometry> float marginValueSum(List<ListPair<T>> list) {
        float sum = 0;
        for (ListPair<T> p : list)
            sum += p.marginSum();
        return sum;
    }

    private static <T extends HasGeometry> List<ListPair<T>> getPairs(int minSize, List<T> list) {
        List<ListPair<T>> pairs = new ArrayList<ListPair<T>>(list.size() - 2 * minSize + 1);
        for (int i = minSize; i < list.size() - minSize; i++) {
            List<T> list1 = list.subList(0, i);
            List<T> list2 = list.subList(i, list.size());
            ListPair<T> pair = new ListPair<T>(list1, list2);
            pairs.add(pair);
        }
        return pairs;
    }

    private static <T extends HasGeometry> List<T> sort(List<T> items,
            Comparator<HasGeometry> comparator) {
        ArrayList<T> list = new ArrayList<T>(items);
        Collections.sort(list, comparator);
        return list;
    }

}

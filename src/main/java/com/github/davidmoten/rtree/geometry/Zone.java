package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.util.ObjectsHelper;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public final class Zone implements Geometry, HasGeometry {
    private final float[] coord1, coord2;
    private final int dim;
    
    protected Zone(float[] coord1, float[] coord2) {
    	Preconditions.checkArgument(coord1.length != 0 && coord1.length == coord2.length);
    	for (int i=0; i<coord1.length; i++){
    		Preconditions.checkArgument(coord2[i] >= coord1[i]);
    	}
        
        this.coord1 = coord1;
        this.coord2 = coord2;
        this.dim = coord1.length;
    }

    public int dim(){
    	return dim;
    }
    
    public float[] coord1() {
        return coord1;
    }
    
    public float coord1(int dim){
    	Preconditions.checkArgument(this.dim >= dim);
    	return coord1[dim];
    }

    public float[] coord2() {
        return coord2;
    }

    public float coord2(int dim){
    	Preconditions.checkArgument(this.dim >= dim);
    	return coord2[dim];
    }
    
    public float content() {
    	float result = 1.0F;
    	for (int i=0; i < this.dim ; i++){
    		result = result * (coord2[i] - coord1[i]);
    	}
        return result;
    }

    public Zone add(Zone r) {
    	Preconditions.checkArgument(null != r);
    	Preconditions.checkArgument(this.dim == r.dim());
        
        float[] mins = new float[this.dim];
        float[] maxs = new float[this.dim];

        for (int i=0; i<this.dim; i++){
        	mins[i] = Math.min(this.coord1[i], r.coord1()[i]);
        	maxs[i] = Math.max(this.coord2[i], r.coord2()[i]);
        }
        return new Zone(mins, maxs);
    }

    public static Zone create(float[] coord1, float[] coord2) {
        return new Zone(coord1, coord2);
    }

    public boolean contains(double[] point) {
    	Preconditions.checkArgument(this.dim == point.length);
    	
    	boolean contained = true;
    	
    	for (int i=0; i<this.dim; i++){
            if (coord1[i] < point[i] || coord2[i] > point[i]){
            	contained = false;
            }
        }
        return contained;
    }

    /**
     * This is an NP-Hard problem (http://kam.mff.cuni.cz/~hansraj/publications/minkowski_dcg.pdf).
     * Beware of using this algorithm for dim >= 5
     */
    @Override
    public boolean intersects(Zone r) {
        float xMaxLeft = Math.max(x1(), r.x1());
        float xMinRight = Math.min(x2(), r.x2());
        if (xMinRight<xMaxLeft) 
            return false;
        else {
            float yMaxBottom = Math.max(y1(), r.y1());
            float yMinTop = Math.min(y2(), r.y2());
            return yMinTop>=yMaxBottom;
        }
    }

    /**
     * This is an NP-Hard problem (http://kam.mff.cuni.cz/~hansraj/publications/minkowski_dcg.pdf).
     * Beware of using this algorithm for dim >= 5
     */
    @Override
    public double distance(Zone r) {
        if (intersects(r))
            return 0;
        else {assert
            Zone mostLeft = x1 < r.x1 ? this : r;
            Zone mostRight = x1 > r.x1 ? this : r;
            double xDifference = Math.max(0, mostLeft.x1 == mostRight.x1 ? 0 : mostRight.x1
                    - mostLeft.x2);

            Zone upper = y1 < r.y1 ? this : r;
            Zone lower = y1 > r.y1 ? this : r;

            double yDifference = Math.max(0, upper.y1 == lower.y1 ? 0 : lower.y1 - upper.y2);

            return Math.sqrt(xDifference * xDifference + yDifference * yDifference);
        }
    }

    @Override
    public Zone mbr() {
        return this;
    }

    @Override
    public String toString() {
        return "Zone [ coord1=" + coord1 + ", coord2=" + coord2 + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(coord1, coord2);
    }
    
    @Override
    public boolean equals(Object obj) {
        Optional<Zone> other = ObjectsHelper.asClass(obj, Zone.class);
        if (other.isPresent()) {
            return Objects.equal(coord1, other.get().coord1) && Objects.equal(coord2, other.get().coord2);
        } else
            return false;
    }

    public float intersectionArea(Zone r) {
        if (!intersects(r))
            return 0;
        else {

            return create(Math.max(x1, r.x1), Math.max(y1, r.y1), Math.min(x2, r.x2),
                    Math.min(y2, r.y2)).content();
        }
    }

    public float perimeter() {
        return 2 * (x2 - x1) + 2 * (y2 - y1);
    }

    @Override
    public Geometry geometry() {
        return this;
    }

}
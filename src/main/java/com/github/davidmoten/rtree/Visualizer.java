package com.github.davidmoten.rtree;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Zone;
import com.google.common.base.Optional;

public final class Visualizer {

    private final RTree<?, Geometry> tree;
    private final int width;
    private final int height;
    private final Zone view;
    private final int maxDepth;

    Visualizer(RTree<?, Geometry> tree, int width, int height, Zone view) {
        this.tree = tree;
        this.width = width;
        this.height = height;
        this.view = view;
        this.maxDepth = calculateMaxDepth(tree.root());
    }

    private static <R, S extends Geometry> int calculateMaxDepth(Optional<? extends Node<R, S>> root) {
        if (!root.isPresent())
            return 0;
        else
            return calculateDepth(root.get(), 0);
    }

    private static <R, S extends Geometry> int calculateDepth(Node<R, S> node, int depth) {
        if (node instanceof Leaf)
            return depth + 1;
        else
            return calculateDepth(((NonLeaf<R, S>) node).children().get(0), depth + 1);
    }

    public BufferedImage createImage() {
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = (Graphics2D) image.getGraphics();
        g.setBackground(Color.white);
        g.clearRect(0, 0, width, height);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));

        if (tree.root().isPresent()) {
            final List<RectangleDepth> nodeDepths = getNodeDepthsSortedByDepth(tree.root().get());
            drawNode(g, nodeDepths);
        }
        return image;
    }

    private <T, S extends Geometry> List<RectangleDepth> getNodeDepthsSortedByDepth(Node<T, S> root) {
        final List<RectangleDepth> list = getRectangleDepths(root, 0);
        Collections.sort(list, new Comparator<RectangleDepth>() {

            @Override
            public int compare(RectangleDepth n1, RectangleDepth n2) {
                return ((Integer) n1.getDepth()).compareTo(n2.getDepth());
            }
        });
        return list;
    }

    private <T, S extends Geometry> List<RectangleDepth> getRectangleDepths(Node<T, S> node,
            int depth) {
        final List<RectangleDepth> list = new ArrayList<RectangleDepth>();
        list.add(new RectangleDepth(node.geometry().mbr(), depth));
        if (node instanceof Leaf) {
            final Leaf<T, S> leaf = (Leaf<T, S>) node;
            for (final Entry<T, S> entry : leaf.entries()) {
                list.add(new RectangleDepth(entry.geometry().mbr(), depth + 2));
            }
        } else {
            final NonLeaf<T, S> n = (NonLeaf<T, S>) node;
            for (final Node<T, S> child : n.children()) {
                list.addAll(getRectangleDepths(child, depth + 1));
            }
        }
        return list;
    }

    private void drawNode(Graphics2D g, List<RectangleDepth> nodes) {
        int gridDim = tree.context().dim();
        if (gridDim < 3){
            gridDim = 1;
        }
        g.setColor(Color.BLACK);
        // Creating multidimensional grid. Using scatter plot matrix representation
        int col = 0;
        int row = 0;
        for(int i = 0; i < gridDim*gridDim; i++) {
            g.drawRect((col * width/gridDim), (row * height/gridDim), width/gridDim, height/gridDim);
            col++;
            if( col == gridDim ) {
                col = 0;
                row++;
            }
        }
        
        Font font = new Font("Tahoma", Font.BOLD, 25);
        g.setFont(font);
        for (int axis1=0; axis1<gridDim; axis1++){
            for (int axis2=0; axis2<gridDim; axis2++){
                if (gridDim>2 && axis1==axis2){
                    g.drawString("Axis " + axis1
                                    , axis1 * width/gridDim + (width/gridDim)/2
                                    , axis2 * height/gridDim + (height/gridDim)/2);
                }
            }
        }
        for (final RectangleDepth node : nodes) {
            final Color color = Color.getHSBColor(node.getDepth() / (maxDepth + 1f), 1f, 1f);
            g.setStroke(new BasicStroke(Math.max(0.5f, maxDepth - node.getDepth() + 1 - 1)));
            g.setColor(color);
            final Zone r = node.getRectangle();
            for (int axis1=0; axis1<gridDim; axis1++){
                int axis2 = 0;
                while( axis2 <= axis1){
                    if (gridDim<=2 || axis1!=axis2){
                        drawRectangle(g, r, gridDim, axis1, axis2);
                    }
                    axis2++;
                }
            }
        }
    }

    private void drawRectangle(Graphics2D g, Zone r, int gridDim, int axis1, int axis2) {
        assert(r.dim()>axis1 && r.dim()>axis2);
        final double x1 = (axis1 + (r.coord1(axis1) - view.coord1(axis1)) / 
                            (view.coord2(axis1) - view.coord1(axis1))) * (width/gridDim);
        final double y1 = (axis2 + (r.coord1(axis2) - view.coord1(axis2)) / 
                            (view.coord2(axis2) - view.coord1(axis2))) * (height/gridDim);
        final double x2 = (axis1 + (r.coord2(axis1) - view.coord1(axis1)) /
                            (view.coord2(axis1) - view.coord1(axis1))) * (width/gridDim);
        final double y2 = (axis2 + (r.coord2(axis2) - view.coord1(axis2)) /
                            (view.coord2(axis2) - view.coord1(axis2))) * (height/gridDim);
        g.drawRect(rnd(x1), rnd(y1), rnd(x2 - x1), rnd(y2 - y1));
    }

    private static int rnd(double d) {
        return (int) Math.round(d);
    }

    public void save(File file, String imageFormat) {
        ImageSaver.save(createImage(), file, imageFormat);
    }

    public void save(String filename, String imageFormat) {
        save(new File(filename), imageFormat);
    }

    public void save(String filename) {
        save(new File(filename), "PNG");
    }
}

package com.github.davidmoten.rtree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Zone;

public class Utilities {

    static List<Entry<Object, Zone>> entries1000() {
        List<Entry<Object, Zone>> list = new ArrayList<Entry<Object, Zone>>();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                BenchmarksRTree.class.getResourceAsStream("/1000.txt")));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] items = line.split(" ");
                double x = Double.parseDouble(items[0]);
                double y = Double.parseDouble(items[1]);
                list.add(Entry.entry(new Object(), Geometries.zone(new double[]{x, y}, new double[]{x + 1, y + 1})));
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
    
    static List<Entry<Object, Zone>> entries10003d() {
        List<Entry<Object, Zone>> list = new ArrayList<Entry<Object, Zone>>();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                BenchmarksRTree.class.getResourceAsStream("/1000-3d.txt")));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] items = line.split(" ");
                double x = Double.parseDouble(items[0]);
                double y = Double.parseDouble(items[1]);
                double z = Double.parseDouble(items[2]);
                list.add(Entry.entry(new Object(), Geometries.zone(new double[]{x, y, z}, new double[]{x + 1, y + 1, z + 1})));
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
    
    static List<Entry<Object, Zone>> entries10004d() {
        List<Entry<Object, Zone>> list = new ArrayList<Entry<Object, Zone>>();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                BenchmarksRTree.class.getResourceAsStream("/1000-4d.txt")));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] items = line.split(" ");
                double x = Double.parseDouble(items[0]);
                double y = Double.parseDouble(items[1]);
                double z = Double.parseDouble(items[2]);
                double u = Double.parseDouble(items[3]);
                list.add(Entry.entry(new Object(), Geometries.zone(new double[]{x, y, z, u}, new double[]{x + 1, y + 1, z + 1, u + 1})));
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}

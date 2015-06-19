/**
 * Copyright: (c) Syncleus, Inc.
 *
 * You may redistribute and modify this source code under the terms and
 * conditions of the Open Source Community License - Type C version 1.0
 * or any later version as published by Syncleus, Inc. at www.syncleus.com.
 * There should be a copy of the license included with this file. If a copy
 * of the license is not included you are granted no right to distribute or
 * otherwise use this file except through a legal and valid license. You
 * should also contact Syncleus, Inc. at the information below if you cannot
 * find a license:
 *
 * Syncleus, Inc.
 * 2604 South 12th Street
 * Philadelphia, PA 19148
 */
package com.syncleus.spangraph.spacetime;

import com.syncleus.toxi.geom.Vec3D;
import com.syncleus.toxi.geom.XYZ;

import java.util.*;


public class OctSet<V extends XYZ> extends OctBox<V> implements Set<V> {


    final Map<V, OctBox<V>> data = new HashMap();

    public OctSet(Vec3D o, Vec3D extents, Vec3D resolution) {
        super(o, extents, resolution);
    }

    @Override
    public OctBox<V> ADD(V p) {
        OctBox<V> target = super.ADD(p);
        if (target!=null) {
            data.put(p, target);
            return target;
        }
        return null;
    }

    @Override
    public boolean remove(Object p) {
        //TODO use the value in data for fast access
        if (data.remove(p)!=null) {
            super.remove(p);
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return data.containsKey(o);
    }

    @Override
    public Iterator<V> iterator() {
        return data.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return data.keySet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(V v) {
        if (ADD(v)!=null) {
            return true;
        }
        return false;
    }



    @Override
    public boolean containsAll(Collection<?> c) {
        return data.keySet().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        return putAll(c) == c.size();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        //SOON
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> points) {
        //SOON
        throw new UnsupportedOperationException();
    }
}

package com.syncleus.spangraph.spacetime;


import com.syncleus.spangraph.InfiniPeer;
import org.infinispan.Cache;
import toxi.geom.Vec3D;
import toxi.geom.XYZ;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class OctMap<K, V extends XYZ> implements Map<K,V> {

    private final Cache<K, V> map;
    private final OctBox<V> oct;

    public OctMap(InfiniPeer p, String id, Vec3D center, Vec3D radius, Vec3D resolution) {
        this.map = p.the(id);
        this.oct = new OctBox(center, radius, resolution);
    }


    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        V removed = map.put(key, value);
        if (removed!=null) {
            octRemove(removed);
        }
        oct.put(value);
        return removed;
    }

    private void octRemove(V v) {
        if (!oct.remove(v)) {
            throw new RuntimeException("Octree inconsistency detected on removal value=" + v);
        }
    }

    @Override
    public V remove(Object key) {
        V v = map.remove(key);
        if (v!=null) {
            octRemove(v);
        }
        return v;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
        oct.put(m.values());
    }

    @Override
    public void clear() {
        map.clear();
        oct.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }
}

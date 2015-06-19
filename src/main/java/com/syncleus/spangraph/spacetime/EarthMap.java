package com.syncleus.spangraph.spacetime;

import com.syncleus.spangraph.InfiniPeer;
import com.syncleus.toxi.geom.Vec3D;
import com.syncleus.toxi.geom.XYZ;

/**
 * PlanetMap preconfigured for Earth's known dimensions and irregular sphericity */
public class EarthMap<K extends XYZ, V> extends PlanetMap<K,V> {
    public EarthMap(InfiniPeer p, String id, Vec3D center, Vec3D radius, Vec3D resolution) {
        super(p, id, center, radius, resolution);
    }
}

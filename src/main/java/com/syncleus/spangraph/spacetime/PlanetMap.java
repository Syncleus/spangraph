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

import com.syncleus.spangraph.InfiniPeer;
import com.syncleus.toxi.geom.Vec3D;
import com.syncleus.toxi.geom.XYZ;

/**
 * OctMap with special handling for planetary coordinates and spheroid geometry.
 * 1st dimensions is Latitude, 2nd dimension is Longitude, 3rd dimension is time */
public class PlanetMap<K extends XYZ, V> extends OctMap<K,V> {

    public PlanetMap(InfiniPeer p, String id, Vec3D center, Vec3D radius, Vec3D resolution) {
        super(p, id, center, radius, resolution);
    }
}

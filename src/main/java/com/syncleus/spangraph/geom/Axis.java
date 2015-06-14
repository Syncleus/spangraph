package com.syncleus.spangraph.geom;

/**
 * Created by me on 6/13/15.
 */
public enum Axis {

    X(Vec3D.X_AXIS),
    Y(Vec3D.Y_AXIS),
    Z(Vec3D.Z_AXIS);

    private final ReadonlyVec3D vector;

    private Axis(ReadonlyVec3D v) {
        this.vector = v;
    }

    public ReadonlyVec3D getVector() {
        return vector;
    }
}

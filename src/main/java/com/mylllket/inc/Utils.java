package com.mylllket.inc;

public class Utils {
    private Utils() {
    }

    public static boolean coordinatesAreEqual(Entity a, Entity b) {
        return coordinatesAreEqual(a.getCoordinate(), b.getCoordinate());
    }

    public static boolean coordinatesAreEqual(Coordinate a, Coordinate b) {
        return a.getX() == b.getX() && a.getY() == b.getY();
    }

    public static boolean coordinatesAreNotEqual(Coordinate a, Coordinate b) {
        return !coordinatesAreEqual(a, b);
    }
}

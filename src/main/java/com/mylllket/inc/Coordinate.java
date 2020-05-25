package com.mylllket.inc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class Coordinate {
    private double x;
    private double y;

    public Coordinate(Coordinate coordinate) {
        this.x = coordinate.getX();
        this.y = coordinate.getY();
    }

    public void updatePosition(double dx, double dy) {
        x += dx;
        y += dy;
    }

    public void update(Coordinate coordinate) {
        this.x = coordinate.getX();
        this.y = coordinate.getY();
    }
}

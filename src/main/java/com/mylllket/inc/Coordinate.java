package com.mylllket.inc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Coordinate {
    private double x;
    private double y;

    public void updatePosition(double dx, double dy) {
        x += dx;
        y += dy;
    }
}

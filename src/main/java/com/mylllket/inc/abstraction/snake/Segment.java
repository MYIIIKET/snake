package com.mylllket.inc.abstraction.snake;

import com.mylllket.inc.Coordinate;
import com.mylllket.inc.Entity;
import com.mylllket.inc.Size;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Segment extends Entity {

    public Segment(Size size, Color color, Coordinate coordinate) {
        super(size, color, coordinate,
                graphics -> {
                    graphics.setColor(color);
                    graphics.fill(new Ellipse2D.Double(coordinate.getX(), coordinate.getY(), size.getWidth(), size.getHeight()));
                });
    }
}

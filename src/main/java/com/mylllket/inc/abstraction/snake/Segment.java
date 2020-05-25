package com.mylllket.inc.abstraction.snake;

import com.mylllket.inc.Coordinate;
import com.mylllket.inc.Direction;
import com.mylllket.inc.Entity;
import com.mylllket.inc.Size;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Segment extends Entity {
    private Direction direction;

    protected Segment(Size size, Color color, Coordinate coordinate, Direction direction) {
        super(size, color, coordinate,
                graphics -> {
                    graphics.setColor(color);
                    graphics.fill(new Ellipse2D.Double(coordinate.getX(), coordinate.getY(), size.getWidth(), size.getHeight()));
                });
        this.direction = direction;
    }

    void updatePosition(double delta) {
        switch (direction) {
            case UP:
                getCoordinate().updatePosition(0, -delta);
                break;
            case DOWN:
                getCoordinate().updatePosition(0, delta);
                break;
            case LEFT:
                getCoordinate().updatePosition(-delta, 0);
                break;
            case RIGHT:
                getCoordinate().updatePosition(delta, 0);
                break;
        }
    }

    void updateDirection(Direction direction) {
        this.direction = direction;
    }

    Direction getDirection() {
        return direction;
    }
}

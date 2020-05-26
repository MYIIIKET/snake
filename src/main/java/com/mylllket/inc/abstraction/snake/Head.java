package com.mylllket.inc.abstraction.snake;

import com.mylllket.inc.Coordinate;
import com.mylllket.inc.Direction;
import com.mylllket.inc.Size;

import java.awt.*;

public class Head extends Segment {
    private Direction direction;

    public Head(Coordinate coordinate) {
        super(new Size(10, 10), Color.GRAY, coordinate);
        this.direction = Direction.IDLE;
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

    protected Direction getDirection() {
        return direction;
    }
}

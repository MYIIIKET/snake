package com.mylllket.inc.abstraction.snake;

import com.mylllket.inc.Direction;
import com.mylllket.inc.interfaces.actions.Drawable;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.awt.*;

@ToString
@AllArgsConstructor
public abstract class Segment implements Drawable {
    private final SegmentCoordinate segmentCoordinate;
    private Direction direction;

    void updatePosition(Direction direction) {
        switch (direction) {
            case UP:
                segmentCoordinate.updatePosition(0, -1);
                break;
            case DOWN:
                segmentCoordinate.updatePosition(0, 1);
                break;
            case LEFT:
                segmentCoordinate.updatePosition(-1, 0);
                break;
            case RIGHT:
                segmentCoordinate.updatePosition(1, 0);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        this.direction = direction;
    }

    void updateDirection(Direction direction) {
        this.direction = direction;
    }

    Direction getDirection() {
        return direction;
    }

    boolean hasTheSameCoordinateAs(Segment segment) {
        return segment.segmentCoordinate.theSameAs(segmentCoordinate);
    }

    @Override
    public void draw(Graphics2D graphics) {
        segmentCoordinate.draw(graphics);
    }
}

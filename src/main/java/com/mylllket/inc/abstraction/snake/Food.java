package com.mylllket.inc.abstraction.snake;

import com.mylllket.inc.Direction;

public abstract class Food extends Segment {
    public Food(SegmentCoordinate segmentCoordinate) {
        super(segmentCoordinate, Direction.IDLE);
    }
}

package com.mylllket.inc.abstraction.snake;

import com.mylllket.inc.Direction;

public abstract class Head extends Segment {
    public Head(SegmentCoordinate segmentCoordinate, Direction direction) {
        super(segmentCoordinate, direction);
    }
}

package com.mylllket.inc.abstraction.snake;

import com.mylllket.inc.Coordinate;
import com.mylllket.inc.Direction;
import com.mylllket.inc.Size;

import java.awt.*;

public class Head extends Segment {
    public Head(Coordinate coordinate) {
        super(new Size(10, 10), Color.GREEN, coordinate, Direction.IDLE);
    }
}

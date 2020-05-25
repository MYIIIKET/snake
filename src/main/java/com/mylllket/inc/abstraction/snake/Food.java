package com.mylllket.inc.abstraction.snake;

import com.mylllket.inc.Coordinate;
import com.mylllket.inc.Direction;
import com.mylllket.inc.Size;

import java.awt.*;

public class Food extends Segment {
    public Food(Coordinate coordinate) {
        super(new Size(10, 10), Color.RED, coordinate, Direction.IDLE);
    }
}

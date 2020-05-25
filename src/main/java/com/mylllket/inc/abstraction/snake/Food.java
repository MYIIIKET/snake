package com.mylllket.inc.abstraction.snake;

import com.mylllket.inc.Coordinate;
import com.mylllket.inc.Size;

import java.awt.*;

public class Food extends Segment {
    private boolean processed = false;

    public Food(Coordinate coordinate) {
        super(new Size(10, 10), Color.RED, coordinate);
    }

    public void process() {
        processed = true;
    }

    public boolean isNotProcessed() {
        return !isProcessed();
    }

    public boolean isProcessed() {
        return processed;
    }
}

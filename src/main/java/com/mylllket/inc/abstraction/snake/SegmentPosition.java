package com.mylllket.inc.abstraction.snake;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public abstract class SegmentPosition {
    private int value;

    public void updatePosition(int delta) {
        value += delta;
    }

    boolean theSameAs(SegmentPosition position) {
        return position.value == this.value;
    }

    public int getValue() {
        return value;
    }
}

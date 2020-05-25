package com.mylllket.inc;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Direction {
    UP,
    LEFT,
    RIGHT,
    DOWN,
    IDLE;

    static {
        UP.opposite = DOWN;
        LEFT.opposite = RIGHT;
        DOWN.opposite = UP;
        RIGHT.opposite = LEFT;
        IDLE.opposite = IDLE;
    }

    public Direction getOpposite() {
        return opposite;
    }

    private Direction opposite;
}

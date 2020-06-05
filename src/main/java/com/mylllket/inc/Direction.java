package com.mylllket.inc;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Direction {
    UP("↑"),
    LEFT("←"),
    RIGHT("→"),
    DOWN("↓"),
    IDLE("•");

    static {
        UP.opposite = DOWN;
        LEFT.opposite = RIGHT;
        DOWN.opposite = UP;
        RIGHT.opposite = LEFT;
        IDLE.opposite = IDLE;
    }

    private final String direction;

    public Direction getOpposite() {
        return opposite;
    }

    private Direction opposite;

    public String getDirection() {
        return direction;
    }

    public static Direction random() {
        List<Direction> collect = Arrays.stream(values()).filter(value -> value != IDLE).collect(Collectors.toList());
        Collections.shuffle(collect);
        return collect.get(0);
    }
}

package com.mylllket.inc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Coordinate<T extends Number> {
    private T x;
    private T y;
}

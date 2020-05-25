package com.mylllket.inc;

import com.mylllket.inc.interfaces.actions.Drawable;

import java.awt.*;

public abstract class Entity implements Drawable {
    private final Size size;
    private final Color color;
    private final Coordinate coordinate;
    private final DrawingMethod drawingMethod;

    protected Entity(Size size, Color color, Coordinate coordinate, DrawingMethod drawingMethod) {
        this.size = size;
        this.color = color;
        this.coordinate = coordinate;
        this.drawingMethod = drawingMethod;
    }

    protected Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public void draw(Graphics2D graphics) {
        drawingMethod.draw(graphics);
    }
}

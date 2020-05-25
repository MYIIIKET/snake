package com.mylllket.inc;

import com.mylllket.inc.interfaces.actions.Drawable;

import java.awt.*;
import java.util.UUID;

public abstract class Entity implements Drawable {
    private final UUID id = UUID.randomUUID();
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

    @Override
    public UUID getId() {
        return id;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void updateCoordinate(Coordinate coordinate) {
        this.coordinate.update(coordinate);
    }

    @Override
    public void draw(Graphics2D graphics) {
        drawingMethod.draw(graphics);
    }
}

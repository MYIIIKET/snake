package com.mylllket.inc;

import com.mylllket.inc.interfaces.actions.Drawable;

import java.awt.*;
import java.awt.geom.Ellipse2D;


public abstract class Entity implements Drawable {
    private final Size size;
    private final Color color;
    private final Coordinate coordinate;
    private final DrawingMethod drawingMethod;

    protected Entity() {
        this.size = new Size(5, 5);
        this.color = Color.BLACK;
        this.coordinate = new Coordinate(0, 0);
        this.drawingMethod = graphics -> {
            graphics.setColor(color);
            graphics.fill(
                    new Ellipse2D.Double(
                            (Double) coordinate.getX(),
                            (Double) coordinate.getY(),
                            size.getWidth(),
                            size.getHeight()));
        };
    }

    @Override
    public void draw(Graphics2D graphics) {
        drawingMethod.draw(graphics);
    }
}

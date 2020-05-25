package com.mylllket.inc;

import com.mylllket.inc.interfaces.actions.Drawable;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.UUID;


public class Border implements Drawable {
    private final UUID id = UUID.randomUUID();
    private final Size size;
    private final Coordinate coordinate;

    public Border(Size size, Coordinate coordinate) {
        this.size = size;
        this.coordinate = coordinate;
    }


    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(2));
        graphics.draw(new Rectangle2D.Double(coordinate.getX(), coordinate.getY(), size.getWidth(), size.getHeight()));
    }

    public boolean outOfBorder(Coordinate coordinate) {
        return size.getWidth() < coordinate.getX()
                || size.getHeight() < coordinate.getY()
                || this.coordinate.getX() > coordinate.getX()
                || this.coordinate.getY() > coordinate.getY();

    }
}

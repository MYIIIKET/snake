package com.mylllket.inc;

import com.mylllket.inc.interfaces.actions.Drawable;

import java.awt.*;
import java.util.UUID;

public class Cell implements Drawable {
    private final UUID id = UUID.randomUUID();
    private final Coordinate coordinate;
    private boolean isBusy;

    public Cell(Coordinate coordinate, boolean isBusy) {
        this.coordinate = coordinate;
        this.isBusy = isBusy;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void updateStatus(boolean isBusy) {
        this.isBusy = isBusy;
    }

    protected boolean isBusy() {
        return isBusy;
    }

    protected boolean isNotBusy() {
        return !isBusy();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void draw(Graphics2D graphics) {
        float x = (float) coordinate.getX();
        float y = (float) coordinate.getY() + 10;
        if (isBusy) {
            graphics.setColor(Color.RED);
            graphics.drawString("1", x, y);
        } else {
            graphics.setColor(Color.BLACK);
            graphics.drawString("0", x, y);
        }
    }
}

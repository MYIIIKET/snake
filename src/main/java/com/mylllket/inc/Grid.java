package com.mylllket.inc;

import com.mylllket.inc.interfaces.actions.Drawable;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.UUID;
import java.util.stream.IntStream;

public class Grid implements Drawable {
    private final UUID id = UUID.randomUUID();
    private final Border border;
    private final int step = 10;

    public Grid(Border border) {
        this.border = border;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void draw(Graphics2D graphics) {

        final int widthLinesNumber = (int) (border.getSize().getWidth() / step);
        final int heightLinesNumber = (int) (border.getSize().getHeight() / step);
        IntStream.rangeClosed(1, widthLinesNumber)
                .forEach(lineNumber -> {
                    graphics.setColor(Color.BLACK);
                    graphics.setStroke(new BasicStroke(1));
                    graphics.draw(new Line2D.Double(
                            border.getCoordinate().getX() + lineNumber * step,
                            border.getCoordinate().getY(),
                            border.getCoordinate().getX() + lineNumber * step,
                            border.getSize().getWidth() + step));
                });
        IntStream.rangeClosed(1, heightLinesNumber)
                .forEach(lineNumber -> {
                    graphics.setColor(Color.BLACK);
                    graphics.setStroke(new BasicStroke(1));
                    graphics.draw(new Line2D.Double(
                            border.getCoordinate().getX(),
                            border.getCoordinate().getY() + lineNumber * step,
                            border.getSize().getHeight() + step,
                            border.getCoordinate().getY() + lineNumber * step));
                });
    }
}

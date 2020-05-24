package com.mylllket.inc.abstraction.snake;

import com.mylllket.inc.interfaces.actions.Drawable;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.awt.*;
import java.awt.geom.Ellipse2D;

@ToString
@RequiredArgsConstructor
public abstract class SegmentCoordinate implements Drawable {
    private final SegmentPosition x;
    private final SegmentPosition y;

    void updatePosition(int dx, int dy) {
        x.updatePosition(dx);
        y.updatePosition(dy);
    }

    boolean theSameAs(SegmentCoordinate segmentCoordinate) {
        return x.theSameAs(segmentCoordinate.x) && y.theSameAs(segmentCoordinate.y);
    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(Color.GREEN);
        graphics.fill(new Ellipse2D.Double(x.getValue(), y.getValue(), 5, 5));
    }
}

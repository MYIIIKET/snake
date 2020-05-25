package com.mylllket.inc.interfaces.actions;

import java.awt.*;
import java.util.UUID;

public interface Drawable {
    UUID getId();

    void draw(Graphics2D graphics);
}

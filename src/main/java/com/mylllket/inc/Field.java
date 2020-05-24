package com.mylllket.inc;

import com.mylllket.inc.interfaces.actions.Drawable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Field extends JPanel {
    private final List<Drawable> drawables = new ArrayList<>();

    public Field() {
        setBackground(Color.WHITE);
        setVisible(true);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        drawables.forEach(drawable -> drawable.draw((Graphics2D) graphics));
    }

    public void add(Drawable drawable) {
        add(Collections.singletonList(drawable));
    }

    public void add(List<Drawable> drawables) {
        this.drawables.addAll(drawables);
    }
}

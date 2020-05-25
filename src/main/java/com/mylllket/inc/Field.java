package com.mylllket.inc;

import com.mylllket.inc.interfaces.actions.Drawable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Field extends JPanel {
    private final Map<UUID, Drawable> drawables = new HashMap<>();

    public Field() {
        setBackground(Color.WHITE);
        setVisible(true);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        drawables.values().forEach(drawable -> drawable.draw((Graphics2D) graphics));
    }

    public void add(Drawable drawable) {
        add(Collections.singletonMap(drawable.getId(), drawable));
    }

    public void remove(UUID drawableId) {
        drawables.remove(drawableId);
    }

    public void add(List<Drawable> drawables) {
        this.drawables.putAll(drawables.stream().collect(Collectors.toMap(Drawable::getId, Function.identity())));
    }

    public void add(Map<UUID, Drawable> drawables) {
        this.drawables.putAll(drawables);
    }
}

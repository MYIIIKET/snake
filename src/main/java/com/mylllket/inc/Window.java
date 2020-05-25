package com.mylllket.inc;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    public Window() {
        init("WINDOW", 640, 480);
    }

    public Window(int width, int height) {
        init("WINDOW", width, height);
    }

    public Window(String title) {
        init(title, 640, 480);
    }

    private void init(String window, int width, int height) {
        setTitle(window);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(width, height));
        pack();
        setVisible(true);
    }
}

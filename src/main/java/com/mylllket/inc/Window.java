package com.mylllket.inc;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    public Window() {
        init("WINDOW");
    }

    public Window(String title) {
        init(title);
    }

    private void init(String window) {
        setTitle(window);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(640, 480));
        pack();
        setVisible(true);
    }
}

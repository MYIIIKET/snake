package com.mylllket.inc;

import com.mylllket.inc.abstraction.snake.Head;
import com.mylllket.inc.abstraction.snake.SegmentCoordinate;
import com.mylllket.inc.abstraction.snake.SegmentPosition;
import com.mylllket.inc.abstraction.snake.Snake;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main {
    public static void main(String[] args) {
        Snake snake = new Snake(new Head(new SegmentCoordinate(new SegmentPosition(1) {
        }, new SegmentPosition(1) {
        }) {
        }, Direction.LEFT) {
        }) {
        };
        Window window = new Window();
        Field field = new Field();
        window.add(field);
        window.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        snake.moveUp();
                        break;
                    case KeyEvent.VK_LEFT:
                        snake.moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        snake.moveRight();
                        break;
                    case KeyEvent.VK_DOWN:
                        snake.moveDown();
                        break;
                }
                field.repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        field.add(snake);
    }
}

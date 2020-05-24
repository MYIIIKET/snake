package com.mylllket.inc;

import com.mylllket.inc.abstraction.snake.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Snake snake = new Snake(new Head(new SegmentCoordinate(new SegmentPosition(1) {
        }, new SegmentPosition(1) {
        }) {
        }, Direction.RIGHT) {
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
                    case KeyEvent.VK_SPACE:
                        snake.move();
                        break;
                }
                field.repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        field.add(snake);
        Food food = new Food(new SegmentCoordinate(new SegmentPosition(10) {
        }, new SegmentPosition(1) {
        }) {
        }) {
        };
        field.add(food);
        Runnable runnable = () -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                snake.move();
                snake.consume(food);
                snake.growTail();
                field.repaint();
            }
        };
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }
}

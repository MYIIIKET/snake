package com.mylllket.inc;

import com.mylllket.inc.abstraction.snake.Food;
import com.mylllket.inc.abstraction.snake.Head;
import com.mylllket.inc.abstraction.snake.Snake;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Head head = new Head(new Coordinate(0, 10));
        Snake snake = new Snake(head);
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
        Random random = new Random();
        Food food = new Food(new Coordinate(10 * random.nextInt(63), 10 * random.nextInt(47)));
        field.add(food);
        Runnable runnable = () -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                snake.move();
                if (snake.consume(food)) {
                    food.updateCoordinate(new Coordinate(10 * random.nextInt(63), 10 * random.nextInt(47)));
                }
                snake.growTail();
                field.repaint();
            }
        };
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }
}

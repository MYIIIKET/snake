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
        Window window = new Window(500, 500);
        Field field = new Field();
        window.add(field);

        Snake snake = new Snake(new Head(new Coordinate(200, 200)));
        field.add(snake);

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

        Border border = new Border(new Size(400, 400), new Coordinate(10, 10));
        field.add(border);

//        Grid grid = new Grid(border);
//        field.add(grid);

        Food food = new Food(getNextFoodCoordinate());
        refreshFood(snake, food);
        field.add(food);
        Runnable runnable = () -> {
            do {
                field.repaint();
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                snake.move();
                if (snake.consume(food)) {
                    refreshFood(snake, food);
                }
                snake.growTail();
            } while (!gameIsOver(snake, border));
        };
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }

    private static void refreshFood(Snake snake, Food food) {
        while (cannotCreate(food, snake)) {
            food.updateCoordinate(getNextFoodCoordinate());
        }
    }

    private static Coordinate getNextFoodCoordinate() {
        Random random = new Random();
        return new Coordinate(10 + 10 * random.nextInt(40), 10 + 10 * random.nextInt(40));
    }

    private static boolean gameIsOver(Snake snake, Border border) {
        return snake.isNotValid() || border.outOfBorder(snake.getHeadCoordinate());
    }

    private static boolean cannotCreate(Food food, Snake snake) {
        return snake.canConsume(food);
    }
}

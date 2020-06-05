package com.mylllket.inc;

import com.mylllket.inc.abstraction.snake.Food;
import com.mylllket.inc.abstraction.snake.Head;
import com.mylllket.inc.abstraction.snake.Snake;
import com.mylllket.inc.utils.Utils;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Window window = new Window(500, 500);
        Field field = new Field();
        window.add(field);

//        startFieldRenderer(field);

        Head head = new Head(new Coordinate(100, 100));
        Snake snake = new Snake(head);
        field.add(snake);

        Border border = new Border(new Size(50, 50), new Coordinate(100, 100));
        field.add(border);

        Grid grid = new Grid(border);
        field.add(grid);

        Cell[][] cells = Utils.toArray(border, snake);

        Food food = new Food(Utils.getNextFoodCoordinate(cells));
        Utils.refreshFood(snake, cells, food);
        field.add(food);

        Utils.refresh(cells, snake, Optional.of(food));
        AStar aStar = new AStar(cells, new Coordinate(snake.getHeadCoordinate()), new Coordinate(food.getCoordinate()), border.getCoordinate().getX(), border.getCoordinate().getY(), food);
        field.add(aStar);
        aStar.buildPath();


        Runnable runnable = () -> {
            int score = 0;
            do {
                field.repaint();
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                snake.move();
                if (snake.consume(food)) {
                    score++;
                    try {
                        Utils.refreshFood(snake, cells, food);
                    } catch (UnsupportedOperationException e) {
                        System.out.println("You win");
                        break;
                    }
                }
                Utils.refresh(cells, snake, Optional.of(food));
                snake.growTail();
                aStar.update(cells, new Coordinate(snake.getHeadCoordinate().getX(), snake.getHeadCoordinate().getY()), new Coordinate(food.getCoordinate()), food);
                aStar.buildPath();
                System.out.println(aStar.havePath());
            } while (!Utils.gameIsOver(snake, border));
            System.out.println("Game is over");
            System.out.println(score);
        };
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();

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
                        break;
                }
//                snake.move();
//                refresh(cells, snake, Optional.of(food));
//                aStar.update(cells, new Coordinate(snake.getHeadCoordinate()), new Coordinate(snake.getTailCoordinate()), food);
//                aStar.buildPath();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private static void startFieldRenderer(Field field) {
        Runnable renderer = () -> {
            while (true) {
                field.repaint();
            }
        };
        Thread rendererThread = new Thread(renderer);
        rendererThread.setDaemon(true);
        rendererThread.start();
    }
}

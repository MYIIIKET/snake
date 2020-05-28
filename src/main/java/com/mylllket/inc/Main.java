package com.mylllket.inc;

import com.mylllket.inc.abstraction.snake.Food;
import com.mylllket.inc.abstraction.snake.Head;
import com.mylllket.inc.abstraction.snake.Snake;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        Window window = new Window(500, 500);
        Field field = new Field();
        window.add(field);

        startFieldRenderer(field);

        Head head = new Head(new Coordinate(70, 70));
        Snake snake = new Snake(head);
        field.add(snake);

        Border border = new Border(new Size(300, 300), new Coordinate(10, 10));
        field.add(border);

        Grid grid = new Grid(border);
        field.add(grid);

        Cell[][] cells = toArray(border, snake);

        Food food = new Food(getNextFoodCoordinate(cells));
        refreshFood(snake, cells, food);
        field.add(food);

        refresh(cells, snake, Optional.of(food));
        AStar aStar = new AStar(cells, new Coordinate(head.getCoordinate()), new Coordinate(food.getCoordinate()));
        field.add(aStar);
        aStar.buildPath();


        Runnable runnable = () -> {
            int score = 0;
            do {
                field.repaint();
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                snake.move();
                if (snake.consume(food)) {
                    score++;
                    try {
                        refreshFood(snake, cells, food);
                    } catch (UnsupportedOperationException e) {
                        System.out.println("You win");
                        break;
                    }
                }
                refresh(cells, snake, Optional.of(food));
                snake.growTail();
            } while (!gameIsOver(snake, border));
            System.out.println("Game is over");
            System.out.println(score);
        };
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
//        thread.start();

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
                snake.move();
                refresh(cells, snake, Optional.of(food));
                aStar.update(cells, new Coordinate(head.getCoordinate()), new Coordinate(food.getCoordinate()));
                aStar.buildPath();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private static void startFieldRenderer(Field field) {
        Runnable renderer = () -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                field.repaint();
            }
        };
        Thread rendererThread = new Thread(renderer);
        rendererThread.setDaemon(true);
        rendererThread.start();
    }

    private static void refreshFood(Snake snake, Cell[][] cells, Food food) {
        refresh(cells, snake, Optional.of(food));
        refreshFood(cells, food);
        refresh(cells, snake, Optional.of(food));
    }

    private static Cell[][] toArray(Border border, Snake snake) {
        int step = 10;
        int cols = (int) (border.getSize().getWidth() / step);
        int rows = (int) (border.getSize().getHeight() / step);
        Cell[][] cells = new Cell[cols][rows];
        IntStream.range(0, cols)
                .forEach(col -> IntStream.range(0, rows)
                        .forEach(row -> {
                            Coordinate coordinate = new Coordinate((col + 1) * step, (row + 1) * step);
                            boolean isBusy = snake.clashesWith(coordinate);
                            cells[col][row] = new Cell(coordinate, isBusy);
                        }));
        return cells;
    }

    private static void refresh(Cell[][] cells, Snake snake, Optional<Food> food) {
        Arrays.stream(cells)
                .forEach(c1 -> Arrays.stream(c1)
                        .forEach(c2 -> {
                            Coordinate coordinate = c2.getCoordinate();
                            boolean isBusy = snake.clashesWith(coordinate)
                                    || food.map(f -> f.clashesWith(coordinate)).orElse(false);
                            c2.updateStatus(isBusy);
                        }));
    }

    private static void refreshFood(Cell[][] cells, Food food) {
        food.updateCoordinate(getNextFoodCoordinate(cells));
    }

    private static Coordinate getNextFoodCoordinate(Cell[][] cells) {
        Random random = new Random();
        Cell[][] freeCells = Arrays.stream(cells)
                .map(r -> Arrays.stream(r)
                        .filter(e -> !e.isBusy())
                        .toArray(Cell[]::new))
                .filter(r -> r.length > 0)
                .toArray(Cell[][]::new);
        int cols = freeCells.length;
        if (cols > 0) {
            int col = random.nextInt(cols);
            int rows = freeCells[col].length;
            if (rows > 0) {
                int row = random.nextInt(rows);
                return new Coordinate(freeCells[col][row].getCoordinate());
            }
        }
        throw new UnsupportedOperationException();
    }

    private static boolean gameIsOver(Snake snake, Border border) {
        return snake.isNotValid() || border.outOfBorder(snake.getHeadCoordinate());
    }
}

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

        Food food = new Food(getNextFoodCoordinate(toArray(border, snake, Optional.empty())));
        refreshFood(border, snake, food);
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
                    refreshFood(border, snake, food);
                }
                snake.growTail();
            } while (!gameIsOver(snake, border));
        };
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }

    private static Cell[][] toArray(Border border, Snake snake, Optional<Food> food) {
        int step = 10;
        int cols = (int) (border.getSize().getWidth() / step);
        int rows = (int) (border.getSize().getHeight() / step);
        Cell[][] cells = new Cell[cols][rows];
        IntStream.range(0, cols)
                .forEach(col -> IntStream.range(0, rows)
                        .forEach(row -> {
                            Coordinate coordinate = new Coordinate((col + 1) * step, (row + 1) * step);
                            boolean isBusy = snake.clashesWith(coordinate)
                                    || food.map(f -> f.clashesWith(coordinate)).orElse(false);
                            cells[col][row] = new Cell(coordinate, isBusy);
                        }));
        return cells;
    }

    private static class Cell {
        private final Coordinate coordinate;
        private boolean isBusy;

        public Cell(Coordinate coordinate, boolean isBusy) {
            this.coordinate = coordinate;
            this.isBusy = isBusy;
        }

        public Cell(Coordinate coordinate) {
            this.coordinate = coordinate;
            this.isBusy = false;
        }

        public Coordinate getCoordinate() {
            return coordinate;
        }
    }

    private static void refreshFood(Border border, Snake snake, Food food) {
        Cell[][] cells = toArray(border, snake, Optional.of(food));
        food.updateCoordinate(getNextFoodCoordinate(cells));
    }

    private static Coordinate getNextFoodCoordinate(Cell[][] cells) {
        Random random = new Random();
        Cell[][] freeCells = Arrays.stream(cells)
                .map(r -> Arrays.stream(r)
                        .filter(e -> !e.isBusy)
                        .toArray(Cell[]::new))
                .toArray(Cell[][]::new);
        int cols = freeCells.length;
        if (cols > 0) {
            int rows = freeCells[0].length;
            if (rows > 0) {
                int col = random.nextInt(cols);
                int row = random.nextInt(rows);
                return cells[col][row].getCoordinate();
            }
        }
        throw new UnsupportedOperationException();
    }

    private static boolean gameIsOver(Snake snake, Border border) {
        return snake.isNotValid() || border.outOfBorder(snake.getHeadCoordinate());
    }

    private static boolean cannotCreate(Food food, Snake snake) {
        return snake.canConsume(food);
    }
}

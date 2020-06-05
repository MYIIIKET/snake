package com.mylllket.inc.utils;

import com.mylllket.inc.*;
import com.mylllket.inc.abstraction.snake.Food;
import com.mylllket.inc.abstraction.snake.Snake;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

public class Utils {
    private Utils() {
    }

    public static boolean coordinatesAreEqual(Entity a, Entity b) {
        return coordinatesAreEqual(a.getCoordinate(), b.getCoordinate());
    }

    public static boolean coordinatesAreEqual(Coordinate a, Coordinate b) {
        return a.getX() == b.getX() && a.getY() == b.getY();
    }

    public static boolean coordinatesAreNotEqual(Coordinate a, Coordinate b) {
        return !coordinatesAreEqual(a, b);
    }

    public static Cell[][] toArray(Border border, Snake snake) {
        int step = 10;
        int cols = (int) (border.getSize().getWidth() / step);
        int rows = (int) (border.getSize().getHeight() / step);
        Cell[][] cells = new Cell[cols][rows];
        IntStream.range(0, cols)
                .forEach(col -> IntStream.range(0, rows)
                        .forEach(row -> {
                            Coordinate coordinate = new Coordinate((col + 1) * step + border.getCoordinate().getX() - step, (row + 1) * step + border.getCoordinate().getY() - step);
                            boolean isBusy = snake.clashesWith(coordinate);
                            cells[col][row] = new Cell(coordinate, isBusy);
                        }));
        return cells;
    }

    public static Coordinate getNextFoodCoordinate(Cell[][] cells) {
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

    public static void refreshFood(Snake snake, Cell[][] cells, Food food) {
        refresh(cells, snake, Optional.of(food));
        refreshFood(cells, food);
        refresh(cells, snake, Optional.of(food));
    }

    public static void refresh(Cell[][] cells, Snake snake, Optional<Food> food) {
        Arrays.stream(cells)
                .forEach(c1 -> Arrays.stream(c1)
                        .forEach(c2 -> {
                            Coordinate coordinate = c2.getCoordinate();
                            boolean isBusy = snake.clashesWith(coordinate)
                                    || food.map(f -> f.clashesWith(coordinate)).orElse(false);
                            c2.updateStatus(isBusy);
                        }));
    }

    public static boolean gameIsOver(Snake snake, Border border) {
        return snake.isNotValid() || border.outOfBorder(snake.getHeadCoordinate());
    }

    private static void refreshFood(Cell[][] cells, Food food) {
        food.updateCoordinate(getNextFoodCoordinate(cells));
    }

    public static double[] collectInput(Snake snake, Cell[][] cells, Food food, AStar aStar) {
        double[] input = new double[5];
        Direction direction = snake.getDirection();
        switch (direction) {
            case UP:
                input[0] = 0;
                input[1] = 0;
                aStar.update(cells,
                        new Coordinate(snake.getHeadCoordinate().getX(), snake.getHeadCoordinate().getY() - 10),
                        new Coordinate(snake.getTailCoordinate()), food);
                aStar.buildPath();
                input[2] = aStar.havePath() ? 1 : 0;

                aStar.update(cells,
                        new Coordinate(snake.getHeadCoordinate().getX() - 10, snake.getHeadCoordinate().getY()),
                        new Coordinate(snake.getTailCoordinate()), food);
                aStar.buildPath();
                input[3] = aStar.havePath() ? 1 : 0;

                aStar.update(cells,
                        new Coordinate(snake.getHeadCoordinate().getX() + 10, snake.getHeadCoordinate().getY()),
                        new Coordinate(snake.getTailCoordinate()), food);
                aStar.buildPath();
                input[4] = aStar.havePath() ? 1 : 0;
                break;
            case DOWN:
                input[0] = 1;
                input[1] = 0;
                aStar.update(cells,
                        new Coordinate(snake.getHeadCoordinate().getX(), snake.getHeadCoordinate().getY() + 10),
                        new Coordinate(snake.getTailCoordinate()), food);
                aStar.buildPath();
                input[2] = aStar.havePath() ? 1 : 0;

                aStar.update(cells,
                        new Coordinate(snake.getHeadCoordinate().getX() - 10, snake.getHeadCoordinate().getY()),
                        new Coordinate(snake.getTailCoordinate()), food);
                aStar.buildPath();
                input[3] = aStar.havePath() ? 1 : 0;

                aStar.update(cells,
                        new Coordinate(snake.getHeadCoordinate().getX() + 10, snake.getHeadCoordinate().getY()),
                        new Coordinate(snake.getTailCoordinate()), food);
                aStar.buildPath();
                input[4] = aStar.havePath() ? 1 : 0;
                break;
            case LEFT:
                input[0] = 0;
                input[1] = 1;

                aStar.update(cells,
                        new Coordinate(snake.getHeadCoordinate().getX() - 10, snake.getHeadCoordinate().getY()),
                        new Coordinate(snake.getTailCoordinate()), food);
                aStar.buildPath();
                input[2] = aStar.havePath() ? 1 : 0;

                aStar.update(cells,
                        new Coordinate(snake.getHeadCoordinate().getX(), snake.getHeadCoordinate().getY() - 10),
                        new Coordinate(snake.getTailCoordinate()), food);
                aStar.buildPath();
                input[3] = aStar.havePath() ? 1 : 0;

                aStar.update(cells,
                        new Coordinate(snake.getHeadCoordinate().getX(), snake.getHeadCoordinate().getY() + 10),
                        new Coordinate(snake.getTailCoordinate()), food);
                aStar.buildPath();
                input[4] = aStar.havePath() ? 1 : 0;
                break;
            case RIGHT:
                input[0] = 1;
                input[1] = 1;

                aStar.update(cells,
                        new Coordinate(snake.getHeadCoordinate().getX() + 10, snake.getHeadCoordinate().getY()),
                        new Coordinate(snake.getTailCoordinate()), food);
                aStar.buildPath();
                input[2] = aStar.havePath() ? 1 : 0;

                aStar.update(cells,
                        new Coordinate(snake.getHeadCoordinate().getX(), snake.getHeadCoordinate().getY() - 10),
                        new Coordinate(snake.getTailCoordinate()), food);
                aStar.buildPath();
                input[3] = aStar.havePath() ? 1 : 0;

                aStar.update(cells,
                        new Coordinate(snake.getHeadCoordinate().getX(), snake.getHeadCoordinate().getY() + 10),
                        new Coordinate(snake.getTailCoordinate()), food);
                aStar.buildPath();
                input[4] = aStar.havePath() ? 1 : 0;
                break;
        }
        return input;
    }
}

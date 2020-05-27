package com.mylllket.inc;

import com.mylllket.inc.abstraction.snake.Food;
import com.mylllket.inc.abstraction.snake.Head;
import com.mylllket.inc.abstraction.snake.Snake;
import com.mylllket.inc.interfaces.actions.Drawable;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        Window window = new Window(500, 500);
        Field field = new Field();
        window.add(field);

        Snake snake = new Snake(new Head(new Coordinate(70, 70)));
        field.add(snake);

        Border border = new Border(new Size(300, 300), new Coordinate(10, 10));
        field.add(border);

//        Grid grid = new Grid(border);
//        field.add(grid);

        Cell[][] cells = toArray(border, snake);
        Coordinate start = new Coordinate(70, 70);
        Coordinate end = new Coordinate(50, 100);
        AStar aStar = new AStar(cells, start, end);
        field.add(aStar);
//        field.add(aStar);
//        field.add(Arrays.stream(cells).flatMap(Arrays::stream).collect(Collectors.toList()));
        Runnable runnable2 = () -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                field.repaint();
            }
        };
        Thread thread2 = new Thread(runnable2);
        thread2.setDaemon(true);
        thread2.start();
        aStar.buildPath();

        Food food = new Food(getNextFoodCoordinate(cells));
        refreshFood(snake, cells, food);
//        field.add(food);
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
//                        snake.move();
//                        aStar.buildPath(null, null);
                        break;
                }
                field.repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
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

    private static class AStar implements Drawable {
        private final UUID id = UUID.randomUUID();
        private final AStarCell[][] cells;
        private final LinkedList<Coordinate> path = new LinkedList<>();
        private final Coordinate start;
        private final Coordinate end;

        private AStar(Cell[][] cells, Coordinate start, Coordinate end) {
            this.cells = Arrays.stream(cells)
                    .map(c1 -> Arrays.stream(c1)
                            .map(cell -> {
                                Coordinate coordinate = cell.getCoordinate();
                                double heuristicDistance = Math.abs(coordinate.getX() - end.getX()) + Math.abs(coordinate.getY() - end.getY());
                                if (Utils.coordinatesAreEqual(coordinate, start)) {
                                    return new AStarCell(cell, heuristicDistance, true);
                                }
                                return new AStarCell(cell, heuristicDistance);
                            })
                            .toArray(AStarCell[]::new))
                    .toArray(AStarCell[][]::new);
            this.start = new Coordinate(start);
            this.end = new Coordinate(end);
        }


        public void buildPath() {
            Runnable runnable = () -> {
                path.clear();
                while (isMoreToVisit() && !getCell(end).visited) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    AStarCell[][] aStarCells = Arrays.stream(cells)
                            .map(c1 -> Arrays.stream(c1)
                                    .filter(c2 -> c2.visited)
                                    .toArray(AStarCell[]::new))
                            .toArray(AStarCell[][]::new);
                    Arrays.stream(aStarCells)
                            .forEach(c1 -> Arrays.stream(c1)
                                    .forEach(c2 -> {
                                        int i = (int) (c2.getCoordinate().getX() / 10) - 1;
                                        int j = (int) (c2.getCoordinate().getY() / 10) - 1;
                                        AStarCell prev = cells[i][j];
                                        visit(prev, i - 1, j);
                                        visit(prev, i + 1, j);
                                        visit(prev, i, j - 1);
                                        visit(prev, i, j + 1);
                                    }));
                }
                Coordinate e = new Coordinate(end);
                path.add(e);


                Coordinate last = new Coordinate(path.getLast());
                while (!Utils.coordinatesAreEqual(last, start)) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(300);
                    } catch (InterruptedException t) {
                        t.printStackTrace();
                    }
                    int i = (int) (last.getX() / 10) - 1;
                    int j = (int) (last.getY() / 10) - 1;
                    Optional<AStarCell> upNeighbor = getNeighbor(i, j - 1);
                    Optional<AStarCell> downNeighbor = getNeighbor(i, j + 1);
                    Optional<AStarCell> leftNeighbor = getNeighbor(i - 1, j);
                    Optional<AStarCell> rightNeighbor = getNeighbor(i + 1, j);
                    Optional<AStarCell> first = Stream.of(upNeighbor, downNeighbor, leftNeighbor, rightNeighbor)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .filter(r -> Utils.coordinatesAreEqual(r.getCoordinate(), start))
                            .findFirst();
                    if (first.isPresent()) {
                        path.add(new Coordinate(first.get().getCoordinate()));
                        break;
                    }
                    Stream.of(upNeighbor, downNeighbor, leftNeighbor, rightNeighbor)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .filter(r -> r.isNotBusy() || Utils.coordinatesAreEqual(r.getCoordinate(), start))
                            .filter(AStarCell::isVisited)
                            .filter(r -> path.stream().noneMatch(e1 -> Utils.coordinatesAreEqual(e1, r.getCoordinate())))
                            .min(Comparator.comparingDouble(AStarCell::getHeuristicDistance))
                            .map(Cell::getCoordinate)
                            .ifPresent(coordinate -> path.add(new Coordinate(coordinate)));
                    last = new Coordinate(path.getLast());
                }
            };
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.start();
        }

        private Optional<AStarCell> getNeighbor(int i, int j) {
            try {
                return Optional.of(cells[i][j]);
            } catch (Exception ignored) {
                return Optional.empty();
            }
        }

        private void visit(AStarCell prev, int i, int j) {
            try {
                AStarCell cell = cells[i][j];
                if (!cell.isBusy()) {
                    cell.visit(prev);
                }
            } catch (Exception ignored) {
            }
        }

        public boolean isMoreToVisit() {
            return Arrays.stream(cells)
                    .anyMatch(c1 -> Arrays.stream(c1)
                            .filter(c2 -> c2.visited)
                            .anyMatch(this::moreToVisit));
        }

        private boolean moreToVisit(AStarCell cell) {
            int i = (int) (cell.getCoordinate().getX() / 10) - 1;
            int j = (int) (cell.getCoordinate().getY() / 10) - 1;
            boolean downIsNotVisited = validateBoundary(i, j + 1);
            boolean leftIsNotVisited = validateBoundary(i - 1, j);
            boolean rightIsNotVisited = validateBoundary(i + 1, j);
            boolean upIsNotVisited = validateBoundary(i, j - 1);
            return leftIsNotVisited || rightIsNotVisited || upIsNotVisited || downIsNotVisited;
        }

        private boolean validateBoundary(int i, int j) {
            boolean isNotVisited;
            try {
                isNotVisited = !cells[i][j].visited || cells[i][j].isBusy();
            } catch (Exception ignored) {
                isNotVisited = false;
            }
            return isNotVisited;
        }

        private AStarCell getCell(Coordinate coordinate) {
            int i = (int) (coordinate.getX() / 10);
            int j = (int) (coordinate.getY() / 10);
            return cells[i][j];
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public void draw(Graphics2D graphics) {
            Arrays.stream(cells)
                    .forEach(c1 -> Arrays.stream(c1)
                            .forEach(c2 -> {
                                if (Utils.coordinatesAreEqual(start, c2.getCoordinate())) {
                                    graphics.setColor(Color.RED);
                                    graphics.draw(new Ellipse2D.Double(c2.getCoordinate().getX() + 3, c2.getCoordinate().getY() + 3, 2, 2));
                                } else if (Utils.coordinatesAreEqual(end, c2.getCoordinate())) {
                                    graphics.setColor(Color.GREEN);
                                    graphics.draw(new Ellipse2D.Double(c2.getCoordinate().getX() + 3, c2.getCoordinate().getY() + 3, 2, 2));
                                } else if (c2.visited) {
                                    graphics.setColor(Color.BLUE);
                                    graphics.draw(new Ellipse2D.Double(c2.getCoordinate().getX() + 3, c2.getCoordinate().getY() + 3, 2, 2));
                                }
                            }));
            path.forEach(coordinate -> {
                graphics.setColor(Color.PINK);
                graphics.draw(new Ellipse2D.Double(coordinate.getX() + 3, coordinate.getY() + 3, 2, 2));
            });
//            Arrays.stream(cells).forEach(c1 -> Arrays.stream(c1)
//                    .forEach(c2 -> {
//                        graphics.drawString(String.valueOf(c2.heuristicDistance), (int) c2.getCoordinate().getX(), (int) c2.getCoordinate().getY());
//                    }));
//            path.forEach(coordinate -> graphics.draw(new Ellipse2D.Double(coordinate.getX(), coordinate.getY(), 1, 1)));
        }

        private static class AStarCell extends Cell {
            private boolean visited = false;
            private double heuristicDistance;

            public AStarCell(Cell cell, double heuristicDistance, boolean visited) {
                super(new Coordinate(cell.getCoordinate()), cell.isBusy);
                this.heuristicDistance = heuristicDistance;
                this.visited = visited;
            }

            public AStarCell(Cell cell, double heuristicDistance) {
                super(new Coordinate(cell.getCoordinate()), cell.isBusy);
                this.heuristicDistance = heuristicDistance;
            }

            public void visit(AStarCell prev) {
                heuristicDistance -= 10;
                visited = true;
            }

            protected boolean isVisited() {
                return visited;
            }

            protected double getHeuristicDistance() {
                return heuristicDistance;
            }
        }
    }

    private static class Cell implements Drawable {
        private final UUID id = UUID.randomUUID();
        private final Coordinate coordinate;
        private boolean isBusy;

        public Cell(Coordinate coordinate, boolean isBusy) {
            this.coordinate = coordinate;
            this.isBusy = isBusy;
        }

        public Coordinate getCoordinate() {
            return coordinate;
        }

        public void updateStatus(boolean isBusy) {
            this.isBusy = isBusy;
        }

        protected boolean isBusy() {
            return isBusy;
        }

        protected boolean isNotBusy() {
            return !isBusy();
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public void draw(Graphics2D graphics) {
            float x = (float) coordinate.getX();
            float y = (float) coordinate.getY() + 10;
            if (isBusy) {
                graphics.setColor(Color.RED);
                graphics.drawString("1", x, y);
            } else {
                graphics.setColor(Color.BLACK);
                graphics.drawString("0", x, y);
            }
        }
    }

    private static void refreshFood(Cell[][] cells, Food food) {
        food.updateCoordinate(getNextFoodCoordinate(cells));
    }

    private static Coordinate getNextFoodCoordinate(Cell[][] cells) {
        Random random = new Random();
        Cell[][] freeCells = Arrays.stream(cells)
                .map(r -> Arrays.stream(r)
                        .filter(e -> !e.isBusy)
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

package com.mylllket.inc;

import com.mylllket.inc.interfaces.actions.Drawable;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.*;
import java.util.stream.Stream;

public class AStar implements Drawable {
    private final UUID id = UUID.randomUUID();
    private final AStarCell[][] cells;
    private final LinkedList<Coordinate> path = new LinkedList<>();
    private final Coordinate start;
    private final Coordinate end;
    private int visitedNodes = 0;

    public AStar(Cell[][] cells, Coordinate start, Coordinate end) {
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

    public void update(Cell[][] cells, Coordinate newStart, Coordinate newEnd) {
        start.update(newStart);
        end.update(newEnd);
        path.clear();
        visitedNodes = 0;
        Arrays.stream(cells).flatMap(Arrays::stream).forEach(this::reinitialize);
    }

    private void reinitialize(Cell cell) {
        if (Utils.coordinatesAreEqual(start, end)) {
            return;
        }
        AStarCell aStarCell = getCell(cell.getCoordinate());
        if (!Utils.coordinatesAreEqual(start, aStarCell.getCoordinate())) {
            aStarCell.reset();
        }
        aStarCell.updateStatus(cell.isBusy());
        aStarCell.updateHeuristicDistance(end);
    }

    public void buildPath() {
        if (Utils.coordinatesAreEqual(start, end)) {
            return;
        }
        visitedNodes = 0;
        path.clear();
        while (isMoreToVisit() && !getCell(end).isVisited()) {
            int visitedNodesPrev = visitedNodes;
            AStarCell[][] aStarCells = Arrays.stream(cells)
                    .map(c1 -> Arrays.stream(c1)
                            .filter(AStarCell::isVisited)
                            .toArray(AStarCell[]::new))
                    .toArray(AStarCell[][]::new);
            Arrays.stream(aStarCells)
                    .forEach(c1 -> Arrays.stream(c1)
                            .forEach(c2 -> {
                                int i = (int) (c2.getCoordinate().getX() / 10) - 1;
                                int j = (int) (c2.getCoordinate().getY() / 10) - 1;
                                visit(i - 1, j);
                                visit(i + 1, j);
                                visit(i, j - 1);
                                visit(i, j + 1);
                            }));
            if (visitedNodesPrev == visitedNodes) {
                break;
            }
        }

        Coordinate e = new Coordinate(end);
        path.add(e);
        Coordinate last = new Coordinate(path.getLast());
        while (!Utils.coordinatesAreEqual(last, start)) {
            int i = (int) (last.getX() / 10) - 1;
            int j = (int) (last.getY() / 10) - 1;
            Optional<AStarCell> upNeighbor = getNeighbor(i, j - 1);
            Optional<AStarCell> downNeighbor = getNeighbor(i, j + 1);
            Optional<AStarCell> leftNeighbor = getNeighbor(i - 1, j);
            Optional<AStarCell> rightNeighbor = getNeighbor(i + 1, j);
            int before = path.size();
            Optional<AStarCell> start = Stream.of(upNeighbor, downNeighbor, leftNeighbor, rightNeighbor)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(r -> Utils.coordinatesAreEqual(r.getCoordinate(), this.start))
                    .findFirst();
            if (start.isPresent()) {
                path.add(new Coordinate(start.get().getCoordinate()));
                break;
            }
            Stream.of(upNeighbor, downNeighbor, leftNeighbor, rightNeighbor)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(r -> r.isNotBusy() || Utils.coordinatesAreEqual(r.getCoordinate(), this.start))
                    .filter(AStarCell::isVisited)
                    .filter(r -> path.stream().noneMatch(e1 -> Utils.coordinatesAreEqual(e1, r.getCoordinate())))
                    .min(Comparator.comparingDouble(AStarCell::getHeuristicDistance))
                    .map(Cell::getCoordinate)
                    .ifPresent(coordinate -> path.add(new Coordinate(coordinate)));
            if (path.size() == before) {
                break;
            }
            last = new Coordinate(path.getLast());
        }
    }

    private Optional<AStarCell> getNeighbor(int i, int j) {
        try {
            return Optional.of(cells[i][j]);
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private void visit(int i, int j) {
        try {
            AStarCell cell = cells[i][j];
            boolean visitedBefore = cell.visited;
            if (!cell.isBusy()) {
                cell.visit();
            }
            if (!cell.isBusy() && !visitedBefore) {
                visitedNodes++;
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
            isNotVisited = !cells[i][j].isVisited() || cells[i][j].isBusy();
        } catch (Exception ignored) {
            isNotVisited = false;
        }
        return isNotVisited;
    }

    private AStarCell getCell(Coordinate coordinate) {
        int i = (int) (coordinate.getX() / 10) - 1;
        int j = (int) (coordinate.getY() / 10) - 1;
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
                                graphics.fill(new Ellipse2D.Double(c2.getCoordinate().getX() + 3, c2.getCoordinate().getY() + 3, 4, 4));
                            } else if (Utils.coordinatesAreEqual(end, c2.getCoordinate())) {
                                graphics.setColor(Color.GREEN);
                                graphics.fill(new Ellipse2D.Double(c2.getCoordinate().getX() + 3, c2.getCoordinate().getY() + 3, 4, 4));
                            } else if (c2.isVisited()) {
                                graphics.setColor(Color.BLUE);
                                graphics.fill(new Ellipse2D.Double(c2.getCoordinate().getX() + 3, c2.getCoordinate().getY() + 3, 4, 4));
                            }
                        }));
        path.forEach(coordinate -> {
            graphics.setColor(Color.PINK);
            graphics.fill(new Ellipse2D.Double(coordinate.getX() + 3, coordinate.getY() + 3, 4, 4));
        });
    }

    private static class AStarCell extends Cell {
        private boolean visited = false;
        private double heuristicDistance;

        public AStarCell(Cell cell, double heuristicDistance, boolean visited) {
            super(new Coordinate(cell.getCoordinate()), cell.isBusy());
            this.heuristicDistance = heuristicDistance;
            this.visited = visited;
        }

        public AStarCell(Cell cell, double heuristicDistance) {
            super(new Coordinate(cell.getCoordinate()), cell.isBusy());
            this.heuristicDistance = heuristicDistance;
        }

        public void updateHeuristicDistance(Coordinate coordinate) {
            Coordinate thisCoordinate = this.getCoordinate();
            this.heuristicDistance = Math.abs(thisCoordinate.getX() - coordinate.getX()) + Math.abs(thisCoordinate.getY() - coordinate.getY());
        }

        public void reset() {
            visited = false;
        }

        public void visit() {
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
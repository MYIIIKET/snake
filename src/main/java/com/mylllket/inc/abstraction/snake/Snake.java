package com.mylllket.inc.abstraction.snake;

import com.mylllket.inc.Coordinate;
import com.mylllket.inc.Direction;
import com.mylllket.inc.interfaces.actions.Drawable;
import com.mylllket.inc.interfaces.actions.Movable;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static com.mylllket.inc.Utils.coordinatesAreEqual;

public class Snake implements Movable, Drawable {

    private final UUID id = UUID.randomUUID();
    private final Head head;
    private final List<Segment> body = new LinkedList<>();
    private final List<Food> consumedFood = new LinkedList<>();
    private static final double step = 10;

    public Snake(Head head) {
        this.head = head;
    }

    @Override
    public void move() {
        Coordinate prev = new Coordinate(head.getCoordinate());
        head.updatePosition(step);
        for (Segment segment : body) {
            Coordinate temp = new Coordinate(segment.getCoordinate());
            segment.updateCoordinate(prev);
            prev = new Coordinate(temp);
        }
    }

    @Override
    public void moveUp() {
        updatePosition(Direction.UP);
    }

    @Override
    public void moveDown() {
        updatePosition(Direction.DOWN);
    }

    @Override
    public void moveLeft() {
        updatePosition(Direction.LEFT);
    }

    @Override
    public void moveRight() {
        updatePosition(Direction.RIGHT);
    }

    private void updatePosition(Direction direction) {
        head.updateDirection(direction);
    }

    public boolean consume(Food food) {
        if (coordinatesAreEqual(head, food)) {
            return consumedFood.add(food);
        }
        return false;
    }

    public void growTail() {
        addTail();
        prepareTail();
    }

    private void prepareTail() {
        if (body.size() > 0) {
            Segment tail = body.get(body.size() - 1);
            consumedFood.stream()
                    .filter(food -> coordinatesAreEqual(tail, food))
                    .findFirst()
                    .ifPresent(Food::process);
        } else {
            consumedFood.stream().findFirst().ifPresent(Food::process);
        }
    }

    private void addTail() {
        consumedFood.stream()
                .filter(Food::isProcessed)
                .findFirst()
                .ifPresent(food -> {
                    Coordinate coordinate = new Coordinate(food.getCoordinate());
                    Segment segment = new Head(coordinate);
                    body.add(segment);
//                    segment.updatePosition(-step);
                });
        consumedFood.removeIf(Food::isProcessed);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void draw(Graphics2D graphics) {
        head.draw(graphics);
        body.forEach(segment -> segment.draw(graphics));
    }
}

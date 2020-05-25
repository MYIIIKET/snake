package com.mylllket.inc.abstraction.snake;

import com.mylllket.inc.Direction;
import com.mylllket.inc.interfaces.actions.Drawable;
import com.mylllket.inc.interfaces.actions.Movable;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import static com.mylllket.inc.Utils.coordinatesAreEqual;

public class Snake implements Movable, Drawable {

    private final Head head;
    private final List<Segment> body = new LinkedList<>();
    private final List<Food> consumedFood = new LinkedList<>();
    private static final double step = 5;

    public Snake(Head head) {
        this.head = head;
        body.add(head);
    }

    @Override
    public void move() {
        body.forEach(segment -> segment.updatePosition(step));
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
        for (Segment segment : body) {
            Direction directionBeforeUpdate = segment.getDirection();
            segment.updateDirection(direction);
            direction = directionBeforeUpdate;
        }
    }

    public void consume(Food food) {
        if (coordinatesAreEqual(head, food)) {
            consumedFood.add(food);
        }
    }

    public void growTail() {
        addTail();
        prepareTail();
    }

    private void prepareTail() {
        Segment tail = body.get(body.size() - 1);
        consumedFood.stream()
                .filter(food -> coordinatesAreEqual(tail, food))
                .findFirst()
                .ifPresent(food -> food.updateDirection(tail.getDirection()));
    }

    private void addTail() {
        final Predicate<Segment> foodIsNotIdle = food -> !food.getDirection().equals(Direction.IDLE);
        consumedFood.stream()
                .filter(foodIsNotIdle)
                .findFirst()
                .ifPresent(body::add);
        consumedFood.removeIf(foodIsNotIdle);
    }

    @Override
    public void draw(Graphics2D graphics) {
        body.forEach(segment -> segment.draw(graphics));
    }
}

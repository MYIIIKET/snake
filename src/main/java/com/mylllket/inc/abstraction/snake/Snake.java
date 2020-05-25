package com.mylllket.inc.abstraction.snake;

import com.mylllket.inc.Coordinate;
import com.mylllket.inc.Direction;
import com.mylllket.inc.Size;
import com.mylllket.inc.interfaces.actions.Drawable;
import com.mylllket.inc.interfaces.actions.Movable;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static com.mylllket.inc.Utils.coordinatesAreEqual;

public class Snake implements Movable, Drawable {

    private final UUID id = UUID.randomUUID();
    private final Head head;
    private final LinkedList<Segment> body = new LinkedList<>();
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
            return consumedFood.add(new Food(new Coordinate(food.getCoordinate())));
        }
        return false;
    }

    public void growTail() {
        addTail();
        prepareTail();
    }

    private void prepareTail() {
        consumedFood.stream()
                .filter(Food::isNotProcessed)
                .findFirst()
                .ifPresent(Food::process);
    }

    private void addTail() {
        if (body.size() > 0) {
            Segment tail = body.getLast();
            Predicate<Food> foodPredicate = food -> food.isProcessed() && coordinatesAreEqual(tail, food);
            consumedFood.stream()
                    .filter(foodPredicate)
                    .findFirst()
                    .ifPresent(food -> {
                        Segment segment = new Segment(new Size(10, 10), Color.GREEN,
                                new Coordinate(new Coordinate(food.getCoordinate())));
                        body.add(segment);
                    });
            consumedFood.removeIf(foodPredicate);
        } else {
            consumedFood.stream()
                    .filter(Food::isProcessed)
                    .findFirst()
                    .ifPresent(food -> {
                        Segment segment = new Segment(new Size(10, 10), Color.GREEN,
                                new Coordinate(new Coordinate(food.getCoordinate())));
                        body.add(segment);
                    });
            consumedFood.removeIf(Food::isProcessed);
        }
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

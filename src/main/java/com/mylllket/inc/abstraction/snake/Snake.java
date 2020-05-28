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
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(70, 80)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(70, 90)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(70, 100)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(70, 110)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(70, 120)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(70, 130)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(70, 140)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(60, 140)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(50, 140)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(40, 140)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(30, 140)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(20, 140)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(20, 130)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(20, 120)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(20, 110)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(20, 100)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(30, 100)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(40, 100)));
        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(50, 100)));
//        body.add(new Segment(new Size(10, 10), Color.GREEN, new Coordinate(60, 100)));
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
        if (head.getDirection() != direction.getOpposite()) {
            head.updateDirection(direction);
        }
    }

    public boolean consume(Food food) {
        if (canConsume(food)) {
            return consumedFood.add(new Food(new Coordinate(food.getCoordinate())));
        }
        return false;
    }

    public boolean canConsume(Food food) {
        return coordinatesAreEqual(head, food);
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
        final Predicate<Food> foodPredicate = getFoodPredicate();
        addTail(foodPredicate);
        consumedFood.removeIf(foodPredicate);
    }

    private Predicate<Food> getFoodPredicate() {
        final Predicate<Food> foodPredicate;
        if (body.size() > 0) {
            Segment tail = body.getLast();
            foodPredicate = food -> food.isProcessed() && coordinatesAreEqual(tail, food);
        } else {
            foodPredicate = Food::isProcessed;
        }
        return foodPredicate;
    }

    private void addTail(Predicate<Food> foodPredicate) {
        consumedFood.stream()
                .filter(foodPredicate)
                .findFirst()
                .ifPresent(this::addTail);
    }

    private void addTail(Food food) {
        Segment segment = new Segment(new Size(10, 10), Color.GREEN,
                new Coordinate(new Coordinate(food.getCoordinate())));
        body.add(segment);
    }

    public Coordinate getHeadCoordinate() {
        return head.getCoordinate();
    }

    public Coordinate getTailCoordinate() {
        return getTail().getCoordinate();
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

    private Segment getTail() {
        return body.size() > 0 ? body.getLast() : head;
    }

    public boolean isNotValid() {
        return body.stream().anyMatch(segment -> coordinatesAreEqual(segment, head));
    }

    public boolean clashesWith(Coordinate coordinate) {
        return coordinatesAreEqual(coordinate, head.getCoordinate())
                || body.stream().anyMatch(segment -> coordinatesAreEqual(coordinate, segment.getCoordinate()))
                || consumedFood.stream().anyMatch(food -> coordinatesAreEqual(coordinate, food.getCoordinate()));
    }
}

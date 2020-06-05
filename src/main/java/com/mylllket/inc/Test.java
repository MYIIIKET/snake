package com.mylllket.inc;

import com.mylllket.inc.abstraction.snake.Food;
import com.mylllket.inc.abstraction.snake.Head;
import com.mylllket.inc.abstraction.snake.Snake;
import com.mylllket.inc.neural_network.NeuralNetwork;
import com.mylllket.inc.utils.Utils;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test {
    public static void main(String[] args) {
        int neuralNetworksNumber = 2;
        Map<UUID, GameStructure> gamesMap = IntStream.range(0, neuralNetworksNumber)
                .mapToObj($ -> {
                    Snake snake = new Snake(new Head(new Coordinate(150, 150), Direction.random()));
                    Border border = new Border(new Size(100, 100), new Coordinate(100, 100));
                    Cell[][] cells = Utils.toArray(border, snake);
                    Food food = new Food(Utils.getNextFoodCoordinate(cells));
                    Utils.refreshFood(snake, cells, food);
                    AStar aStar = new AStar(cells,
                            new Coordinate(snake.getHeadCoordinate()), new Coordinate(snake.getTailCoordinate()),
                            border.getCoordinate().getX(), border.getCoordinate().getY(), food);
                    return new GameStructure(snake, border, cells, food, aStar, new NeuralNetwork(5, 5, 2));
                })
                .collect(Collectors.toMap(GameStructure::getId, Function.identity()));

        int iterations = 100;
        IntStream.range(0, iterations)
                .forEach(generation -> {
                    System.out.printf("Generation: %s%n", generation);
                    play(gamesMap);
                    List<GameStructure> collect = gamesMap.values()
                            .stream()
                            .sorted(Comparator.comparing(GameStructure::getIterations).reversed())
                            .collect(Collectors.toList());
                    GameStructure first = collect.get(0);
                    GameStructure second = collect.get(1);
                    NeuralNetwork crossed = gamesMap.get(first.getId()).getNeuralNetwork().cross(gamesMap.get(second.getId()).getNeuralNetwork());
                    System.out.printf("score: [%s, %s]%n", first.iterations, second.iterations);
                    if (generation != iterations - 1) {
                        gamesMap.values().forEach(game -> game.replaceNetwork(crossed));
                        gamesMap.values().forEach(GameStructure::reinitialize);
                    }
                });
        GameStructure game = gamesMap.values().stream()
                .sorted(Comparator.comparing(GameStructure::getIterations).reversed())
                .findFirst().get();
        game.reinitialize();
        Window window = new Window(500, 500);
        Field field = new Field();
        window.add(field);
        field.add(game.snake);
        field.add(game.border);
        field.add(game.food);
        Runnable runnable = () -> {
            int score = 0;
            do {
                field.repaint();
                play(game);
                game.iterate();
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!Utils.gameIsOver(game.snake, game.border));
            System.out.println("Game is over");
            System.out.println(score);
        };
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }

    private static void play(Map<UUID, GameStructure> gamesMap) {
        int localIterations = 0;
        System.out.printf("Non finished games: %s%n", gamesMap.values().stream().filter(r -> !r.finished).count());
        while (gamesMap.values().stream().noneMatch(r -> r.finished) && localIterations < 100) {
            Collection<GameStructure> games = gamesMap.values();
            games.stream()
                    .filter(game -> !game.finished)
                    .peek(Test::play)
                    .forEach(GameStructure::iterate);
            localIterations++;
        }
    }

    private static void play(GameStructure game) {
        double[] input = Utils.collectInput(game.snake, game.cells, game.food, game.aStar);
        double[] output = game.getNeuralNetwork().feedForward(input);
        switch (game.snake.getDirection()) {
            case UP:
                if ((int) output[0] == 0 && (int) output[1] == 0) {
                    game.snake.moveLeft();
                } else if ((int) output[0] == 0 && (int) output[1] == 1) {
                    game.snake.moveRight();
                }
                break;
            case DOWN:
                if ((int) output[0] == 0 && (int) output[1] == 0) {
                    game.snake.moveRight();
                } else if ((int) output[0] == 0 && (int) output[1] == 1) {
                    game.snake.moveLeft();
                }
                break;
            case LEFT:
                if ((int) output[0] == 0 && (int) output[1] == 0) {
                    game.snake.moveDown();
                } else if ((int) output[0] == 0 && (int) output[1] == 1) {
                    game.snake.moveUp();
                }
                break;
            case RIGHT:
                if ((int) output[0] == 0 && (int) output[1] == 0) {
                    game.snake.moveUp();
                } else if ((int) output[0] == 0 && (int) output[1] == 1) {
                    game.snake.moveDown();
                }
                break;
        }
    }

    @Getter
    public static class GameStructure {
        public final UUID id = UUID.randomUUID();
        public Snake snake;
        public Border border;
        public Cell[][] cells;
        public Food food;
        public AStar aStar;
        public NeuralNetwork neuralNetwork;
        public boolean finished = false;

        private int iterations = 0;

        public GameStructure(Snake snake, Border border, Cell[][] cells, Food food, AStar aStar, NeuralNetwork neuralNetwork) {
            this.snake = snake;
            this.border = border;
            this.cells = cells;
            this.food = food;
            this.aStar = aStar;
            this.neuralNetwork = neuralNetwork;
        }

        public void reinitialize() {
            snake = new Snake(new Head(new Coordinate(150, 150), Direction.random()));
            border = new Border(new Size(100, 100), new Coordinate(100, 100));
            cells = Utils.toArray(border, snake);
            food = new Food(Utils.getNextFoodCoordinate(cells));
            Utils.refreshFood(snake, cells, food);
            aStar = new AStar(cells,
                    new Coordinate(snake.getHeadCoordinate()), new Coordinate(snake.getTailCoordinate()),
                    border.getCoordinate().getX(), border.getCoordinate().getY(), food);
            finished = false;
            iterations = 0;
        }

        public void replaceNetwork(NeuralNetwork neuralNetwork) {
            this.neuralNetwork = neuralNetwork.mutate();
        }

        public double computeScore() {
            return (1 / (1 + Math.exp(-iterations))) - 0.5;
        }

        public void iterate() {
            snake.move();
            if (snake.consume(food)) {
                try {
                    Utils.refreshFood(snake, cells, food);
                } catch (UnsupportedOperationException e) {
                    System.out.println("You win");
                    return;
                }
            }
            Utils.refresh(cells, snake, Optional.of(food));
            snake.growTail();
            if (Utils.gameIsOver(snake, border)) {
                finished = true;
                return;
            }
            iterations++;
        }
    }

}

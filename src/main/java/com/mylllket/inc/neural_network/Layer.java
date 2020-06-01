package com.mylllket.inc.neural_network;

import org.apache.commons.lang3.RandomUtils;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Layer {
    public int size;
    public double[] neurons;
    public double[] biases;
    public double[][] weights;

    public Layer(int size, int nextSize) {
        this.size = size;
        neurons = new double[size];
        biases = new double[size];
        weights = new double[size][nextSize];
    }

    public Layer(Layer layer) {
        this.size = layer.size;
        this.neurons = Arrays.stream(layer.neurons).toArray();
        this.biases = Arrays.stream(layer.biases).toArray();
        this.weights = Arrays.stream(layer.weights)
                .map(r -> Arrays.stream(r).toArray())
                .toArray(double[][]::new);
    }

    public Layer cross(Layer layer) {
        cross(biases, layer.biases);

        boolean b = RandomUtils.nextBoolean();
        if (b) {
            IntStream.range(0, weights.length / 2)
                    .forEach(index -> weights[index] = Arrays.stream(Arrays.stream(layer.weights[index])
                            .map(this::mutate)
                            .toArray()).toArray());
        } else {
            IntStream.range(weights.length / 2, weights.length)
                    .forEach(index -> weights[index] = Arrays.stream(Arrays.stream(layer.weights[index])
                            .map(this::mutate)
                            .toArray()).toArray());
        }

        return new Layer(this);
    }

    private void cross(double[] values, double[] data) {
        int length = values.length;
        int half = length / 2;
        boolean b = RandomUtils.nextBoolean();
        if (b) {
            IntStream.range(0, half).forEach(index -> values[index] = data[index]);
        } else {
            IntStream.range(half, length).forEach(index -> values[index] = data[index]);
        }
        IntStream.range(0, length).forEach(index -> values[index] = mutate(values[index]));
    }

    public void mutate() {
        IntStream.range(0, biases.length).forEach(index -> this.biases[index] = mutate(this.biases[index]));
        IntStream.range(0, weights.length)
                .forEach(index -> weights[index] = Arrays.stream(Arrays.stream(weights[index])
                        .map(this::mutate)
                        .toArray()).toArray());
    }

    private double mutate(double value) {
        boolean needMutate = RandomUtils.nextBoolean();
        if (!needMutate) {
            return value;
        }
        boolean b = RandomUtils.nextBoolean();
        double delta = RandomUtils.nextDouble(0, 1);
        if (b) {
            return value + delta;
        } else {
            return value - delta;
        }
    }
}

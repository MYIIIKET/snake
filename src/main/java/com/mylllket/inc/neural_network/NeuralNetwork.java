package com.mylllket.inc.neural_network;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public class NeuralNetwork {
    private final UUID id = UUID.randomUUID();
    private Layer[] layers;
    private final UnaryOperator<Double> activation = x -> 1 / (1 + Math.exp(-x));

    public NeuralNetwork(int... sizes) {
        layers = new Layer[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            int nextSize = 0;
            if (i < sizes.length - 1) nextSize = sizes[i + 1];
            layers[i] = new Layer(sizes[i], nextSize);
            for (int j = 0; j < sizes[i]; j++) {
                layers[i].biases[j] = Math.random() * 2.0 - 1.0;
                for (int k = 0; k < nextSize; k++) {
                    layers[i].weights[j][k] = Math.random() * 2.0 - 1.0;
                }
            }
        }
    }

    public double[] feedForward(double... inputs) {
        System.arraycopy(inputs, 0, layers[0].neurons, 0, inputs.length);
        for (int i = 1; i < layers.length; i++) {
            Layer l = layers[i - 1];
            Layer l1 = layers[i];
            for (int j = 0; j < l1.size; j++) {
                l1.neurons[j] = 0;
                for (int k = 0; k < l.size; k++) {
                    l1.neurons[j] += l.neurons[k] * l.weights[k][j];
                }
                l1.neurons[j] += l1.biases[j];
                l1.neurons[j] = activation.apply(l1.neurons[j]);
            }
        }
        return Arrays.stream(layers[layers.length - 1].neurons)
                .map(r -> r > 0.5 ? 1 : 0)
//                .map(activationOut::apply)
                .toArray();
    }

    public NeuralNetwork(NeuralNetwork neuralNetwork) {
        this.layers = Arrays.stream(neuralNetwork.layers)
                .map(Layer::new)
                .toArray(Layer[]::new);
    }

    public NeuralNetwork(Layer[] layers) {
        this.layers = layers;
    }

    public NeuralNetwork cross(NeuralNetwork neuralNetwork) {
        Layer[] layers = neuralNetwork.layers;
        int length = layers.length;
        Layer[] crossedLayers = IntStream.range(0, length)
                .mapToObj(index -> this.layers[index].cross(layers[index]))
                .toArray(Layer[]::new);
        return new NeuralNetwork(crossedLayers);
    }

    public UUID getId() {
        return id;
    }

    public NeuralNetwork mutate() {
        Arrays.stream(layers).forEach(Layer::mutate);
        return this;
    }
}

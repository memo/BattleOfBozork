package battle.controllers.Piers;

import asteroids.Action;
import battle.BattleController;
import battle.SimpleBattle;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by pwillic on 11/06/2015.
 * <p>
 * Features to use for avoidance
 * <p>
 * 1. Distance to enemy - 0 very close, 1 very far
 * 2. Enemy in FL - 0 no, 1 yes
 * 3. Enemy in FR
 * 4. Enemy in BL
 * 5. Enemy in BR
 * <p>
 * Features to use for shooting
 * 1. Distance to enemy
 * 2. Deviance from dead straight Left - 0.5 no deviance, 0 full left, 1 full right
 * 3. Missiles Remaining
 * <p>
 * Features to use for aligning
 * 1. Distance to enemy
 * 2. Deviance from enemy
 * 3.
 */
public class PiersController implements BattleController {
    Neuron tinyBrain;
    int fitness;

    public PiersController() {
        tinyBrain = new Neuron(10);
    }

    public static void main(String[] args) {
        int POPULATION_COUNT = 20;
        int TERMINATION_SCORE = 500;

        // Learn the controllers

        SimpleBattle battle = new SimpleBattle();

        ArrayList<PiersController> candidates = new ArrayList<>(POPULATION_COUNT);
        for (int i = 0; i < POPULATION_COUNT; i++) candidates.add(new PiersController());

        // Run the GA?
        PiersController best = candidates.get(0);

        while (best.fitness < TERMINATION_SCORE){

        }
    }

    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId) {

        double[] avoidanceTable = new double[5];
        double[] shootingTable = new double[3];

        return null;
    }
}

class Neuron {
    private static Random random = new Random();
    int numberOfInputs;
    double[] neuronWeights;

    // Random neuron
    public Neuron(int numberOfInputs) {
        this.numberOfInputs = numberOfInputs;
        this.neuronWeights = new double[numberOfInputs];
        for (int i = 0; i < neuronWeights.length; i++) {
            neuronWeights[i] = random.nextDouble();
        }
    }

    // clone a neuron / make one
    public Neuron(double[] neuronWeights) {
        this.numberOfInputs = neuronWeights.length;
        this.neuronWeights = neuronWeights;
    }

    public double think(double[] inputs) {
        if (inputs.length != numberOfInputs)
            throw new IllegalArgumentException("Wrong number of inputs" + inputs.length + " - should be: " + numberOfInputs);
        double totalSum = 0;
        for (int index = 0; index < numberOfInputs; index++) {
            totalSum += (inputs[index] * neuronWeights[index]);
        }
        return totalSum;
    }
}
package ga;

import battle.BattleController;
import battle.SimpleBattle;
import battle.controllers.Memo.MemoController1;
import battle.controllers.mmmcts.MMMCTS;

import java.util.Arrays;
import java.util.Random;

public class SimpleRandomHillClimber {

    // Random mutation hill climber
    // using a relative (hence co-evolutionary)
    // fitness function
    static Random random = new Random();

    double[] bestYet;
    Eval2 eval;

    double stepFac = 0.05;

    // set stepAdjust to 1.0 to keep the stepFac fixed

    // on quadratic bowl 1.01 works much better than 1.0
    // which increases the step size every time a mutation
    // is successful and decreases it every time it fails
    // to improve
    // but be careful: could it make things worse in some cases?
    double stepAdjust = 1.01;

    // example ready for a minimal version of
    // co-evolution

    public static void main(String[] args) {
        int nEvals = 100;

        SimpleRandomHillClimber evo = new SimpleRandomHillClimber(MemoController1.getFeatures(), new MemoEval2());

        evo.run(nEvals);

        System.out.println("Best Found: " + Arrays.toString(evo.bestYet));
        System.out.println("Fitness: " + mag2(evo.bestYet));

    }

    public SimpleRandomHillClimber(double[] bestYet, Eval2 eval) {
        this.bestYet = bestYet;
        this.eval = eval;
    }

    public void run(int nEvals) {
        for (int i=0; i<nEvals; i++) {
            // randomly mutate the best yet
            double[] mut = randMut(bestYet, stepFac);

           // System.out.println(i + "\t " + mag2(bestYet) + "\t" + Arrays.toString(bestYet) + "\t" + Arrays.toString(mut));
            double diff = eval.pointsDiff(bestYet, mut);

            // if it's better then adopt the mutation as the new best
            if (diff >= 0) {
                bestYet = mut;
                // try making it bigger - make even faster progress
            } else {
                // try making the step size smaller
                stepFac /= stepAdjust;
            }
            System.out.println(i + "\t " + mag2(bestYet));
        }
    }

    // to evolve a game playing agent inject a and b into
    // game agent controllers and return the points difference
    // ensure to interpret the sign correctly
    // the example is set up to MINIMIZE a fitness score

    static interface Eval2 {
        double pointsDiff(double[] a, double[] b);
    }

    static double[] randVec(int n) {
        double[] x = new double[n];
        for (int i=0; i<n; i++) {
            x[i] = random.nextGaussian();
        }
        return x;
    }

    static double[] randMut(double[] v, double stepFac) {
        int n = v.length;
        double[] x = new double[n];
        for (int i=0; i<n; i++) {
            x[i] = v[i] + stepFac * random.nextGaussian();
        }
        return x;
    }

    static class QuadraticBowl implements Eval2 {

        @Override
        public double pointsDiff(double[] a, double[] b) {
            // simple example that evaluates quality as being
            // the minimum squared magnitude of a vector
            return mag2(a) - mag2(b);
        }
    }

    static double mag2(double[] v) {
        // square of the magnitude of the vector
        double tot = 0;
        for (double x : v) tot += x * x;
        return tot;
    }


    /**
     * Created by Memo Akten on 12/06/2015.
     */
    static class MemoEval2 implements Eval2 {

        // returns whether or not I won
        int playWith(double[] v) {
            System.out.print("Playing with: " + Arrays.toString(v));
            SimpleBattle battle = new SimpleBattle();
            BattleController player1 = new MemoController1();
            MemoController1.setFeatures(v);

            BattleController player2 = new MMMCTS();
            battle.playGame(player1, player2);

            int score = battle.getPoints(0) - battle.getPoints(1);
            System.out.println(" | Score: " + score);
            return score;
        }

        @Override
        public double pointsDiff(double[] a, double[] b) {
            int num_games = 3;
            int wins_a = 0, wins_b = 0;
            for(int i=0; i<num_games; i++) {
                wins_a += playWith(a);
                wins_b += playWith(b);
            }

            return wins_a > wins_b ? -1 : 1;
        }
    }
}

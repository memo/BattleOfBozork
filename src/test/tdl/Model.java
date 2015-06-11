package test.tdl;


import utilities.JEasyFrame;
import utilities.StatSummary;

import java.util.Random;
import java.util.Arrays;
import java.util.Scanner;
import java.io.FileReader;


public class Model {

    public double gamma = 1.0;
    public double lambda = 0.5;

    // alpha is the learning rate
    public double alpha = 0.1;
    public int nSuccess = 0;
    public int nSteps = 0;

    public int size = 15;
    public double epsilon = 0.1;
    double[] steps;
    double[] err;
    // this field is set and used to control
    // animation delay (to make it slow at first then speed up)
    int iteration;

    // rewards for non-goal, goal state
    public double[] rewards = {-1, 0};

    public int[][] maze;

    public StatSummary run(int nEpisodes) throws Exception {
        StatSummary ss = new StatSummary();
        steps = new double[nEpisodes];
        err = new double[nEpisodes];
        for (int i = 0; i < nEpisodes; i++) {
            iteration = i;
            double res = episode();
            steps[i] = res;
            err[i] = valDiff();
            ss.add(res);
            // System.out.println(i + "\t " + res);
            if (view != null) {
                Thread.sleep(delay(10));
            }
        }
        return ss;
    }

    public void makeMaze() {
        maze = new int[size][size];
        // try and read in a file
        try {
            FileReader f = new FileReader("c:/multimod/tdl/data/maze1.txt");
            Scanner sc = new Scanner(f);

            for (int i=0; i<size; i++) {
                String s = sc.nextLine();
                for (int j=0; j<s.length(); j++) {
                    if (s.charAt(j) == 'M') maze[j][i] = 1;
                }
            }
        } catch(Exception e) {
            System.out.println("Unable to find maze file");
            e.printStackTrace();
        }
    }

    private double valDiff() {
        double err = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                err += Math.abs(v[i][j] +
                        (Math.abs(i - goal[0]) + Math.abs(j - goal[1])));
            }
        }
        return err / (size * size);
    }

    public void selfEval() throws Exception {
        int[] state = new int[2];
        StatSummary ss = new StatSummary();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                state[0] = i;
                state[1] = j;
                int cost = episode(state);
                ss.add(cost);
                // System.out.format("%d, %d, -> %d (v = %2.2f)\n", i, j, cost, v[i][j]);
            }
        }
        System.out.println(ss);
    }


    public void resetValues() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                v[i][j] = 0;
            }
        }
        nSteps = 0;
        nSuccess = 0;
    }

    long delay(int i) throws Exception {
        return (i > 2 ? 50 : 200);
    }

    public void makeView() {
        view = new View(this, v);
        new JEasyFrame(view, "Grid View");
        eview = new View(this, e);
        JEasyFrame el = new JEasyFrame(eview, "Eligibility");
        el.setLocation(460, 0);
    }

    static Random r = new Random();

    static int defaultSize = 15;
    // value table
    public double[][] v;

    // eligibility trace
    double[][] e;
    public int[] state;
    View view, eview;
    int maxIts = 2 * size * size;
    public int[] goal;

    //    int[] state;
    int[][] actions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};


    public Model() {
        this(defaultSize);
    }

    public Model(int size) {
        this.size = size;
        v = new double[size][size];
        e = new double[size][size];
        state = new int[2];
        goal = new int[]{size / 2, size / 2};
        // cheat();
    }

    public int episode() throws Exception {
        return episode(randState());
    }

    public int episode(int[] state) throws Exception {
        // run an episode for a maximum number of iterations
        // int[] nextState;
        // copyState(state, prevState);
        this.state = state;
        // copyState(state, this.state);
        resetTrace();
        for (int i = 0; i < maxIts; i++) {
            if (atGoal(state)) {
                nSuccess++;
                return i;
            }
            int[] action = getAction(state);
            int[] nextState = act(state, action);
            nSteps++;
            updateView();
            double reward = atGoal(state) ? rewards[1] : rewards[0];
            double delta = reward + gamma * value(nextState) - value(state);
            // System.out.println(value(prevState) + "\t " + value(state));
            // update accumulating eligibility trace
            e[state[0]][state[1]]++;
            // updateValue(prevState, delta);
            // now update the states and traces
            // updateVE();
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    v[j][k] += alpha * delta * e[j][k];
                    e[j][k] = e[j][k] * gamma * lambda;
                }
            }
            copyState(nextState, state);
        }
        return 2 * maxIts;
    }

    protected void resetTrace() {
        for (int j = 0; j < size; j++) {
            for (int k = 0; k < size; k++) {
                e[j][k] = 0;
            }
        }

    }

//    public void updateValue(int[] s, double delta) {
//        // v[s[0]][s[1]] += rate * delta;
//    }

    //
    private void updateView() throws Exception {
        if (view != null) {
            view.repaint();
            eview.repaint();
            // do the first iterations nice and slow
            if (iteration <= 2) Thread.sleep(100);
        }
    }

    protected void copyState(int[] a, int[] b) {
        b[0] = a[0];
        b[1] = a[1];
    }

    public boolean atGoal(int[] s) {
        return Arrays.equals(s, goal);
    }

    public int[] randState() {
        int[] s = new int[]{r.nextInt(size), r.nextInt(size)};
        if (legal(s)) return s;
        else return randState();
    }

    public int[] getAction(int[] state) {
        // consider all states that this action could lead to
        if (r.nextDouble() < epsilon) {
            return randLegalAction(state);
        }
        int best = 0;
        double bestScore = eval(state, actions[0]);
        for (int i = 1; i < actions.length; i++) {
            // apply the action and see what happens
            double score = eval(state, actions[i]);
            if (score > bestScore) {
                bestScore = score;
                best = i;
            }
        }
        return actions[best];
    }

    public int[] randLegalAction(int[] s) {
        int[] act = actions[r.nextInt(actions.length)];
        int[] next = act(s, act);
        if (legal(next)) {
            return act;
        } else {
            return randLegalAction(s);
        }
    }

    public double eval(int[] s, int[] a) {
        int[] next = act(s, a);
        if (!legal(next)) {
            // System.out.println("Illegal move attempt");
            return Double.NEGATIVE_INFINITY;
        }
        double score = r.nextDouble() * 0.0001 + value(next);
        // System.out.println("Score: " + score);
        return score;
    }

    public boolean legal(int[] s) {
        return maze == null || maze[s[0]][s[1]] == 0;
    }

    public boolean legal(int i, int j) {
        return maze == null || maze[i][j] == 0;
    }

    public double value(int[] s) {
        if (atGoal(s)) {
            return 1;
        } else {
            return v[s[0]][s[1]];
        }
    }

    public int[] act(int[] s, int[] a) {
        int[] ns = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            ns[i] = ((s[i] + a[i]) + size) % size;
        }
        return ns;
    }

    public void cheat() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                v[i][j] = -(Math.abs(i - goal[0]) + Math.abs(j - goal[1]));
            }
        }
    }
}

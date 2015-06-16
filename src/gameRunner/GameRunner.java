package gameRunner;

import battle.BattleController;
import battle.SimpleBattle;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Class for running multiple games and logging the results
 * <p>
 * Created by pwillic on 16/06/2015.
 */
public class GameRunner {


    private static ExecutorService threadPool;
    private int NUMBER_OF_THREADS = 4;
    private ArrayList<Callable<Object>> runnerThreads;
    private ArrayList<BattleController> controllers;

    public GameRunner(int numberOfThreads, ArrayList<BattleController> controllers) {
        this.NUMBER_OF_THREADS = numberOfThreads;
        this.controllers = controllers;
    }

}

class GameRunnerWrapper implements Callable<Object> {
    SimpleBattle game;
    BattleController p1;
    BattleController p2;

    public GameRunnerWrapper(SimpleBattle game, BattleController p1, BattleController p2) {
        this.game = game;
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public Object call() throws Exception {
        game.playGame(p1, p2);
        return null;
    }
}

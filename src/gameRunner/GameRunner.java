package gameRunner;

import battle.BattleController;
import battle.SimpleBattle;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private int gamesPerMatchup = 10;

    public GameRunner(int numberOfThreads, ArrayList<BattleController> controllers, int gamesPerMatchup) {
        this.NUMBER_OF_THREADS = numberOfThreads;
        this.controllers = controllers;
        this.gamesPerMatchup = gamesPerMatchup;
        if(threadPool == null){
            threadPool = Executors.newCachedThreadPool();
        }
    }

    public void runTheGames() {
        for(BattleController p1 : controllers){
            for(BattleController p2 : controllers){
                if(p1 != p2){
                    runnerThreads.add(new GameRunnerWrapper(new SimpleBattle(false), p1, p2));
                }
            }
        }
        try {
            threadPool.invokeAll(runnerThreads);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

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
